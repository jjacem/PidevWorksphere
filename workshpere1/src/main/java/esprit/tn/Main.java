package esprit.tn;

import entities.Entretien;
import entities.Feedback;
import entities.TypeEntretien;
import services.EntretienService;
import services.FeedbackService;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;


public class Main {
    public static void main(String[] args) throws SQLException {

        EntretienService service = new EntretienService();

        System.out.println(service.getEntretienById(2));


//            Entretien entretien = new Entretien("Entretien mt3 info ", "je veux un ingenieur qui matrise java et synphony ",new Date(), Time.valueOf("10:00:00"), TypeEntretien.EN_PRESENTIEL, true);
//            service.ajouter(entretien);
//
//            System.out.println(service.afficher());
//
//            entretien.setTitre("Entretien Finallllllllll");
//            entretien.setDescription("yasiine il3ossssssssssssssss");
//            service.modifier(entretien);
////
////     service.supprimer(3);
//
//        Feedback feedback = new Feedback(
//                "Candidat compétent avec une bonne maîtrise des technologies demandées.",
//                5, // Score sur 5
//                7,
//                new Date(System.currentTimeMillis())
//        ) ;
//
//
//
//       FeedbackService feedbackService = new FeedbackService();
//
//        feedbackService.ajouter(feedback);










        }









        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.


    }
