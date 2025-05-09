package esprit.tn.services;

import esprit.tn.entities.Certifie;
import esprit.tn.entities.Formation;
import esprit.tn.entities.Typeformation;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFormation implements IServiceFormation<Formation> {
    Connection connection;

    public ServiceFormation() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouterFormation(Formation formation) throws SQLException {
        String req = "INSERT INTO formation (titre, description, date, nb_place, type, photo, id_user, certifie, langue) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, formation.getTitre());
            preparedStatement.setString(2, formation.getDescription());
            preparedStatement.setDate(3, Date.valueOf(formation.getDate()));
            preparedStatement.setInt(4, formation.getNb_place());
            preparedStatement.setString(5, formation.getType().toString());
            preparedStatement.setString(6, formation.getPhoto());
            preparedStatement.setInt(7, formation.getId_user());
            preparedStatement.setString(8, formation.getCertifie().toString());
            preparedStatement.setString(9, formation.getLangue());

            preparedStatement.executeUpdate();
            System.out.println("Formation ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la formation : " + e.getMessage());
        }
    }

    @Override
    public void modifierFormation(Formation formation) throws SQLException {
        String req = "UPDATE formation SET titre = ?, description = ?, date = ?, nb_place = ?, type = ?, photo = ?, id_user = ?, certifie = ?, langue = ? WHERE id_f = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, formation.getTitre());
            preparedStatement.setString(2, formation.getDescription());
            preparedStatement.setDate(3, Date.valueOf(formation.getDate()));
            preparedStatement.setInt(4, formation.getNb_place());
            preparedStatement.setString(5, formation.getType().toString());
            preparedStatement.setString(6, formation.getPhoto());
            preparedStatement.setInt(7, formation.getId_user());
            preparedStatement.setString(8, formation.getCertifie().toString());
            preparedStatement.setString(9, formation.getLangue());
            preparedStatement.setInt(10, formation.getId_f());

            preparedStatement.executeUpdate();
            System.out.println("Formation mise à jour avec succès.");
        }
    }

    @Override
    public void supprimeFormation(Formation formation) throws SQLException {
        String req = "DELETE FROM formation WHERE id_f = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, formation.getId_f());
            preparedStatement.executeUpdate();
            System.out.println("Formation supprimée avec succès.");
        }
    }

    @Override
    public List<Formation> getListFormation() throws SQLException {
        List<Formation> formations = new ArrayList<>();
        String req = "SELECT * FROM formation";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Formation f = new Formation();
                f.setId_f(rs.getInt("id_f"));
                f.setTitre(rs.getString("titre"));
                f.setDescription(rs.getString("description"));
                f.setDate(rs.getDate("date").toLocalDate());
                f.setNb_place(rs.getInt("nb_place"));

                String typeStr = rs.getString("type");
                if (typeStr != null) {
                    f.setType(Typeformation.valueOf(typeStr));
                }

                f.setPhoto(rs.getString("photo"));
                f.setId_user(rs.getInt("id_user"));

                String certifieStr = rs.getString("certifie");
                if (certifieStr != null) {
                    f.setCertifie(Certifie.valueOf(certifieStr));
                }

                f.setLangue(rs.getString("langue"));

                formations.add(f);
            }
        }

        return formations;
    }

    @Override
    public Formation getFormationById(int id) throws SQLException {
        String req = "SELECT * FROM formation WHERE id_f = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                Formation f = new Formation();
                f.setId_f(rs.getInt("id_f"));
                f.setTitre(rs.getString("titre"));
                f.setDescription(rs.getString("description"));
                f.setDate(rs.getDate("date").toLocalDate());
                f.setNb_place(rs.getInt("nb_place"));

                String typeStr = rs.getString("type");
                if (typeStr != null) {
                    f.setType(Typeformation.valueOf(typeStr));
                }

                f.setPhoto(rs.getString("photo"));
                f.setId_user(rs.getInt("id_user"));

                String certifieStr = rs.getString("certifie");
                if (certifieStr != null) {
                    f.setCertifie(Certifie.valueOf(certifieStr));
                }

                f.setLangue(rs.getString("langue"));

                return f;
            }
        }

        return null;
    }

    public int getNombrePlaces(int formationId) throws SQLException {
        String query = "SELECT nb_place FROM formation WHERE id_f = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, formationId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("nb_place");
            } else {
                throw new SQLException("Formation non trouvée avec l'ID : " + formationId);
            }
        }
    }
}