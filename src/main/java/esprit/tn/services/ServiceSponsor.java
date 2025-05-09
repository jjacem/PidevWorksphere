package esprit.tn.services;

import esprit.tn.entities.Classement;
import esprit.tn.entities.DureeContrat;
import esprit.tn.entities.EvenementSponsor;
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




    public void ajouterEvenementASponsor(int sponsorId, int evenementId, Date datedebutContrat, String duree) throws SQLException {
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

        // Insérer l'événement au sponsor avec les nouveaux attributs
        String query = "INSERT INTO evenement_sponsor (evenement_id, sponsor_id, datedebutContrat, duree) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, evenementId);
            preparedStatement.setInt(2, sponsorId);
            preparedStatement.setDate(3, new java.sql.Date(datedebutContrat.getTime()));
            preparedStatement.setString(4, duree);
            preparedStatement.executeUpdate();
            System.out.println("Événement ajouté au sponsor avec les nouveaux attributs !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'événement au sponsor: " + e.getMessage());
        }
    }


    public boolean isEventAssociatedWithSponsor(int sponsorId, int eventId) throws SQLException {
        String query = "SELECT COUNT(*) FROM evenement_sponsor WHERE sponsor_id = ? AND evenement_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sponsorId);
            preparedStatement.setInt(2, eventId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Retourne true si une association existe
            }
        }
        return false;
    }

    public void updateAssociation(int sponsorId, int eventId, Date datedebutContrat, String duree) throws SQLException {
        String query = "UPDATE evenement_sponsor SET datedebutContrat = ?, duree = ? WHERE sponsor_id = ? AND evenement_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, datedebutContrat);
            preparedStatement.setString(2, duree);
            preparedStatement.setInt(3, sponsorId);
            preparedStatement.setInt(4, eventId);
            preparedStatement.executeUpdate();
            System.out.println("Association mise à jour avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de l'association : " + e.getMessage());
            throw e;
        }
    }
    // Méthode pour ajouter un sponsor


    @Override
    public void ajouter(Sponsor sponsor) throws SQLException {
        String query = "INSERT INTO sponsor (nomSponso, prenomSponso, emailSponso, budgetSponso, secteurSponsor) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, sponsor.getNomSponso());
            preparedStatement.setString(2, sponsor.getPrenomSponso());
            preparedStatement.setString(3, sponsor.getEmailSponso());
            preparedStatement.setDouble(4, sponsor.getBudgetSponso());
            preparedStatement.setString(5, sponsor.getSecteurSponsor());

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
                        resultSet.getDouble("budgetSponso"),
                        resultSet.getString("secteurSponsor")
                );
                sponsor.setIdSponsor(resultSet.getInt("idSponsor"));
                // Récupérer et attribuer le classement
                String classementStr = resultSet.getString("classement");
                if (classementStr != null) {
                    sponsor.setClassement(Classement.valueOf(classementStr));
                }
                sponsors.add(sponsor);
            }
        }
        return sponsors;
    }
    /*@Override
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
    }*/


    @Override
    public void modifier(Sponsor sponsor) throws SQLException {
        String query = "UPDATE sponsor SET nomSponso = ?, prenomSponso = ?, emailSponso = ?, budgetSponso = ?, secteurSponsor = ? WHERE idSponsor = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, sponsor.getNomSponso());
            preparedStatement.setString(2, sponsor.getPrenomSponso());
            preparedStatement.setString(3, sponsor.getEmailSponso());
            preparedStatement.setDouble(4, sponsor.getBudgetSponso());
            preparedStatement.setString(5, sponsor.getSecteurSponsor());
            preparedStatement.setInt(6, sponsor.getIdSponsor());

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



    public void mettreAJourBudgetApresReduction(int idSponsor, double budgetApresReduction) throws SQLException {
        String query = "UPDATE sponsor SET BudgetApresReduction = ? WHERE idSponsor = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, budgetApresReduction);
            stmt.setInt(2, idSponsor);
            stmt.executeUpdate();
        }
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
        String query = "SELECT e.nomEvent, es.datedebutContrat, es.duree FROM evennement e " +
                "JOIN evenement_sponsor es ON e.idEvent = es.evenement_id " +
                "WHERE es.sponsor_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sponsorId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String eventName = resultSet.getString("nomEvent");
                Date datedebutContrat = resultSet.getDate("datedebutContrat");
                String duree = resultSet.getString("duree");
                eventNames.add(eventName + " (Début: " + datedebutContrat + ", Durée: " + duree + ")");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des événements sponsorisés : " + e.getMessage());
        }

        return eventNames;
    }


    public void mettreAJourClassement(int idSponsor, Classement classement) throws SQLException {
        String req = "UPDATE sponsor SET classement=? WHERE idSponsor=?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, classement.toString());
            pst.setInt(2, idSponsor);
            pst.executeUpdate();
            System.out.println("Classement du sponsor mis à jour avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du classement du sponsor : " + e.getMessage());
            throw e;
        }
    }




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


    public String getSponsorEmailById(int sponsorId) throws SQLException {
        String query = "SELECT emailSponso FROM sponsor WHERE idSponsor = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sponsorId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("emailSponso");
            } else {
                throw new SQLException("Aucun sponsor trouvé avec l'ID : " + sponsorId);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'e-mail du sponsor : " + e.getMessage());
            throw e;
        }
    }
}
