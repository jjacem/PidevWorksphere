package esprit.tn.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class NominatimAPI {

    public static String getCoordinates(String lieu) {
        try {
            // Encoder le lieu pour qu'il soit compatible avec une URL
            //Le lieu (nom de la ville ou de l'endroit) peut contenir des espaces,
            // donc la méthode remplace les espaces par des "+"
            // pour que l'URL soit valide et correctement interprétée par l'API
            String encodedLieu = lieu.replace(" ", "+");

            // Construire l'URL de l'API Nominatim
            //L'URL est construite pour appeler l'API de recherche de Nominatim. Cette API prend un paramètre q pour
            // le lieu recherché et retourne la réponse au format JSON.
            String urlString = "https://nominatim.openstreetmap.org/search?q=" + encodedLieu + "&format=json";
            URL url = new URL(urlString);

            // Ouvrir une connexion HTTP
            //La méthode ouvre une connexion HTTP de type GET pour récupérer les données à partir de l'URL de l'API.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Lire la réponse de l'API
            //Une fois la connexion établie, les données JSON retournées par
            // l'API sont lues ligne par ligne et stockées dans un StringBuilder.
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Afficher la réponse brute pour le débogage
            System.out.println("Réponse de l'API Nominatim : " + response.toString());

            // Parser la réponse JSON
            JSONArray jsonArray = new JSONArray(response.toString());
            //Si la réponse contient des résultats, le premier élément est extrait et
            // ses valeurs de latitude (lat) et longitude (lon) sont récupérées.
            if (jsonArray.length() > 0) {
                JSONObject firstResult = jsonArray.getJSONObject(0);
                String lat = firstResult.getString("lat");
                String lon = firstResult.getString("lon");
                return "Latitude: " + lat + ", Longitude: " + lon;
            } else {
                return "Lieu non trouvé";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la récupération des coordonnées";
        }
    }
}