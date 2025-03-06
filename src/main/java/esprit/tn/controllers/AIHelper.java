
package esprit.tn.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AIHelper {
   // Stocke la clé API nécessaire pour authentifier les requêtes auprès de l'API Google.
    private static final String API_KEY = "AIzaSyB5HQtWxwdLaAXwsfE9RpRM0uAVgtKCS4M";
    //Contient l’URL de l’API pour interagir avec Gemini 2.0 Flash
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
//Elle prend un prompt (String) comme paramètre, qui représente la question ou la demande envoyée à l’IA.
    public static String generateContent(String prompt) throws IOException {
        //Crée une connexion HTTP vers API_URL.
        URL url = new URL(API_URL);
        //Ouvre une connexion vers l'URL spécifiée.
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //Définit la méthode HTTP POST (nécessaire pour envoyer des données à l’API).
        connection.setRequestMethod("POST");
        //Spécifie que le contenu envoyé sera du JSON (Content-Type: application/json
        connection.setRequestProperty("Content-Type", "application/json");
        //Active la sortie (setDoOutput(true)) pour pouvoir écrire dans la requête.
        connection.setDoOutput(true);
//Construit un JSON au format attendu par l’API.
        //Crée une chaîne JSON représentant le corps de la requête.
        String jsonInputString = "{\"contents\": [{\"parts\":[{\"text\": \"" + prompt + "\"}]}]}";
//Ce flux permet d'écrire des données dans la requête HTTP.
        //on va envoyer des données au serveur.
        //Cela permet d'envoyer un JSON dans une requête POST (ou PUT), souvent utilisé dans les APIs REST.
        try (OutputStream os = connection.getOutputStream()) {
            //Convertit la chaîne JSON (jsonInputString) en un tableau de bytes encodé
            //en UTF-8 (nécessaire pour le transfert réseau)
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
//Lecture de la réponse du serveur
        //connection.getInputStream() → Ouvre un flux d'entrée (InputStream) pour lire la réponse du serveur.
        //🔹 new InputStreamReader(..., "utf-8") → Convertit le flux d'octets en caractères lisibles en UTF-8.
        //🔹 BufferedReader → Permet de lire la réponse ligne par ligne de manière optimisée.
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            // Lit la réponse ligne par ligne jusqu'à la fin.
            while ((responseLine = br.readLine()) != null) {
                //Supprime les espaces inutiles et stocke la réponse dans un StringBuilder
                response.append(responseLine.trim());
            }
            //Retourne la réponse complète sous forme de String.
            return response.toString();
        }
    }
}