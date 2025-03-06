package esprit.tn.services;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class GemeniService {


    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    private static final String API_KEY = "";


    public static Optional<String> getQuestionsFromChatbot(String poste) {
        try {
            URL url = new URL(API_URL + "?key=" + API_KEY);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String prompt = "Génère 5 questions d'entretien pour un poste de " + poste;
            String jsonInputString = "{ \"contents\": [{\"role\": \"user\", \"parts\": [{\"text\": \"" + prompt + "\"}]}]}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] inputBytes = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(inputBytes, 0, inputBytes.length);
            }

            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                System.err.println("❌ Erreur API: " + statusCode + " - " + connection.getResponseMessage());
                return Optional.empty();
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray candidates = jsonResponse.optJSONArray("candidates");

            if (candidates == null || candidates.isEmpty()) {
                System.err.println("❌ Erreur: Réponse API invalide ou vide.");
                return Optional.empty();
            }

            return Optional.of(
                    candidates.getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")
            );

        } catch (IOException e) {
            System.err.println("❌ Erreur réseau: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue: " + e.getMessage());
        }

        return Optional.empty();
    }


    public static String generatePDF(String questions, String fileName) {
        try {
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Questions d'entretien").setFontSize(16));
            document.add(new Paragraph(questions));

            document.close();
            System.out.println("✅ Fichier PDF enregistré: " + fileName);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération du PDF: " + e.getMessage());
        }
        return questions;
    }
}
