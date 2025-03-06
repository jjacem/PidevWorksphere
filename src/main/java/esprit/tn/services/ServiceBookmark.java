package esprit.tn.services;

import esprit.tn.entities.Bookmark;
import esprit.tn.entities.OffreEmploi;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceBookmark implements IService<Bookmark> {
    private Connection conn;

    public ServiceBookmark() {
        conn = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Bookmark bookmark) throws SQLException {
        String sql = "INSERT INTO bookmarks (id_candidat, id_offre, date_created) VALUES (?, ?, NOW())";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, bookmark.getUserId());
            preparedStatement.setInt(2, bookmark.getOffreId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void modifier(Bookmark bookmark) throws SQLException {
        // Not needed for bookmarks as we only need to add or delete
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM bookmarks WHERE id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Bookmark> afficher() throws SQLException {
        return null;
    }

    public void supprimerParUserEtOffre(int userId, int offreId) throws SQLException {
        String sql = "DELETE FROM bookmarks WHERE id_candidat = ? AND id_offre = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, offreId);
            preparedStatement.executeUpdate();
        }
    }

    public List<Bookmark> recuperer() throws SQLException {
        List<Bookmark> bookmarks = new ArrayList<>();
        String sql = "SELECT * FROM bookmarks";
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Bookmark bookmark = new Bookmark(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("offre_id"),
                        resultSet.getTimestamp("date_created")
                );
                bookmarks.add(bookmark);
            }
        }
        return bookmarks;
    }

    public List<OffreEmploi> recupererOffresBookmarkees(int userId) throws SQLException {
        List<OffreEmploi> offres = new ArrayList<>();
        String sql = "SELECT o.* FROM offre o JOIN bookmarks b ON o.id_offre = b.id_offre WHERE b.id_candidat = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    OffreEmploi offre = new OffreEmploi();
                    offre.setIdOffre(resultSet.getInt("id_offre"));
                    offre.setTitre(resultSet.getString("titre"));
                    offre.setDescription(resultSet.getString("description"));
                    offre.setTypeContrat(resultSet.getString("type_contrat"));
                    offre.setLieuTravail(resultSet.getString("lieu_travail"));
                    offre.setSalaire(resultSet.getInt("salaire"));
                    offre.setStatutOffre(resultSet.getString("statut_offre"));
                    offre.setExperience(resultSet.getString("experience"));
                    offre.setDatePublication(resultSet.getDate("date_publication"));
                    offre.setDateLimite(resultSet.getDate("date_limite"));
                    offres.add(offre);
                }
            }
        }
        return offres;
    }

    public List<Integer> getBookmarkedOfferIds(int userId) throws SQLException {
        List<Integer> bookmarkedIds = new ArrayList<>();
        String sql = "SELECT id_offre FROM bookmarks WHERE id_candidat = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    bookmarkedIds.add(resultSet.getInt("id_offre"));
                }
            }
        }
        return bookmarkedIds;
    }

    public boolean isOfferBookmarked(int userId, int offreId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM bookmarks WHERE id_candidat = ? AND id_offre = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, offreId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count") > 0;
                }
            }
        }
        return false;
    }
}
