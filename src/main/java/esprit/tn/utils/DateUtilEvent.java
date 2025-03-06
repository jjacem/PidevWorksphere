package esprit.tn.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtilEvent {
    //Cette méthode prend en paramètre un objet LocalDate représentant la date de l'événement
    public static String getDaysRemainingMessage(LocalDate eventDate) {
        //Elle compare cette date à la date actuelle (LocalDate.now())
        // pour calculer le nombre de jours restant avant l'événement
        LocalDate currentDate = LocalDate.now();
        long daysRemaining = ChronoUnit.DAYS.between(currentDate, eventDate);
        //Si le nombre de jours restant est positif, cela signifie que l'événement est à venir.
        //Si le nombre de jours est égal à zéro, cela signifie que l'événement a lieu aujourd'hui.
        //Si le nombre de jours est négatif, cela signifie que l'événement est déjà passé.
        if (daysRemaining > 0) {
            return "Jours restants : " + daysRemaining + " \u23F3"; // Ajouter un emoji d'horloge
        } else if (daysRemaining == 0) {
            return "L'événement est aujourd'hui ! \uD83C\uDF89"; // Ajouter un emoji de fête
        } else {
            return "L'événement est passé \uD83D\uDCC5"; // Ajouter un emoji de calendrier
        }
    }
}
