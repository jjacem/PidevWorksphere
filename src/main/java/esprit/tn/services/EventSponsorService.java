package esprit.tn.services;

import esprit.tn.entities.DureeContrat;
import esprit.tn.entities.EvenementSponsor;
import esprit.tn.entities.Sponsor;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventSponsorService implements IService<EvenementSponsor>{


    private Connection connection;

    public EventSponsorService(Connection connection) {
        this.connection = connection;
    }

    public EventSponsorService(){connection = MyDatabase.getInstance().getConnection();}


    @Override
    public void ajouter(EvenementSponsor evenementSponsor) throws SQLException {

    }

    @Override
    public void modifier(EvenementSponsor evenementSponsor) throws SQLException {

    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    /*@Override
    public List<EvenementSponsor> afficher() throws SQLException {
        List<EvenementSponsor> evenementsSponsors = new ArrayList<>();
        String query = "SELECT * FROM evenement_sponsor";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                EvenementSponsor evenementSponsor = new EvenementSponsor(
                        resultSet.getInt("evenement_id"),
                        resultSet.getInt("sponsor_id"),
                        resultSet.getDate("datedebutContrat"),
                        DureeContrat.valueOf(resultSet.getString("duree")) // Convertir la chaîne en enum
                );
                evenementsSponsors.add(evenementSponsor);
            }
        }

        if (evenementsSponsors.isEmpty()) {
            System.out.println("Aucune association événement-sponsor trouvée.");
        } else {
            System.out.println("Liste des associations événement-sponsor :");
            for (EvenementSponsor es : evenementsSponsors) {
                System.out.println(es);
            }
        }

        return evenementsSponsors;
    }*/
    @Override
    public List<EvenementSponsor> afficher() throws SQLException {
        List<EvenementSponsor> evenementsSponsors = new ArrayList<>();
        String query = "SELECT * FROM evenement_sponsor";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int evenementId = resultSet.getInt("evenement_id");
                int sponsorId = resultSet.getInt("sponsor_id");
                System.out.println("Récupération - Evenement ID: " + evenementId + ", Sponsor ID: " + sponsorId);

                EvenementSponsor evenementSponsor = new EvenementSponsor(
                        evenementId,
                        sponsorId,
                        resultSet.getDate("datedebutContrat"),
                        DureeContrat.valueOf(resultSet.getString("duree")) // Convertir la chaîne en enum
                );
                evenementsSponsors.add(evenementSponsor);
            }
        }

        if (evenementsSponsors.isEmpty()) {
            System.out.println("Aucune association événement-sponsor trouvée.");
        } else {
            System.out.println("Liste des associations événement-sponsor :");
            for (EvenementSponsor es : evenementsSponsors) {
                System.out.println(es);
            }
        }

        return evenementsSponsors;
    }


}