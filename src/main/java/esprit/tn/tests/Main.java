package esprit.tn.tests;


import esprit.tn.entities.*;
import esprit.tn.services.*;
import esprit.tn.utils.Emailsend;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.List;
import java.util.Date ;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection connection;

        connection = MyDatabase.getInstance().getConnection();


//        //test
        //user
//        ServiceUser serviceUser = new ServiceUser();
//        User u1 = new User();
//        u1 = u1.RH(
//                "jacem",
//                "jouili",
//                "jacemjouili@gmail.com",
//                "123456",
//                "01/05/1985",
//                Sexe.FEMME,
//                "New York",
//                10,
//                "Senior HR Manager"
//        );
//
//        User u2 = new User();
//        u2 = u2.Candidat(
//                "Smith",
//                "James",
//                "james.smith24@gmail.com",
//                "Candid@t3Pass",
//                "12/11/1992",
//                Sexe.HOMME,
//                "Los Angeles",
//                750.50
//        );
//
//        User u3 = new User();
//        u3 = u3.Manager(
//                "Davis",
//                "Sophia",
//                "sophia.davis@financepro.com",
//                "Fin@nc3Guru",
//                "23/03/1980",
//                Sexe.FEMME,
//                "Chicago",
//                "Finance",
//                15,
//                2500000.00
//        );
//
//          User u4 = new User();
//          u4 = u4.Employe(
//                "Eya",
//                "Kassous",
//                "eyakassous55@gmail.com",
//                "123456",
//                "09/09/1990",
//                Sexe.HOMME,
//                "San Francisco",
//                "IT Support",
//                1200.00,
//                8,
//                "Technology",
//                "Technical Support"
//        );
//
////        try {
////            serviceUser.ajouter(u4);
////serviceUser.findbyid(1);
////        } catch (SQLException e) {
////            System.out.println(e.getMessage());
////        }
////        try {
////            ServiceReclamation s = new ServiceReclamation();
////
////
////            Reclamation rec1 = new Reclamation("En attente", "Problème de transport", "Problème de transport", "Transport", 3, 13);
////            s.ajouter(rec1);
////            s.supprimer(19);
////
////            // Afficher les réclamations
//////            System.out.println("Liste des réclamations après ajout :");
//////            for (Reclamation r : serviceReclamation.afficher()) {
//////                System.out.println(r);
//////            }
////
////            // Modifier une réclamation
////            rec1.setStatus("Résolu");
////            rec1.setDescription("Problème azzaazzzaazazazaz");
////            s.modifier(rec1);
////
////            // Supprimer une réclamation
//////            serviceReclamation.supprimer(rec1.getId_reclamation());
////
////            // Afficher les réclamations après suppression
//////            System.out.println("Liste des réclamations après suppression :");
//////            for (Reclamation r : serviceReclamation.afficher()) {
//////                System.out.println(r);
//////            }
////
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
//        ServiceReponse serviceReponse = new ServiceReponse();
//
//        try {
//            // 1. Ajouter une réponse
//            Reponse newReponse = new Reponse("Ceci est un message de test", 13, 20, "en attente");
//            serviceReponse.ajouter(newReponse);
//            System.out.println("Réponse ajoutée avec succès!");
//
//            // 2. Afficher toutes les réponses
//            List<Reponse> reponses = serviceReponse.afficher();
//            System.out.println("\nListe des réponses:");
//            for (Reponse rep : reponses) {
//                System.out.println(rep);
//            }
//
//            // 3. Modifier une réponse existante
//            if (!reponses.isEmpty()) {
//                Reponse reponseToUpdate = reponses.get(0);
//                reponseToUpdate.setMessage("Message mis à jour");
//                reponseToUpdate.setStatus("en cours");
//                serviceReponse.modifier(reponseToUpdate);
//                System.out.println("Réponse modifiée avec succès!");
//            }
//
//            if (!reponses.isEmpty()) {
//                int idToDelete = reponses.get(0).getId_reponse();
//                serviceReponse.supprimer(idToDelete);
//                System.out.println("Réponse supprimée avec succès!");
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Erreur: " + e.getMessage());
//        }
//
//
//        ServiceReclamation rec = new ServiceReclamation();
//        Reclamation rec2 = new Reclamation("en cours", "titre", "description", "type", 3, 22);
//        try {
//            rec.ajouter(rec2);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//
//serviceUser.changermdp("1234",3);
//
//
//
//
      // serviceUser.ajouter(u1);
        //serviceUser.ajouter(u2);
       // serviceUser.ajouter(u3);
       // serviceUser.ajouter(u4);

        Date currentDate = new Date();
        Time currentTime = new Time(System.currentTimeMillis());

//        Entretien entretien = new Entretien(
//                "Entretien java hhhhhhhhhh  ",
//                "Entretien pour le poste de développeur",
//                currentDate,
//                currentTime,
//                TypeEntretien.EN_PRESENTIEL,
//                true,
//                4
//
//        );
//
////        int idOffre = 1;
////        EntretienService entretienService = new EntretienService();
////
////
//////        try {
//////            entretienService.ajouterwithId_offre(entretien, idOffre);
//////        } catch (Exception e) {
//////            e.printStackTrace();
//////        }
////
////
////        System.out.println(entretienService.afficher());
//
//        EntretienService service = new EntretienService();
//
//        int ancienIdEntretien = 3;
//
//
//
//        service.updateEntretienWithId_offre(ancienIdEntretien, entretien);
//
//        Entretien entretienMisAJour = service.getEntretienById(ancienIdEntretien);
//        if (entretienMisAJour != null) {
//            System.out.println("Entretien mis à jour : " + entretienMisAJour);
//        } else {
//            System.out.println("L'entretien n'existe pas !");
//        }

        User user= new User();

        user = user.Candidat(
                "Smith",
                "James",
                "nn.smith24@gmail.com",
                "Candid@t3Pass",
                "12/11/1992",
                Sexe.HOMME,
                "Los Angeles",
                750.50
        );
ServiceUser u=new ServiceUser();

try {
    System.out.println(user);
    u.ajouter(user);
    user.setRole(Role.CANDIDAT);
    user.setSexe(Sexe.HOMME);
    user.setSalaire(1500.00);
    user.setExperienceTravail(5);
    user.setDepartement("it");
    user.setCompetence("no comp");
    user.setIdUser(48);
u.changetoEmploye(user);


} catch (Exception e) {
    throw new RuntimeException(e);
}


//        try {
//            EntretienService service = new EntretienService();
//
//            int idOffre = 1;
//
//            Entretien nouvelEntretien = new Entretien(
//                    "Entretien RH",
//                    "Discussion sur le parcours du candidat",
//                    new java.util.Date(),
//                    new Time(System.currentTimeMillis()),
//                    TypeEntretien.EN_PRESENTIEL,
//                    false,
//                    2,
//                    4,
//                    1,
//                    0
//            );
//
//            service.ajouterEntretienAvecCandidature(nouvelEntretien, idOffre);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }















    }



















