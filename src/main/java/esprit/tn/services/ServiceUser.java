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
        String query = "INSERT INTO user (nom, prenom, email, mdp, role, adresse, sexe, image_profil, status, salaire_attendu, poste, salaire, experience_travail, departement, competence, nombreProjet, budget, departement_géré, ans_experience, specialisation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getNom());
            preparedStatement.setString(2, user.getPrenom());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getMdp());
            preparedStatement.setString(5, user.getRole().toString());
            preparedStatement.setString(6, user.getAdresse());
            preparedStatement.setString(7, user.getSexe());
            preparedStatement.setString(8, user.getImageProfil());
            preparedStatement.setString(9, user.getStatus());
            preparedStatement.setDouble(10, user.getSalaireAttendu());
            preparedStatement.setString(11, user.getPoste());
            preparedStatement.setDouble(12, user.getSalaire());
            preparedStatement.setInt(13, user.getExperienceTravail());
            preparedStatement.setString(14, user.getDepartement());
            preparedStatement.setString(15, String.join(",", user.getCompetence())); // Convertir List en String
            preparedStatement.setInt(16, user.getNombreProjet());
            preparedStatement.setDouble(17, user.getBudget());
            preparedStatement.setString(18, user.getDepartementGere());
            preparedStatement.setInt(19, user.getAnsExperience());
            preparedStatement.setString(20, user.getSpecialisation());

            preparedStatement.executeUpdate();

            // Récupérer l'ID généré
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setIdUser(generatedKeys.getInt(1));
            }

            System.out.println("Utilisateur ajouté avec ID: " + user.getIdUser());
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
