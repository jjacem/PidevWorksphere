package tests;

import entities.Formation;
import entities.Reservation;
import entities.Typeformation;
import services.ServiceFormation;
import services.ServiceReservation;
import services.ServiceUser;
import utils.MyDatabase;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {
        //service user
        ServiceUser serviceuser= new ServiceUser();
        //service formation
        ServiceFormation servicformation = new ServiceFormation();
        //service reservation
        ServiceReservation servicereservation = new ServiceReservation();
        Formation formation2 = new Formation(
                7,
                "Mobile",
                "eeeeeeeee",
                LocalDate.of(2025, 2, 22),
                LocalTime.of(15, 0),
                LocalTime.of(17, 0),
                25,
                Typeformation.Presentiel,
                1
        );
        Reservation res= new Reservation(2,LocalDate.of(2025, 2, 18),2,6);


        try {
            //user
            System.out.println(serviceuser.afficher());
            //formation
            //servicformation.ajouterFormation(formation);
           // servicformation.supprimeFormation(formation);
            servicformation.modifierFormation(formation2);
            System.out.println(servicformation.getListFormation());
           //Reservation
            System.out.println(servicereservation.getListReservation());
            //servicereservation.ajouterReservation(res);
            //servicereservation.modifierReservation(res);
            servicereservation.supprimeReservation(res);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
