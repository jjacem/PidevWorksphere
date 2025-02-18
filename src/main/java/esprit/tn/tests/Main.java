package esprit.tn.tests;


import esprit.tn.entities.*;
import esprit.tn.services.*;
import esprit.tn.utils.Emailsend;
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
        u1 = u1.RH("yassine", "bouras", "yassinbouras0@rh.com", "1234", "", Sexe.HOMME, "", 5, "conflicts manager");
        User u2 = new User();
        u2 = u2.Candidat("yassine", "bouras", "yassin18.gmail", "1234", "", Sexe.HOMME, "", 500.00);
        User u3 = new User();
        u3 = u3.Manager("yassine", "bouras", "yassinbouras@Candidat.com", "1234", "", Sexe.HOMME, "", "finance", 10, 5000000.00);
        User u4 = new User();
        u4 = u4.Employe("yassine", "bouras", "yassinbouras@Candidat.com", "1234", "", Sexe.HOMME, "", "financier", 1000.00, 5, "finance", "compete");

        try {
            serviceUser.ajouter(u4);
serviceUser.findbyid(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try {
            ServiceReclamation serviceReclamation = new ServiceReclamation();

            // Ajouter une réclamation
            Reclamation rec1 = new Reclamation("En attente", "Problème de transport");
            rec1.setId_candidat(3);  // Assurez-vous que cet ID utilisateur existe
            serviceReclamation.ajouter(rec1);

            // Afficher les réclamations
            System.out.println("Liste des réclamations après ajout :");
            for (Reclamation r : serviceReclamation.afficher()) {
                System.out.println(r);
            }

            // Modifier une réclamation
            rec1.setStatus("Résolu");
            rec1.setDescription("Problème réglé");
            rec1.setId_reclamation(2);
            serviceReclamation.modifier(rec1);

            // Supprimer une réclamation
            serviceReclamation.supprimer(rec1.getId_reclamation());

            // Afficher les réclamations après suppression
            System.out.println("Liste des réclamations après suppression :");
            for (Reclamation r : serviceReclamation.afficher()) {
                System.out.println(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }  try {
            ServiceReponse serviceReponse = new ServiceReponse();

            // Ajouter une réponse avec id_reclamation = 2 et id_employe = 13
            Reponse rep1 = new Reponse("Ceci est une réponse test.", 13, 3);
            serviceReponse.ajouter(rep1);

            // Afficher toutes les réponses
            System.out.println("Liste des réponses après ajout :");
            for (Reponse r : serviceReponse.afficher()) {
                System.out.println(r);
            }

            // Modifier la réponse
            rep1.setMessage("Message mis à jour.");
            serviceReponse.modifier(rep1);

            // Supprimer la réponse
            serviceReponse.supprimer(rep1.getId_reponse());

            // Afficher après suppression
            System.out.println("Liste des réponses après suppression :");
            for (Reponse r : serviceReponse.afficher()) {
                System.out.println(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    }