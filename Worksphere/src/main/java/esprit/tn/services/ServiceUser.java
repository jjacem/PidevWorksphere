package esprit.tn.services;

import esprit.tn.entities.User;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.List;

public class ServiceUser implements IService<User> {
    Connection connection;
    public ServiceUser(){
        connection= MyDatabase.getInstance().getConnection();

    }

    /*@Override
    public void ajouter(User user) throws SQLException {
        String req = "INSERT INTO User (nom, email) " +
                "VALUES ('" + user.getNom() + "', '" + user.getEmail() + "')";



        Statement statement=connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("user ajoute");
    }
*/
    public void ajouter(User user) throws SQLException {
        String query = "INSERT INTO user (nom, email) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getNom());
            preparedStatement.setString(2, user.getEmail());

            preparedStatement.executeUpdate();

            // Récupérer l'ID généré
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
            }

            System.out.println("Utilisateur ajouté avec ID: " + user.getId());
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'utilisateur: " + e.getMessage());
        }
    }

    @Override
    public void modifier(User user) throws SQLException {

    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    @Override
    public List<User> afficher() throws SQLException {
        return List.of();
    }
}
