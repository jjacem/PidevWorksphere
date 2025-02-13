package test;

import entities.*;
import service.ServiceEquipe;
import service.ServiceProjet;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
public class Main {
    public static void main(String[] args) {
        ServiceEquipe serviceEquipe = new ServiceEquipe();
        ServiceProjet serviceProjet = new ServiceProjet();

        Equipe equipe = new Equipe(37, "devops", Arrays.asList(
                new Employee(1, "sellami", "asma"),
                //new Employee(2, "gharbi", "molka"),
                new Employee(3, "jouili", "jacem")
        ));

        Projet projet = new Projet(10 ,"PROJET-TEST", "desctest",
                java.sql.Date.valueOf("2025-02-09"),
                java.sql.Date.valueOf("2026-02-12"),
                equipe);

        try {
            /*** Ajouter/modifier/supprimer/afficher équipe ***/

            //serviceEquipe.ajouterEquipe(equipe);
            //serviceEquipe.modifierEquipe(equipe);
            //serviceEquipe.supprimerEquipe(37);
            System.out.println(serviceEquipe.afficherEquipe());

            /*** Ajouter/modifier/supprimer/afficher projet  **/

             //serviceProjet.ajouterProjet(projet);
            //serviceProjet.modifierProjet(projet);
            //serviceProjet.supprimerProjet(11);
            System.out.println(serviceProjet.afficherProjet());

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}