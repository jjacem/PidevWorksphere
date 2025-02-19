package esprit.tn.services;

import esprit.tn.entities.Formation;
import esprit.tn.entities.Typeformation;
import esprit.tn.entities.User;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceFormation implements IServiceFormation <Formation>  {
    Connection connection;
    public ServiceFormation() {
        connection= MyDatabase.getInstance().getConnection();
    }


    @Override
    public void ajouterFormation(Formation formation) throws SQLException {
        String req = "INSERT INTO formation (titre, description, date, heure_debut, heure_fin, nb_place, type , photo , id_user) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ? , ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            // Affecter les valeurs aux paramètres de la requête
            preparedStatement.setString(1, formation.getTitre());
            preparedStatement.setString(2, formation.getDescription());
            preparedStatement.setDate(3, java.sql.Date.valueOf(formation.getDate()));
            preparedStatement.setTime(4, java.sql.Time.valueOf(formation.getHeure_debut()));
            preparedStatement.setTime(5, java.sql.Time.valueOf(formation.getHeure_fin()));
            preparedStatement.setInt(6, formation.getNb_place());
            preparedStatement.setString(7, formation.getType().toString());
            preparedStatement.setString(8, formation.getPhoto().toString());
            preparedStatement.setInt(9, formation.getId_user());

            // Exécuter la mise à jour
            preparedStatement.executeUpdate();
            System.out.println("Formation ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la formation : " + e.getMessage());
        }
    }

    @Override
    public void modifierFormation(Formation formation) throws SQLException {
        String req = "UPDATE formation SET titre = ?, description = ?, date = ?, heure_debut = ?, heure_fin = ?, nb_place = ?, type = ?, photo = ?, id_user = ? WHERE id_f = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);

        preparedStatement.setString(1, formation.getTitre());
        preparedStatement.setString(2, formation.getDescription());
        preparedStatement.setDate(3, java.sql.Date.valueOf(formation.getDate()));
        preparedStatement.setTime(4, java.sql.Time.valueOf(formation.getHeure_debut()));
        preparedStatement.setTime(5, java.sql.Time.valueOf(formation.getHeure_fin()));
        preparedStatement.setInt(6, formation.getNb_place());
        preparedStatement.setString(7, formation.getType().toString());
        preparedStatement.setString(8, formation.getPhoto() != null ? formation.getPhoto().toString() : null);
        preparedStatement.setInt(9, formation.getId_user());
        preparedStatement.setInt(10, formation.getId_f());

        preparedStatement.executeUpdate();
        System.out.println("Formation mise à jour avec succès.");
        preparedStatement.close();
    }

    @Override
    public void supprimeFormation(Formation formation) throws SQLException {
        String req = "DELETE FROM formation WHERE id_f = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, formation.getId_f());
        preparedStatement.executeUpdate();
        System.out.println("Formation supprimée avec succès.");
        preparedStatement.close();
    }

    @Override
    public List<Formation> getListFormation() throws SQLException {
        List<Formation> formations = new ArrayList<>();

        String req = "SELECT f.id_f, f.titre, f.description, f.date, f.heure_debut, f.heure_fin, " +
                "f.nb_place, f.type, f.photo, u.id_user, u.nom, u.prenom, u.email " +
                "FROM formation f " +
                "JOIN user u ON f.id_user = u.id_user";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            // Création de l'objet Formation
            Formation f = new Formation();
            f.setId_f(rs.getInt("id_f"));
            f.setTitre(rs.getString("titre"));
            f.setDescription(rs.getString("description"));
            f.setDate(rs.getDate("date").toLocalDate());
            f.setHeure_debut(rs.getTime("heure_debut").toLocalTime());
            f.setHeure_fin(rs.getTime("heure_fin").toLocalTime());
            f.setNb_place(rs.getInt("nb_place"));

            // Gestion du type
            String typeStr = rs.getString("type");
            if (typeStr != null) {
                f.setType(Typeformation.valueOf(typeStr));
            }

            f.setPhoto(rs.getURL("photo"));

            // Création de l'objet User
            User user = new User();
            user.setIdUser(rs.getInt("id_user"));
            user.setNom(rs.getString("nom"));
            user.setPrenom(rs.getString("prenom"));
            user.setEmail(rs.getString("email"));

            // Associer le User à la Formation
            f.setUser(user);

            formations.add(f);
        }

        return formations;
    }

    @Override
    public Formation getFormationById(int id) throws SQLException {
        String req = "SELECT f.id_f, f.titre, f.description, f.date, f.heure_debut, f.heure_fin, " +
                "f.nb_place, f.type, f.photo, u.id_user, u.nom, u.prenom, u.email " +
                "FROM formation f " +
                "JOIN user u ON f.id_user = u.id_user " +
                "WHERE f.id_f = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                // Création de l'objet Formation
                Formation f = new Formation();
                f.setId_f(rs.getInt("id_f"));
                f.setTitre(rs.getString("titre"));
                f.setDescription(rs.getString("description"));
                f.setDate(rs.getDate("date").toLocalDate());
                f.setHeure_debut(rs.getTime("heure_debut").toLocalTime());
                f.setHeure_fin(rs.getTime("heure_fin").toLocalTime());
                f.setNb_place(rs.getInt("nb_place"));

                // Gestion du type
                String typeStr = rs.getString("type");
                if (typeStr != null) {
                    f.setType(Typeformation.valueOf(typeStr));
                }

                f.setPhoto(rs.getURL("photo"));

                // Création de l'objet User
                User user = new User();
                user.setIdUser(rs.getInt("id_user"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));


                f.setUser(user);

                return f;
            }
        }

        return null;
    }


}
