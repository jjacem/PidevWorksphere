package esprit.tn.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class WhatsAppService {


    private static final String ACCOUNT_SID = "";

    private static final String AUTH_TOKEN = "";

    private static final String TWILIO_WHATSAPP_NUMBER = "";



    public static void sendWhatsAppMessage(String toNumber, String messageBody) {

        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + toNumber),
                    new PhoneNumber(TWILIO_WHATSAPP_NUMBER),
                    messageBody
            ).create();



            System.out.println("Message envoyé avec SID: " + message.getSid());



        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du message WhatsApp: " + e.getMessage());

        }
    }





}