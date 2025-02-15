package esprit.tn.tests;

import esprit.tn.entities.*;
import esprit.tn.services.ServiceEquipe;
import esprit.tn.services.ServiceProjet;

import java.sql.SQLException;
import java.util.*;
public class Main {
    public static void main(String[] args) {
        ServiceEquipe serviceEquipe = new ServiceEquipe();
        ServiceProjet serviceProjet = new ServiceProjet();

        //User user1 = new User(1, "sellami", "asma", Role.EMPLOYE);
        //User user2 = new User(2, "gharbi", "molka", Role.EMPLOYE);
        //User user3 = new User(3, "jouili", "jacem", Role.EMPLOYE);
        User user4 = new User(4, "kassous", "eya", Role.MANAGER);

        // Créer une équipe avec des utilisateurs ayant des rôles
        Equipe equipe = new Equipe(1, "DEVOPS", Arrays.asList(user4));

        Projet projet = new Projet(2 ,"PROJET-TEST", "desctest",
                java.sql.Date.valueOf("2025-02-09"),
                java.sql.Date.valueOf("2026-02-12"),
                EtatProjet.Annulé,
                equipe);

        try {
            /*** Ajouter/modifier/supprimer/afficher équipe ***/

            serviceEquipe.ajouterEquipe(equipe);
            //serviceEquipe.modifierEquipe(equipe);
           // serviceEquipe.supprimerEquipe(2);
            System.out.println(serviceEquipe.afficherEquipe());

            /*** Ajouter/modifier/supprimer/afficher projet  **/

             //serviceProjet.ajouterProjet(projet);
            //serviceProjet.modifierProjet(projet);
            //serviceProjet.supprimerProjet(4);
            System.out.println(serviceProjet.afficherProjet());

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}