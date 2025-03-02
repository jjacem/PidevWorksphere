package esprit.tn.services;

import esprit.tn.entities.Evenement;
import esprit.tn.entities.Sponsor;
import esprit.tn.entities.User;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceEvenement implements IService<Evenement> {
    Connection connection;

    public ServiceEvenement() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override

    public void ajouter(Evenement evenement) throws SQLException {
        String query = "INSERT INTO evennement (nomEvent, descEvent, dateEvent, lieuEvent, capaciteEvent, id_user) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, evenement.getNomEvent());
            preparedStatement.setString(2, evenement.getDescEvent());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(evenement.getDateEvent()));
            preparedStatement.setString(4, evenement.getLieuEvent());
            preparedStatement.setInt(5, evenement.getCapaciteEvent());
            preparedStatement.setInt(6, evenement.getId_user());

            preparedStatement.executeUpdate();
            System.out.println("Événement ajouté avec succès!");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'événement: " + e.getMessage());
        }
    }


    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM evennement WHERE idEvent = ?";

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
   public void modifier(Evenement evenement) throws SQLException {
       String query = "UPDATE evennement SET nomEvent = ?, descEvent = ?, dateEvent = ?, lieuEvent = ?, capaciteEvent = ?, id_user = ? WHERE idEvent = ?";

       try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
           preparedStatement.setString(1, evenement.getNomEvent());
           preparedStatement.setString(2, evenement.getDescEvent());
           preparedStatement.setTimestamp(3, Timestamp.valueOf(evenement.getDateEvent())); // Utilisation de Timestamp
           preparedStatement.setString(4, evenement.getLieuEvent());
           preparedStatement.setInt(5, evenement.getCapaciteEvent());
           preparedStatement.setInt(6, evenement.getId_user());
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
    public List<Evenement> afficher() throws SQLException {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT e.*, u.nom, u.prenom FROM evennement e JOIN user u ON e.id_user = u.id_user";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                User u = new User();
                u.testRH("email"+resultSet.getInt("id_user")+ "@gmail.com");
                u.setIdUser(resultSet.getInt("id_user"));
                u.setNom(resultSet.getString("nom"));
                u.setPrenom(resultSet.getString("prenom"));

                Evenement evenement = new Evenement(
                        resultSet.getString("nomEvent"),
                        resultSet.getString("descEvent"),
                        resultSet.getTimestamp("dateEvent").toLocalDateTime(), // Récupération de LocalDateTime
                        resultSet.getString("lieuEvent"),
                        resultSet.getInt("capaciteEvent"),
                        u,
                        resultSet.getInt("id_user")
                );
                evenement.setIdEvent(resultSet.getInt("idEvent"));

                evenements.add(evenement);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des événements : " + e.getMessage());
        }

        return evenements;
    }





    public List<String> getEventNames() {
        List<String> eventNames = new ArrayList<>();
        String query = "SELECT nomEvent FROM evennement";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) { // Utiliser la connexion existante

            while (resultSet.next()) {
                eventNames.add(resultSet.getString("nomEvent"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des noms des événements: " + e.getMessage());
        }

        return eventNames;
    }


    public Integer getEventIdByName(String eventName) throws SQLException {
        String query = "SELECT idEvent FROM evennement WHERE nomEvent = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, eventName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("idEvent");
            }
        }
        return null; // Retourner null si aucun résultat trouvé
    }










}
