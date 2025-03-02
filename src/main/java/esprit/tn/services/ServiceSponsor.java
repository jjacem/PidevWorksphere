package esprit.tn.services;

import esprit.tn.entities.Sponsor;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceSponsor implements IService<Sponsor> {
    public Connection connection;

    public ServiceSponsor() {
        connection = MyDatabase.getInstance().getConnection();
    }

    public void ajouterEvenementASponsor(int sponsorId, int evenementId) throws SQLException {
        // Vérification de l'existence de l'événement
        //?:
        //Ils servent de paramètres dynamiques qui seront remplacés par des valeurs via setString(), setInt(), etc.
        String checkEventQuery = "SELECT COUNT(*) FROM evennement WHERE idEvent = ?";
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
    @Override

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

    @Override

    public void supprimer(int idSponsor) throws SQLException {
        String query = "DELETE FROM sponsor WHERE idSponsor = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idSponsor);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sponsor supprimé avec succès !");
            } else {
                System.out.println("Aucun sponsor trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du sponsor : " + e.getMessage());
        }
    }


    @Override
    public List<Sponsor> afficher() throws SQLException {
        List<Sponsor> sponsors = new ArrayList<>();
        String query = "SELECT * FROM sponsor";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Sponsor sponsor = new Sponsor(
                        resultSet.getString("nomSponso"),
                        resultSet.getString("prenomSponso"),
                        resultSet.getString("emailSponso"),
                        resultSet.getDouble("budgetSponso")
                );
                sponsor.setIdSponsor(resultSet.getInt("idSponsor"));
                sponsors.add(sponsor);
            }
        }
        return sponsors;
    }
    @Override

    public void modifier(Sponsor sponsor) throws SQLException {
        String query = "UPDATE sponsor SET nomSponso = ?, prenomSponso = ?, emailSponso = ?, budgetSponso = ? WHERE idSponsor = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, sponsor.getNomSponso());
            preparedStatement.setString(2, sponsor.getPrenomSponso());
            preparedStatement.setString(3, sponsor.getEmailSponso());
            preparedStatement.setDouble(4, sponsor.getBudgetSponso());
            preparedStatement.setInt(5, sponsor.getIdSponsor());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sponsor modifié avec succès !");
            } else {
                System.out.println("Aucun sponsor trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du sponsor : " + e.getMessage());
        }
    }

    public List<Integer> getSponsorIds() {
        List<Integer> sponsorIds = new ArrayList<>();
        String query = "SELECT idSponsor FROM sponsor";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) { // Use the existing connection

            while (resultSet.next()) {
                sponsorIds.add(resultSet.getInt("idSponsor"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des IDs des sponsors: " + e.getMessage());
        }

        return sponsorIds;
    }


    public List<String> getSponsorEmails() {
        List<String> sponsorEmails = new ArrayList<>();
        String query = "SELECT emailSponso FROM sponsor";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) { // Utiliser la connexion existante

            while (resultSet.next()) {
                sponsorEmails.add(resultSet.getString("emailSponso"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des emails des sponsors: " + e.getMessage());
        }

        return sponsorEmails;
    }
    public Integer getSponsorIdByEmail(String sponsorEmail) throws SQLException {
        String query = "SELECT idSponsor FROM sponsor WHERE emailSponso = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, sponsorEmail);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("idSponsor");
            }
        }
        return null; // Retourner null si aucun résultat trouvé
    }




    public Sponsor getSponsorByName(String sponsorName) throws SQLException {
        String query = "SELECT * FROM sponsor WHERE nomSponso = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, sponsorName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Sponsor sponsor = new Sponsor();
                sponsor.setIdSponsor(resultSet.getInt("idSponsor"));
                sponsor.setNomSponso(resultSet.getString("nomSponso"));
                return sponsor;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du sponsor par nom : " + e.getMessage());
        }
        return null;
    }

    public List<String> getEventNamesBySponsor(int sponsorId) throws SQLException {
        List<String> eventNames = new ArrayList<>();
        String query = "SELECT e.nomEvent FROM evennement e " +
                "JOIN evenement_sponsor es ON e.idEvent = es.evenement_id " +
                "WHERE es.sponsor_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sponsorId); // On passe l'id du sponsor à la requête
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                eventNames.add(resultSet.getString("nomEvent")); // Ajout du nom de l'événement à la liste
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des événements sponsorisés : " + e.getMessage());
        }

        return eventNames; // Retourne la liste des noms d'événements
    }




    public void supprimerAssociationEventSponsor(int sponsorId, int evenementId) throws SQLException {
        // Vérification de l'existence de l'événement
        String checkEventQuery = "SELECT COUNT(*) FROM evennement WHERE idEvent = ?";
        try (PreparedStatement psCheckEvent = connection.prepareStatement(checkEventQuery)) {
            psCheckEvent.setInt(1, evenementId);
            ResultSet rs = psCheckEvent.executeQuery();
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

        // Suppression de l'association dans la table evenement_sponsor
        String deleteAssociationQuery = "DELETE FROM evenement_sponsor WHERE evenement_id = ? AND sponsor_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteAssociationQuery)) {
            preparedStatement.setInt(1, evenementId);
            preparedStatement.setInt(2, sponsorId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Association supprimée avec succès !");
            } else {
                System.out.println("Aucune association trouvée pour cet événement et ce sponsor.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'association : " + e.getMessage());
        }
    }



    /*public void removeEventFromSponsor(int sponsorId, int eventId) throws SQLException {
        String query = "DELETE FROM evenement_sponsor WHERE evenement_id = ? AND sponsor_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, eventId);
            preparedStatement.setInt(2, sponsorId);
            preparedStatement.executeUpdate();
            System.out.println("Association sponsor-événement supprimée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'association : " + e.getMessage());
        }
    }*/
    public void removeEventFromSponsor(int sponsorId, int eventId) throws SQLException {
        String query = "DELETE FROM evenement_sponsor WHERE sponsor_id = ? AND evenement_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sponsorId);
            preparedStatement.setInt(2, eventId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Association supprimée avec succès !");
            } else {
                System.out.println("Aucune association trouvée.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'association : " + e.getMessage());
        }
    }

    public int getEventIdByName(String eventName) throws SQLException {
        String query = "SELECT idEvent FROM evennement WHERE nomEvent = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, eventName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("idEvent");
            }
        }
        throw new SQLException("Événement non trouvé : " + eventName);
    }
}
