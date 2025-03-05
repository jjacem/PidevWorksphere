package esprit.tn.services;

import esprit.tn.entities.Quote;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class QuoteFetcher {

    public static Quote fetchRandomQuote() {
        try {
            URL url = new URL("https://dummyjson.com/quotes/random");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON
                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                String quote = jsonObject.get("quote").getAsString();
                String author = jsonObject.get("author").getAsString();

                return new Quote(quote, author);
            } else {
                throw new RuntimeException("Failed to fetch quote. Response code: " + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching quote: " + e.getMessage(), e);
        }
    }
}