package esprit.tn.services;

import esprit.tn.entities.Evenement;
import esprit.tn.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ServiceEvenement implements IService<Evenement> {
    Connection connection;

    public ServiceEvenement() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Evenement evenement) throws SQLException {
        String query = "INSERT INTO evenement (nomEvent, descEvent, dateEvent, lieuEvent, capaciteEvent, RHgestionnaireId) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, evenement.getNomEvent());
            preparedStatement.setString(2, evenement.getDescEvent());
            preparedStatement.setDate(3, new java.sql.Date(evenement.getDateEvent().getTime()));
            preparedStatement.setString(4, evenement.getLieuEvent());
            preparedStatement.setInt(5, evenement.getCapaciteEvent());
            preparedStatement.setInt(6, evenement.getRHgestionnaireId());

            preparedStatement.executeUpdate();
            System.out.println("Événement ajouté avec succès!");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'événement: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Evenement evenement) throws SQLException {
        String query = "UPDATE evenement SET nomEvent = ?, descEvent = ?, dateEvent = ?, lieuEvent = ?, capaciteEvent = ?, RHgestionnaireId = ? WHERE idEvent = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, evenement.getNomEvent());
            preparedStatement.setString(2, evenement.getDescEvent());
            preparedStatement.setDate(3, new java.sql.Date(evenement.getDateEvent().getTime())); // Conversion de Date en java.sql.Date
            preparedStatement.setString(4, evenement.getLieuEvent());
            preparedStatement.setInt(5, evenement.getCapaciteEvent());
            preparedStatement.setInt(6, evenement.getRHgestionnaireId());
            preparedStatement.setInt(7, evenement.getIdEvent());

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Événement mis à jour avec succès !");
            } else {
                System.out.println("Aucun événement trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de l'événement : " + e.getMessage());
        }
    }




    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM evenement WHERE idEvent = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Événement supprimé avec succès !");
            } else {
                System.out.println("Aucun événement trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'événement : " + e.getMessage());
        }
    }


    @Override
    public List<Evenement> afficher() throws SQLException {
        return List.of();
    }
}
