package tn.esprit;

import tn.esprit.entities.Candidature;
import tn.esprit.entities.OffreEmploi;
import tn.esprit.entities.User;
import tn.esprit.entities.Role;
import tn.esprit.services.ServiceCandidature;
import tn.esprit.services.ServiceOffre;

import java.sql.SQLException;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        ServiceOffre serviceOffre = new ServiceOffre();
        ServiceCandidature serviceCandidature = new ServiceCandidature();

        // Prepare an offer for testing ServiceOffre CRUD
        OffreEmploi offre = new OffreEmploi(1200, "Conseiller Commercial", "Initial description", "CDI", "Tunis", "ouverte", "0 à 1 an", new Date(), new Date());

        try {
            // Testing ServiceOffre CRUD
//            serviceOffre.ajouter(offre);
//            System.out.println("Offre ajoutée: " + offre);

            // Update offer
//            offre.setDescription("mise à jour du description");
//            offre.setSalaire(2000);
//            offre.setIdOffre(33);
//            serviceOffre.modifier(offre);
//            System.out.println("Offre modifiée: " + offre);

            // Display all offers
            System.out.println("All offers: " + serviceOffre.afficher());

            // Delete offer
            offre.setIdOffre(34);
            serviceOffre.supprimer(offre.getIdOffre());
            System.out.println("Offre supprimée avec succés");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Prepare a candidature for testing ServiceCandidature CRUD.
        offre.setIdOffre(33);
        User candidat = new User(1,"Jacem", "Jouili", "password", "jacem@gmail.com", Role.CANDIDAT);
        Candidature candidature = new Candidature(offre, candidat, "/cv.pdf", "Motivation letter");

        try {

            // Testing ServiceCandidature CRUD
//            serviceCandidature.ajouter(candidature);
//            System.out.println("Candidature ajoutée: " + candidature);

            // Update candidature
//            candidature.setIdCandidature(11);
//            candidature.setLettreMotivation("MLetter");
//            serviceCandidature.modifier(candidature);
//            System.out.println("Candidature updated: " + candidature);

            // Display all candidatures
           System.out.println("All candidatures: " + serviceCandidature.afficher());
//
//            // Delete candidature
//            candidature.setIdCandidature(7);
//            serviceCandidature.supprimer(candidature.getIdCandidature());
//            System.out.println("Candidature deleted successfully");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}