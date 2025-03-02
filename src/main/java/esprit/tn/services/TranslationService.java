package esprit.tn.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationService {

    public interface ProgressCallback {
        void onProgress(double progress);
    }

    private static final String LIBRE_TRANSLATE_URL = "https://translate.fedilab.app/translate";
    
    /**
     * Translate text from source language to target language
     * @param text Text to translate
     * @param sourceLanguage Source language code (e.g., "fr" for French)
     * @param targetLanguage Target language code (e.g., "en" for English)
     * @param callback Progress callback
     * @return Translated text or original text if translation fails
     */
    public String translate(String text, String sourceLanguage, String targetLanguage, ProgressCallback callback) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        // Limit text to avoid API overload (most free APIs have size limits)
        String limitedText = text;
        if (text.length() > 5000) {
            limitedText = text.substring(0, 5000) + "...";
        }
        
        try {
            callback.onProgress(0.2);
            URL url = new URL(LIBRE_TRANSLATE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            
            // Construct JSON request body
            String jsonInputString = String.format(
                "{\"q\": \"%s\", \"source\": \"%s\", \"target\": \"%s\"}",
                escapeJson(limitedText), sourceLanguage, targetLanguage
            );
            
            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            callback.onProgress(0.5);
            
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
                    
                    // Extract translation from JSON response
                    String translatedText = extractTranslatedText(response.toString());
                    callback.onProgress(0.8);
                    callback.onProgress(1.0);
                    return translatedText != null ? translatedText : text;
                }
            } else {
                System.err.println("Translation API returned error code: " + responseCode);
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.err.println("Error response: " + response);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Translation failed: " + e.getMessage(), e);
        }
        
        return text; // Return original text if translation fails
    }
    
    /**
     * Extract translated text from JSON response
     */
    private String extractTranslatedText(String jsonResponse) {
        Pattern pattern = Pattern.compile("\"translatedText\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(jsonResponse);
        if (matcher.find()) {
            return matcher.group(1).replace("\\\"", "\"").replace("\\n", "\n");
        }
        return null;
    }
    
    /**
     * Escape special JSON characters in text
     */
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    /**
     * Check if the API is available
     * @return true if API is available, false otherwise
     */
    public boolean isAvailable() {
        try {
            URL url = new URL(LIBRE_TRANSLATE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_BAD_METHOD;
        } catch (IOException e) {
            return false;
        }
    }
}
