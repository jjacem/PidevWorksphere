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
            String encodedLieu = lieu.replace(" ", "+");

            // Construire l'URL de l'API Nominatim
            String urlString = "https://nominatim.openstreetmap.org/search?q=" + encodedLieu + "&format=json";
            URL url = new URL(urlString);

            // Ouvrir une connexion HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Lire la réponse de l'API
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