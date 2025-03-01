package esprit.tn.services;

public interface SmsService {
    boolean envoyerSms(String numero, String message);
}
