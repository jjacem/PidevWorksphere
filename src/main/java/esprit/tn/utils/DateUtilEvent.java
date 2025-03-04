package esprit.tn.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtilEvent {
    public static String getDaysRemainingMessage(LocalDate eventDate) {
        LocalDate currentDate = LocalDate.now();
        long daysRemaining = ChronoUnit.DAYS.between(currentDate, eventDate);

        if (daysRemaining > 0) {
            return "Jours restants : " + daysRemaining + " \u23F3"; // Ajouter un emoji d'horloge
        } else if (daysRemaining == 0) {
            return "L'événement est aujourd'hui ! \uD83C\uDF89"; // Ajouter un emoji de fête
        } else {
            return "L'événement est passé \uD83D\uDCC5"; // Ajouter un emoji de calendrier
        }
    }
}
