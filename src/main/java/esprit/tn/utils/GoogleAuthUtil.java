package esprit.tn.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.sun.net.httpserver.HttpServer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class GoogleAuthUtil {
    private static final String CLIENT_ID = "1067630665961-fv50p9cce6roe5ipk942sbpbfmii2634.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:8100/callback";

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email"
    );

    // Load the logo as Base64 once at class initialization
    private static final String LOGO_BASE64;
    static {
        String logoPath = "C:\\Users\\yassi\\OneDrive\\Documents\\GitHub\\finalone\\safeone\\PidevWorksphere\\src\\main\\resources\\Images\\474188139_2099437073823213_5214087864459817142_n.png"; // Adjust path as needed
        String base64Image = "";
        try {
            File logoFile = new File(logoPath);
            if (logoFile.exists()) {
                byte[] fileContent = Files.readAllBytes(logoFile.toPath());
                String mimeType = Files.probeContentType(logoFile.toPath());
                base64Image = "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(fileContent);
            } else {
                System.err.println("Fichier logo introuvable à : " + logoPath);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du logo : " + e.getMessage());
        }
        LOGO_BASE64 = base64Image;
    }

    public static Credential authenticate() throws IOException {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPES)
                .setAccessType("offline")
                .setApprovalPrompt("force") // Ensure refresh token is granted
                .build();

        String authUrl = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI.create(authUrl));
        } else {
            System.out.println("Ouvrez cette URL dans votre navigateur : " + authUrl);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8100), 0);
        AtomicReference<Credential> credentialHolder = new AtomicReference<>();

        server.createContext("/callback", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String code = query != null && query.contains("code=") ? query.split("code=")[1].split("&")[0] : null;

            // HTML response with logo and bluish theme in French
            String htmlResponse = buildHtmlResponse(code != null);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, htmlResponse.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(htmlResponse.getBytes());
            }
            exchange.close();

            if (code != null) {
                try {
                    GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                            .setRedirectUri(REDIRECT_URI)
                            .execute();

                    Credential credential = flow.createAndStoreCredential(tokenResponse, null);
                    credentialHolder.set(credential);
                    System.out.println("Authentification réussie !");

                } catch (Exception e) {
                    System.err.println("Erreur lors de l'échange de jeton : " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Échec de l'authentification : Aucun code reçu.");
            }

            server.stop(0);
        });

        server.setExecutor(Executors.newFixedThreadPool(1));
        server.start();
        System.out.println("En attente de l'authentification Google...");

        while (credentialHolder.get() == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return credentialHolder.get();
    }

    public static Userinfo getUserInfo(Credential credential) throws IOException {
        if (credential == null || credential.getAccessToken() == null) {
            throw new IOException("Identifiants invalides. Impossible de récupérer les informations utilisateur.");
        }

        Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("Worksphere")
                .build();

        return oauth2.userinfo().get().execute();
    }

    private static String buildHtmlResponse(boolean isValid) {
        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Authentification Google</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f7fa; margin: 0; padding: 0; display: flex; justify-content: center; align-items: center; height: 100vh; }" +
                ".container { background-color: #ffffff; padding: 40px; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); text-align: center; max-width: 500px; }" +
                ".logo { max-width: 150px; height: auto; margin-bottom: 20px; }" +
                "h1 { color: " + (isValid ? "#3498db" : "#e74c3c") + "; font-size: 28px; margin-bottom: 20px; }" +
                "p { font-size: 16px; color: #333333; line-height: 1.6; }" +
                ".icon { font-size: 60px; margin-bottom: 20px; }" +
                ".button { display: inline-block; padding: 12px 30px; background-color: #3498db; color: #ffffff; text-decoration: none; border-radius: 5px; font-weight: bold; margin-top: 20px; }" +
                ".button:hover { background-color: #2980b9; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                (LOGO_BASE64.isEmpty() ? "" : "<img src='" + LOGO_BASE64 + "' alt='FitFusion Logo' class='logo'>") +
                "<div class='icon'>" + (isValid ? "✅" : "❌") + "</div>" +
                "<h1>" + (isValid ? "Compte Gmail Valide" : "Compte Gmail Invalide") + "</h1>" +
                "<p>" + (isValid ? "Votre compte Gmail est valide. Veuillez retourner à l'application FitFusion." :
                "Une erreur s'est produite. Votre compte Gmail n'a pas pu être vérifié. Réessayez ou contactez le support.") + "</p>" +
                "<a href='javascript:window.close()' class='button'>Fermer la Fenêtre</a>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    // Example usage
    public static void main(String[] args) throws IOException {
        Credential credential = authenticate();
        if (credential != null) {
            Userinfo userInfo = getUserInfo(credential);
            System.out.println("Email de l'utilisateur : " + userInfo.getEmail());
            System.out.println("Nom de l'utilisateur : " + userInfo.getName());
        }
    }
}