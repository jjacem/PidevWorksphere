package esprit.tn.tests;

import esprit.tn.entities.Sponsor;
import esprit.tn.entities.User;
import esprit.tn.entities.Evenement;
import esprit.tn.services.ServiceEvenement;
import esprit.tn.services.ServiceSponsor;
import esprit.tn.services.ServiceUser;

import java.sql.SQLException;
import java.util.Date;

public class Main {
   /* public static void main(String[] args) {
        ServiceUser serviceUser = new ServiceUser();
        ServiceEvenement serviceEvenement = new ServiceEvenement();

        try {
            // Création et ajout de l'utilisateur
            User user = new User("eya", "eya@gmail.com");
            User user2 = new User("molka", "molka@gmail.com");
            serviceUser.ajouter(user);  // L'ID sera maintenant généré et mis à jour dans l'objet
            serviceUser.ajouter(user2);
            // Vérifie que l'utilisateur a un ID valide
            if (user2.getId() == 0) {
                System.out.println("Erreur: L'ID de l'utilisateur est invalide !");
                return;
            }

            // Création d'un événement
            Evenement event = new Evenement("Conférence IT", "Une conférence sur les nouvelles technologies",
                    new Date(), "Tunis", 100, user2, user2.getId());

            // Ajout de l'événement
            serviceEvenement.ajouter(event);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }*/

//test supprimer event:
       /* public static void main(String[] args) {
            ServiceEvenement serviceEvenement = new ServiceEvenement();

            try {
                int idEventToDelete = 24; // Remplace par l'ID de l'événement à supprimer
                serviceEvenement.supprimer(idEventToDelete);
            } catch (SQLException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }*/


    //tester modifier
   /* public static void main(String[] args) {
        ServiceEvenement serviceEvenement = new ServiceEvenement();

        try {
            int idEventToUpdate = 13; // Remplace par l'ID de l'événement à modifier
            Evenement updatedEvent = new Evenement("Nouveau Nom", "Nouvelle Description", new Date(), "Nouvelle Adresse", 200, null, 2);
            updatedEvent.setIdEvent(idEventToUpdate); // Définit l'ID de l'événement existant

            serviceEvenement.modifier(updatedEvent);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }*/

    //ajout sponsor + liaison
    public static void main(String[] args) {
        ServiceUser serviceUser = new ServiceUser();
        ServiceEvenement serviceEvenement = new ServiceEvenement();
        ServiceSponsor serviceSponsor = new ServiceSponsor();

        try {

            // Vous pouvez également tester la liaison inverse
            serviceSponsor.ajouterEvenementASponsor(4, 15);


        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
