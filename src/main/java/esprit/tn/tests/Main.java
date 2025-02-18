package esprit.tn.tests;

import esprit.tn.entities.*;
import esprit.tn.services.ServiceFormation;
import esprit.tn.services.ServiceReservation;
import esprit.tn.services.ServiceUser;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) throws MalformedURLException {
        //service user
        ServiceUser serviceuser= new ServiceUser();
        //service formation
        ServiceFormation servicformation = new ServiceFormation();
        //service reservation
        ServiceReservation servicereservation = new ServiceReservation();
        URL photoUrl = new URL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRtVFvnHlQ8c2lcp0CQeQNJ3GuyZ_3RqFgbsA&s");
        User user1 = new User( "Eya", "Kassous", Role.Rh, "eya@gmail.com" ,"sjdjd" , "tunis");
//        Formation formation = new Formation(
//                "Bonjour3",
//                "Description",
//                LocalDate.of(2025, 2, 22),
//                LocalTime.of(12, 0),
//                LocalTime.of(14, 0),
//                30,
//                Typeformation.Présentiel,
//                2
//        );
        Reservation res= new Reservation(1,LocalDate.of(2025, 2, 19),2,6);


        try {
            //user
            //System.out.println(serviceuser.getAllusers());
            // serviceuser.ajouterUser(user1);
            //formation
            //servicformation.ajouterFormation(formation);
            // servicformation.supprimeFormation(formation);
            //servicformation.modifierFormation(formation);
            System.out.println(servicformation.getListFormation());
            //Reservation
            // System.out.println(servicereservation.getListReservation());
            // servicereservation.ajouterReservation(res);
            // servicereservation.modifierReservation(res);
            //servicereservation.supprimeReservation(res);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}