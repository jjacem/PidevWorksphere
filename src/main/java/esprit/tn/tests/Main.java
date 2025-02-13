package esprit.tn.tests;


import esprit.tn.entities.*;
import esprit.tn.services.*;
import esprit.tn.utils.MyDatabase;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        Connection connection;

        connection = MyDatabase.getInstance().getConnection();


        //test
        //user
        ServiceUser serviceUser = new ServiceUser();

        User u1 = new User("yassinbouras", "bouras", "yassinb@gmail.com", "1236545", "tunis", Sexe.HOMME);
        try {
            serviceUser.ajouter(u1);
            System.out.println(serviceUser.afficher());
            int i1 = serviceUser.findidbyemail(u1.getEmail());
            u1.setIdUser(i1);
            u1.setNom("yasss");
            u1.setPrenom("bou");
            serviceUser.modifier(u1);
            System.out.println(serviceUser.afficher());
            serviceUser.supprimer(u1.getIdUser());
            System.out.println(serviceUser.afficher());


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        //empl
        ServiceEmploye serviceEmploye = new ServiceEmploye();
        User u2 = new User("yassinbouras", "bouras", "yassin1020@emp.com", "1236545", "tunis", Sexe.HOMME);
        Employe e1 = new Employe(u2, "java", 2000, 2, "developpeur");
        try {
            serviceEmploye.ajouter(e1);
            System.out.println(serviceEmploye.afficher());
            int i1 = serviceEmploye.findidbyemail(u2.getEmail());
            System.out.println("idest" + i1);
            e1.setId_employe(i1);
            e1.setNom("yasss");
            e1.setPrenom("bou");
            e1.setPoste("tailwi,d");
            serviceEmploye.modifier(e1);
            System.out.println(serviceEmploye.afficher());
            serviceEmploye.supprimer(i1);
            System.out.println(serviceEmploye.afficher());


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        //manager
        ServiceManager serviceManager = new ServiceManager();
        User u3 = new User("yassinbouras", "bouras", "yassin100@Manager.com", "1236545", "tunis", Sexe.HOMME);
         Manager m1 = new Manager(u3, 30, 8000, "hrever");

        try {
            serviceManager.ajouter(m1);
            System.out.println(serviceEmploye.afficher());
            int i1 = serviceManager.findidbyemail(u3.getEmail());
            System.out.println("idest" + i1);
            m1.setId_manager(i1);
            e1.setNom("yasss");
            e1.setPrenom("bou");
            e1.setDepartement("finance");
            serviceManager.modifier(m1);
            System.out.println(serviceManager.afficher());
            serviceManager.supprimer(i1);
            System.out.println(serviceManager.afficher());


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        //candidat
        ServiceCandidat serviceCandidat = new ServiceCandidat();
        User u4 = new User("yassinbouras", "bouras", "yassinb@candidat.com", "1236545", "tunis", Sexe.HOMME);
        Candidat c1 = new Candidat(u4, Status.Candidature, 1000);

        //rh
        ServiceRH serviceRH = new ServiceRH();
        User u5 = new User("yassinbouras", "bouras", "yassinb@emp.com", "1236545", "tunis", Sexe.HOMME);
        RH rh1 = new RH(u5, "java", "developpeur", 2);

    }
}