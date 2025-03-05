package esprit.tn.entities;

import esprit.tn.services.SmsService;
import okhttp3.*;

import java.io.IOException;

public class TwilioSMS  implements SmsService {
    private static final String API_KEY = "e12bf72d07e4c9542d208378f1667727-260cc3b4-fef6-4f54-a161-8e248a60ba04";
    private static final String API_URL = "https://nmvl3e.api.infobip.com/sms/2/text/advanced";

    @Override
    public boolean envoyerSms(String numero, String message) {
        OkHttpClient client = new OkHttpClient();

        String jsonBody = "{\"messages\":[{\"destinations\":[{\"to\":\"" + numero + "\"}],"
                + "\"from\":\"447491163443\",\"text\":\"" + message + "\"}]}";

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "App " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Reservation effectué avec succès !");
                return true;
            } else {
                System.err.println("Erreur SMS : " + response.body().string());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Échec de l'envoi du SMS.");
            return false;
        }
    }

//    public static void main(String[] args) {
//        // Création d'une instance de TwilioSMS
//        TwilioSMS twilioSMS = new TwilioSMS();
//
//        // Numéro de téléphone (à tester) et message à envoyer
//        String numero = "+21653462002"; // Remplace par un numéro valide
//        String message = "Ceci est un message de test via l'API SMS.";
//
//        // Envoi du SMS et récupération du résultat
//        boolean resultat = twilioSMS.envoyerSms(numero, message);
//
//        // Affichage du résultat
//        if (resultat) {
//            System.out.println("Le SMS a été envoyé avec succès.");
//        } else {
//            System.out.println("L'envoi du SMS a échoué.");
//        }
//    }
}