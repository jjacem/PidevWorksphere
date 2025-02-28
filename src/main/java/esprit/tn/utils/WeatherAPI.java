package esprit.tn.utils;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAPI {

    private static final String API_KEY = "8f928f9a500f0646cabebc1526662747";
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    public static String getWeather(String city) {
        try {
            String urlString = String.format(API_URL, city, API_KEY);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonReader jsonReader = Json.createReader(in);
            JsonObject json = jsonReader.readObject();
            jsonReader.close();
            connection.disconnect();

            JsonObject main = json.getJsonObject("main");
            double temp = main.getJsonNumber("temp").doubleValue();
            String weather = json.getJsonArray("weather").getJsonObject(0).getString("main");

            return String.format("Température: %.1f°C, Météo: %s", temp, weather);

        } catch (Exception e) {
            e.printStackTrace();
            return "Impossible de récupérer la météo";
        }
    }
}