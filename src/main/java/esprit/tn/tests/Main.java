package esprit.tn.tests;


import esprit.tn.entities.*;
import esprit.tn.services.*;
import esprit.tn.utils.Emailsend;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
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
        u3 = u3.Manager("Jacem", "Hbaieb", "jacemhbaieb@gmail.com", "1234", "", Sexe.HOMME, "", "finance", 10, 5000000.00);
        User u4 = new User();
        u4 = u4.Employe("yassine", "bouras", "yassinbouras90@gmail.com", "1234", "", Sexe.HOMME, "", "financier", 1000.00, 5, "finance", "compete");

//        try {
//            serviceUser.ajouter(u4);
//serviceUser.findbyid(1);
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        try {
//            ServiceReclamation s = new ServiceReclamation();
//
//
//            Reclamation rec1 = new Reclamation("En attente", "Problème de transport", "Problème de transport", "Transport", 3, 13);
//            s.ajouter(rec1);
//            s.supprimer(19);
//
//            // Afficher les réclamations
////            System.out.println("Liste des réclamations après ajout :");
////            for (Reclamation r : serviceReclamation.afficher()) {
////                System.out.println(r);
////            }
//
//            // Modifier une réclamation
//            rec1.setStatus("Résolu");
//            rec1.setDescription("Problème azzaazzzaazazazaz");
//            s.modifier(rec1);
//
//            // Supprimer une réclamation
////            serviceReclamation.supprimer(rec1.getId_reclamation());
//
//            // Afficher les réclamations après suppression
////            System.out.println("Liste des réclamations après suppression :");
////            for (Reclamation r : serviceReclamation.afficher()) {
////                System.out.println(r);
////            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        ServiceReponse serviceReponse = new ServiceReponse();

        try {
            // 1. Ajouter une réponse
            Reponse newReponse = new Reponse("Ceci est un message de test", 13, 20, "en attente");
            serviceReponse.ajouter(newReponse);
            System.out.println("Réponse ajoutée avec succès!");

            // 2. Afficher toutes les réponses
            List<Reponse> reponses = serviceReponse.afficher();
            System.out.println("\nListe des réponses:");
            for (Reponse rep : reponses) {
                System.out.println(rep);
            }

            // 3. Modifier une réponse existante
            if (!reponses.isEmpty()) {
                Reponse reponseToUpdate = reponses.get(0);
                reponseToUpdate.setMessage("Message mis à jour");
                reponseToUpdate.setStatus("en cours");
                serviceReponse.modifier(reponseToUpdate);
                System.out.println("Réponse modifiée avec succès!");
            }

            if (!reponses.isEmpty()) {
                int idToDelete = reponses.get(0).getId_reponse();
                serviceReponse.supprimer(idToDelete);
                System.out.println("Réponse supprimée avec succès!");
            }

        } catch (SQLException e) {
            System.out.println("Erreur: " + e.getMessage());
        }


        ServiceReclamation rec = new ServiceReclamation();
        Reclamation rec2 = new Reclamation("en cours", "titre", "description", "type", 3, 22);
        try {
            rec.ajouter(rec2);
        } catch (SQLException e) {
            e.printStackTrace();
        }


serviceUser.changermdp("1234",3);




    serviceUser.ajouter(u3);



}}