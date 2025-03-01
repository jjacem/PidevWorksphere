package esprit.tn.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class QRCodeGenerator {

    private static final String QR_CODE_API_URL = "https://api.qrcode-monkey.com/qr/custom";

    public static byte[] generateQRCode(String jsonData) throws IOException, InterruptedException {
        // Encoder les données JSON pour l'URL
        String encodedData = URLEncoder.encode(jsonData, StandardCharsets.UTF_8);

        // Construire l'URL de l'API
        String apiUrl = QR_CODE_API_URL + "?data=" + encodedData + "&size=300&fileType=png";

        // Envoyer une requête HTTP pour générer le QR code
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        // Retourner les données binaires de l'image
        return response.body();
    }
}