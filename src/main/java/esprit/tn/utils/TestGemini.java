package esprit.tn.utils;

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

public class TestGemini {

    // API URL
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    // API Key should be stored securely (e.g., in an environment variable)
    private static final String API_KEY = "AIzaSyDlJH2RyzPz9CZdF2n9zcggC0JKd0nOwGc\n";

    public static void main(String[] args) {
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("❌ Erreur: La clé API Gemini est manquante. Configurez la variable d'environnement GEMINI_API_KEY.");
            return;
        }

        String poste = "post bodyguard"; // Change le poste ici
        Optional<String> questions = getQuestionsFromChatbot(poste);

        if (questions.isPresent()) {
            generatePDF(questions.get(), "Questions_Entretien.pdf");
            System.out.println("✅ PDF généré avec succès !");
        } else {
            System.out.println("❌ Échec de la génération des questions.");
        }
    }

    /**
     * Récupère les questions d'entretien depuis l'API Gemini.
     *
     * @param poste Le poste pour lequel générer les questions.
     * @return Les questions sous forme de chaîne de caractères, ou un Optional vide en cas d'erreur.
     */
    public static Optional<String> getQuestionsFromChatbot(String poste) {
        try {
            URL url = new URL(API_URL + "?key=" + API_KEY);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Construire la requête JSON correctement
            String prompt = "Génère 5 questions d'entretien pour un poste de " + poste;
            String jsonInputString = "{ \"contents\": [{\"role\": \"user\", \"parts\": [{\"text\": \"" + prompt + "\"}]}]}";

            // Envoyer la requête
            try (OutputStream os = connection.getOutputStream()) {
                byte[] inputBytes = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(inputBytes, 0, inputBytes.length);
            }

            // Vérifier la réponse HTTP
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                System.err.println("❌ Erreur API: " + statusCode + " - " + connection.getResponseMessage());
                return Optional.empty();
            }

            // Lire la réponse
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            // Parser la réponse JSON
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

    /**
     * Génère un fichier PDF contenant les questions d'entretien.
     *
     * @param questions Le texte des questions.
     * @param fileName  Le nom du fichier PDF.
     */
    public static void generatePDF(String questions, String fileName) {
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
    }
}
