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

        User u1 = new User();
        u1=u1.RH("yassine","bouras","yassinbouras0@rh.com","1234","",Sexe.HOMME,"",5,"conflicts manager");
      User u2=new User();
              u2=u2.Candidat("yassine","bouras","yassinbou000000ra0s@Candida00t.com","1234","",Sexe.HOMME,"",500.00);
   User u3=new User();
    u3=u3.Manager ("yassine","bouras","yassinbouras@Candidat.com","1234","",Sexe.HOMME,"","finance",10,5000000.00);
    User u4=new User();
    u4=u4.Employe ("yassine","bouras","yassinbouras@Candidat.com","1234","",Sexe.HOMME,"","financier",1000.00,5,"finance","compete");

        try {
            serviceUser.ajouter(u2);
            System.out.println(serviceUser.afficher());
            int i1 = serviceUser.findidbyemail(u2.getEmail());
            u2.setIdUser(i1);
            u2.setNom("yasss");
            u2.setPrenom("bou");
            serviceUser.modifier(u2);
            System.out.println(serviceUser.afficher());

            System.out.println(serviceUser.afficher());
            Reclamation r1 = new Reclamation("en cours", "probleme de salaire");
            r1.setId_candidat(u2.getIdUser());
            ServiceReclamation serviceReclamation = new ServiceReclamation();
            serviceReclamation.ajouter(r1);
            System.out.println(serviceReclamation.afficher());
            serviceReclamation.afficher();
            serviceUser.supprimer(u2.getIdUser());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }






}}