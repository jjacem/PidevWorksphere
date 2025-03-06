package esprit.tn.services;

import esprit.tn.entities.Favoris;
import esprit.tn.entities.Formation;
import esprit.tn.entities.User;
import esprit.tn.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceFavori implements IServiceFavori<Favoris> {
    Connection connection;
    public ServiceFavori() {
        connection = MyDatabase.getInstance().getConnection();


    }

    @Override
    public void ajouterFavori(int userId, int formationId) throws SQLException {
        String query = "INSERT INTO favoris (id_user, id_f) VALUES (?, ?) ON DUPLICATE KEY UPDATE id_user=id_user";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setInt(2, formationId);
            pst.executeUpdate();
        }
    }

    @Override
    public void supprimerFavori(int userId, int formationId) throws SQLException {
        String query = "DELETE FROM favoris WHERE id_user = ? AND id_f = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setInt(2, formationId);
            pst.executeUpdate();
        }
    }

    @Override
    public boolean estFavori(int userId, int formationId) throws SQLException {
        String query = "SELECT COUNT(*) FROM favoris WHERE id_user = ? AND id_f = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setInt(2, formationId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public List<Favoris> getFavoris(int userId) throws SQLException {
        List<Favoris> favorisList = new ArrayList<>();
        String query = "SELECT f.*, fav.id_favori, u.* FROM formation f " +
                "JOIN favoris fav ON f.id_f = fav.id_f " +
                "JOIN user u ON u.id_user = fav.id_user " +
                "WHERE fav.id_user = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                // Créer un objet Formation
                Formation f = new Formation();
                f.setId_f(rs.getInt("id_f"));
                f.setTitre(rs.getString("titre"));
                f.setDescription(rs.getString("description"));
                f.setDate(rs.getDate("date").toLocalDate());
                f.setHeure_debut(rs.getTime("heure_debut").toLocalTime());
                f.setHeure_fin(rs.getTime("heure_fin").toLocalTime());
                f.setNb_place(rs.getInt("nb_place"));

                // Créer un objet User
                User user = new User(
                        rs.getInt("id_user"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email")
                );

                // Créer un objet Favoris avec la formation et l'utilisateur
                Favoris favoris = new Favoris(
                        rs.getInt("id_favori"),
                        user,
                        userId,
                        f,
                        f.getId_f()  // Corrected: passing the formation's id_f to the constructor
                );

                favorisList.add(favoris);
            }
        }
        return favorisList;
    }

}
