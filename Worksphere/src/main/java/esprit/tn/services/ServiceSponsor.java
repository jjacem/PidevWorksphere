package esprit.tn.services;

import esprit.tn.entities.Sponsor;
import esprit.tn.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceSponsor {

    Connection connection;

    public ServiceSponsor() {
        connection = MyDatabase.getInstance().getConnection();
    }
    public void ajouterEvenementASponsor(int sponsorId, int evenementId) throws SQLException {
        // Vérification de l'existence de l'événement
        //?:
        //Ils servent de paramètres dynamiques qui seront remplacés par des valeurs via setString(), setInt(), etc.
        String checkEventQuery = "SELECT COUNT(*) FROM evenement WHERE idEvent = ?";
        //PreparedStatement est indispensable pour écrire du code SQL sécurisé, performant et maintenable en Java.
        try (PreparedStatement psCheckEvent = connection.prepareStatement(checkEventQuery)) {
            psCheckEvent.setInt(1, evenementId);
            //// Exécuter la requête et récupérer le résultat
            ResultSet rs = psCheckEvent.executeQuery();
            // Vérifier si l'événement existe (doit retourner au moins 1 ligne)
            if (!rs.next() || rs.getInt(1) == 0) {
                System.out.println("Erreur : L'événement n'existe pas.");
                return;
            }
        }

        // Vérification de l'existence du sponsor
        String checkSponsorQuery = "SELECT COUNT(*) FROM sponsor WHERE idSponsor = ?";
        try (PreparedStatement psCheckSponsor = connection.prepareStatement(checkSponsorQuery)) {
            psCheckSponsor.setInt(1, sponsorId);
            ResultSet rs = psCheckSponsor.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0) {
                System.out.println("Erreur : Le sponsor n'existe pas.");
                return;
            }
        }

        // Insérer l'événement au sponsor
        String query = "INSERT INTO evenement_sponsor (evenement_id, sponsor_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, evenementId);
            preparedStatement.setInt(2, sponsorId);
            preparedStatement.executeUpdate();
            System.out.println("Événement ajouté au sponsor !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'événement au sponsor: " + e.getMessage());
        }
    }


    // Méthode pour ajouter un sponsor
    public void ajouter(Sponsor sponsor) throws SQLException {
        String query = "INSERT INTO sponsor (nomSponso, prenomSponso, emailSponso, budgetSponso) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, sponsor.getNomSponso());
            preparedStatement.setString(2, sponsor.getPrenomSponso());
            preparedStatement.setString(3, sponsor.getEmailSponso());
            preparedStatement.setDouble(4, sponsor.getBudgetSponso());

            preparedStatement.executeUpdate();
            System.out.println("Sponsor ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du sponsor : " + e.getMessage());
        }
    }

    // Méthode pour lier un sponsor à un événement
    public void lierSponsorEvenement(int idSponsor, int idEvent) throws SQLException {
        String query = "INSERT INTO evenement_sponsor (idEvent, idSponsor) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idEvent);
            preparedStatement.setInt(2, idSponsor);

            preparedStatement.executeUpdate();
            System.out.println("Sponsor lié à l'événement avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la liaison du sponsor à l'événement : " + e.getMessage());
        }
    }
}
