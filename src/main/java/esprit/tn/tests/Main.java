package esprit.tn.tests;

import esprit.tn.entities.Role;
import esprit.tn.entities.Sponsor;
import esprit.tn.entities.User;
import esprit.tn.entities.Evenement;
import esprit.tn.services.ServiceEvenement;
import esprit.tn.services.ServiceSponsor;
import esprit.tn.services.ServiceUser;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class Main {


    //ajouter user +event
 /* public static void main(String[] args) {
        ServiceUser serviceUser = new ServiceUser();
        ServiceEvenement serviceEvenement = new ServiceEvenement();

        try {
            // Création et ajout de l'utilisateur
            User user3 = new User(
                    "Eya", "Kassous", "eya5@gmail.com", "password123", Role.EMPLOYE,
                    "Tunis", "Femme", "profile1.jpg", "Candidature",
                    2500.0, "Développeur", 3000.0, 2,
                    "Informatique", List.of("Java", "Symfony", "FlutterFlow"),
                    5, 10000.0, "N/A", 2, "Développement Web"
            );

            User user5 = new User(
                    "Molka", "Ben Ali", "molka@gmail.com", "securePass456", Role.MANAGER,
                    "Ariana", "Femme", "profile2.jpg", "Candidature",
                    4000.0, "Chef de projet", 5000.0, 5,
                    "Gestion de projet", List.of("Gestion", "Scrum", "Leadership"),
                    10, 50000.0, "Informatique", 5, "Gestion agile"
            );

            serviceUser.ajouter(user3);  // L'ID sera maintenant généré et mis à jour dans l'objet


            // Création d'un événement
            Evenement event2 = new Evenement("Conférence IT2", "Une conférence sur les nouvelles technologies",
                    new Date(), "Tunis", 100, user3, user3.getIdUser());

            // Ajout de l'événement
            serviceEvenement.ajouter(event2);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
*/
//test supprimer event:
       /*public static void main(String[] args) {
            ServiceEvenement serviceEvenement = new ServiceEvenement();

            try {
                int idEventToDelete = 11; // Remplace par l'ID de l'événement à supprimer
                serviceEvenement.supprimer(idEventToDelete);
            } catch (SQLException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }*/


    //tester modifier event
  /* public static void main(String[] args) {
        ServiceEvenement serviceEvenement = new ServiceEvenement();

        try {
            int idEventToUpdate = 1; // Remplace par l'ID de l'événement à modifier
            Evenement updatedEvent = new Evenement("Nouveau Nom", "Nouvelle Description", new Date(), "Nouvelle Adresse", 200, null, 7);
            updatedEvent.setIdEvent(idEventToUpdate); // Définit l'ID de l'événement existant

            serviceEvenement.modifier(updatedEvent);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }*/

    //ajout sponsor + liaison+delete
   /*public static void main(String[] args) throws SQLException {

        ServiceSponsor serviceSponsor = new ServiceSponsor();
        Sponsor sponsor1 = new Sponsor("Orange", "Telecom", "contact@orange.tn", 50000.0);
        //serviceSponsor.ajouter(sponsor1);

        try {

            // Vous pouvez é'galement tester la liaison inverse
           // serviceSponsor.ajouterEvenementASponsor(6, 6);
           serviceSponsor.supprimer(3);


        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }*/

    //modifier sponsor

       /*  public static void main(String[] args) {
        ServiceSponsor serviceSponsor = new ServiceSponsor();

        try {
            int idSponsorToUpdate = 4; // Remplace par l'ID de l'événement à modifier

            Sponsor updatedSponsor = new Sponsor("Nouveau Nom", "Nouveau preNom", "newmail@gmail.com", 200);
            updatedSponsor.setIdSponsor(idSponsorToUpdate); // Définit l'ID de l'événement existant


            serviceSponsor.modifier(updatedSponsor);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }*/

    //afficher les sponsors:
        /*public static void main(String[] args) throws SQLException {
            ServiceSponsor serviceSponsor=new ServiceSponsor();
            List<Sponsor> sponsors = serviceSponsor.afficher();
            for (Sponsor s : sponsors) {
                System.out.println(s.toString());
            }

        }*/
    /*public static void main(String[] args) throws SQLException {
        ServiceEvenement serviceEvenement=new ServiceEvenement();
        List<Evenement> evenements = serviceEvenement.afficher();
        for (Evenement e : evenements) {
            System.out.println(e.toString());
        }

    }*/

    //mail sponsors + nom event
   /* public static void main(String[] args) throws SQLException {

        ServiceEvenement serviceEvenement = new ServiceEvenement();
        ServiceSponsor serviceSponsor = new ServiceSponsor();


        System.out.println("sponsors mails: "+serviceSponsor.getSponsorEmails());

        System.out.println("evenet names: "+serviceEvenement.getEventNames());

        System.out.println("get event name of event 6: "+serviceEvenement.getEventNameById(6));
        System.out.println("get email of sponsor 4: "+serviceSponsor.getSponsorEmailById(4));

        // Test de la méthode getEventNamesBySponsor avec un sponsorId spécifique
        int sponsorId = 4;  // Remplace par un sponsorId existant
        System.out.println("Event names sponsored by sponsor " + sponsorId + ": " +
                serviceSponsor.getEventNamesBySponsor(sponsorId));

        //supprimerAssociationSponsorEvenement
        serviceEvenement.supprimerAssociationSponsorEvenement(4,7);
    }*/



    //removeEventFromSponsor

    public static void main(String[] args) {
        ServiceSponsor serviceSponsor = new ServiceSponsor();

        try {
            int sponsorId = 4; // Remplace avec un ID de sponsor existant
            int eventId = 9; // Remplace avec un ID d'événement existant

            serviceSponsor.removeEventFromSponsor(sponsorId, eventId);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

}
