package esprit.tn.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OCRService {

    public interface ProgressCallback {
        void onProgress(double progress);
    }

    private static final String OCR_SPACE_API_KEY = "K89808668988957\n";
    private static final String OCR_SPACE_API_URL = "https://api.ocr.space/parse/image";

    /**
     * Extract text from a PDF file using PDFBox directly
     * This is faster and more reliable for normal PDFs
     */
    public String extractTextFromPDF(String text, ProgressCallback callback) {
        // If the input is a file path, process it as a PDF
        if (text.toLowerCase().endsWith(".pdf")) {
            return extractTextFromPDFFile(text, callback);
        }
        // Otherwise, return the text as-is
        callback.onProgress(1.0);
        return text;
    }

    private String extractTextFromPDFFile(String pdfFilePath, ProgressCallback callback) {
        callback.onProgress(0.1);
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            callback.onProgress(0.3);
            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText(document);
            
            callback.onProgress(0.6);
            // If text extraction was successful and results aren't too short, return results
            if (text != null && text.trim().length() > 50) {
                callback.onProgress(1.0);
                return text;
            }
            
            callback.onProgress(0.7);
            // If text extraction didn't yield good results, try OCR as a fallback
            return extractTextUsingOCR(pdfFilePath, callback);
        } catch (IOException e) {
            System.err.println("Error extracting text from PDF: " + e.getMessage());
            try {
                // Try OCR as fallback
                return extractTextUsingOCR(pdfFilePath, callback);
            } catch (Exception ex) {
                System.err.println("OCR fallback also failed: " + ex.getMessage());
                return "Failed to extract text from document.";
            }
        }
    }

    /**
     * Extract text using OCR Space API as fallback for scanned PDFs
     */
    private String extractTextUsingOCR(String filePath, ProgressCallback callback) throws IOException {
        // Convert PDF to Base64 for API submission
        File file = new File(filePath);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        String fileBase64 = Base64.getEncoder().encodeToString(fileContent);

        // Set up connection
        URL url = new URL(OCR_SPACE_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Build request parameters
        StringBuilder postData = new StringBuilder();
        append(postData, "apikey", OCR_SPACE_API_KEY);
        append(postData, "base64Image", "data:application/pdf;base64," + fileBase64);
        append(postData, "language", "fre"); // French language
        append(postData, "isOverlayRequired", "false");

        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Get response
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                
                // Extract text from JSON response
                String jsonResponse = response.toString();
                Pattern pattern = Pattern.compile("\"ParsedText\":\"(.*?)\"");
                Matcher matcher = pattern.matcher(jsonResponse);
                
                if (matcher.find()) {
                    String extractedText = matcher.group(1)
                            .replace("\\r\\n", "\n")
                            .replace("\\\"", "\"");
                    return extractedText;
                }
            }
        } else {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.err.println("OCR API error: " + response);
            }
        }
        
        return "OCR text extraction failed.";
    }

    /**
     * Helper method to append URL encoded parameters
     */
    private void append(StringBuilder postData, String key, String value) throws UnsupportedEncodingException {
        if (postData.length() != 0) {
            postData.append("&");
        }
        postData.append(URLEncoder.encode(key, StandardCharsets.UTF_8.toString()));
        postData.append("=");
        postData.append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
    }
}
