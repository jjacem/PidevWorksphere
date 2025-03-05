package esprit.tn.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.io.File;
import jakarta.mail.util.ByteArrayDataSource;

public class Emailsend {

    public static void sendEmail(String recipient, String subject, String content) {
        final String senderEmail = "worksphere12345@gmail.com";
        final String senderPassword = "ehfp ylcf vrrw cdiz"; // App-specific password for Gmail

        // SMTP properties for Gmail
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Create a session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create a new email message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);

            // Multipart content for HTML and image
            Multipart multipart = new MimeMultipart();

            // HTML body with blue-themed CSS
            String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f4f7fa; margin: 0; padding: 0; }" +
                    ".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }" +
                    ".header { background-color: #3498db; padding: 20px; text-align: center; }" +
                    ".header img { max-width: 150px; height: auto; }" +
                    ".content { padding: 30px; color: #333333; }" +
                    ".content h1 { color: #2980b9; font-size: 24px; margin-top: 0; }" +
                    ".content p { font-size: 16px; line-height: 1.5; }" +
                    ".footer { background-color: #ecf0f1; padding: 15px; text-align: center; font-size: 12px; color: #7f8c8d; }" +
                    ".button { display: inline-block; padding: 12px 25px; background-color: #2980b9; color: #ffffff; text-decoration: none; border-radius: 5px; font-weight: bold; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    "<img src='cid:logo' alt='WorkSphere Logo'>" +
                    "</div>" +
                    "<div class='content'>" +
                    "<h1>" + subject + "</h1>" +
                    "<p>" + content + "</p>" +

                    "</div>" +
                    "<div class='footer'>" +
                    "<p>&copy; 2025 WorkSphere. All rights reserved.</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            // Add HTML content
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);

            // Attach the logo image
            MimeBodyPart imagePart = new MimeBodyPart();
            String logoPath = "C:\\Users\\yassi\\OneDrive\\Documents\\GitHub\\finalone\\safeone\\PidevWorksphere\\src\\main\\resources\\Images\\474188139_2099437073823213_5214087864459817142_n.png"; // Adjust path as needed
            File logoFile = new File(logoPath);
            if (logoFile.exists()) {
                imagePart.attachFile(logoFile);
                imagePart.setContentID("<logo>");
                imagePart.setDisposition(MimeBodyPart.INLINE);
                multipart.addBodyPart(imagePart);
            } else {
                System.out.println("Logo file not found at: " + logoPath);
            }

            // Set the multipart content to the message
            message.setContent(multipart);

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully to " + recipient + "!");

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error attaching logo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Example usage
    public static void main(String[] args) {
        sendEmail("recipient@example.com", "Welcome to WorkSphere",
                "Hello! We're excited to have you on board. This is a test email with a blue-themed design.");
    }
}