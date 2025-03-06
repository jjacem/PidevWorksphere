package esprit.tn.services;

import esprit.tn.entities.Candidature;
import esprit.tn.entities.OffreEmploi;

import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCandidature implements IService<Candidature> {
    private final Connection connection;

    public ServiceCandidature() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Candidature candidature) throws SQLException {
        String req = "INSERT INTO candidature (id_offre, id_candidat, cv, lettre_motivation) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, candidature.getIdOffre().getIdOffre());
            ps.setInt(2, candidature.getIdCandidat());
            ps.setString(3, candidature.getCv());
            ps.setString(4, candidature.getLettreMotivation());
            ps.executeUpdate();
            System.out.println("Candidature ajoutée avec succès !");
        }
    }

    @Override
    public void modifier(Candidature candidature) throws SQLException {
        String req = "UPDATE candidature SET id_offre=?, id_candidat=?, cv=?, lettre_motivation=? WHERE id_candidature=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, candidature.getIdOffre().getIdOffre());
            ps.setInt(2, candidature.getIdCandidat());
            ps.setString(3, candidature.getCv());
            ps.setString(4, candidature.getLettreMotivation());
            ps.setInt(5, candidature.getIdCandidature());
            ps.executeUpdate();
            System.out.println("Candidature modifiée avec succès !");
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM candidature WHERE id_candidature=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Candidature supprimée avec succès !");
        }
    }

    @Override
    public List<Candidature> afficher() throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
        String req = "SELECT c.*, o.*, u.id_user, u.nom, u.prenom, u.email " +
                "FROM candidature c " +
                "JOIN offre o ON c.id_offre = o.id_offre " +
                "JOIN user u ON c.id_candidat = u.id_user " +
                "WHERE u.role = 'candidat'";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                OffreEmploi offre = new OffreEmploi(
                        rs.getInt("id_offre"),
                        rs.getInt("salaire"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("type_contrat"),
                        rs.getString("lieu_travail"),
                        rs.getString("statut_offre"),
                        rs.getString("experience"),
                        rs.getDate("date_publication"),
                        rs.getDate("date_limite")
                );

                Candidature candidature = new Candidature(
                        rs.getInt("id_candidature"),
                        offre,
                        rs.getInt("id_candidat"),
                        rs.getString("cv"),
                        rs.getString("lettre_motivation")
                );

                candidatures.add(candidature);
            }
        }

        return candidatures;
    }

    public boolean hasUserAppliedForOffer(int userId, int offerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM candidature WHERE id_candidat = ? AND id_offre = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, offerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public List<Integer> getAppliedOfferIds(int userId) throws SQLException {
        List<Integer> appliedOfferIds = new ArrayList<>();
        String query = "SELECT id_offre FROM candidature WHERE id_candidat = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    appliedOfferIds.add(rs.getInt("id_offre"));
                }
            }
        }

        return appliedOfferIds;
    }

    public List<Candidature> getCandidaturesByUser(int userId) throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
        String query = "SELECT c.*, o.id_offre, o.titre, o.description " +
                "FROM candidature c " +
                "JOIN offre o ON o.id_offre = c.id_offre " +
                "WHERE c.id_candidat = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OffreEmploi offre = new OffreEmploi(
                            rs.getInt("id_offre"),
                            0, "", "", "", "", "", "",
                            null, null
                    );
                    offre.setTitre(rs.getString("titre"));
                    offre.setDescription(rs.getString("description"));

                    Candidature candidature = new Candidature(
                            rs.getInt("id_candidature"),
                            offre,
                            userId,
                            rs.getString("cv"),
                            rs.getString("lettre_motivation")
                    );

                    candidatures.add(candidature);
                }
            }
        }

        return candidatures;
    }

    public List<Candidature> getCandidaturesByOffre(int offreId) throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
        String sql = "SELECT * FROM candidature WHERE id_offre = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, offreId);
            
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Candidature candidature = mapResultSetToCandidature(rs);
                    candidatures.add(candidature);
                }
            }
        }
        
        return candidatures;
    }

    private Candidature mapResultSetToCandidature(ResultSet rs) throws SQLException {
        Candidature candidature = new Candidature();
        candidature.setIdCandidature(rs.getInt("id_candidature"));
        candidature.setIdCandidat(rs.getInt("id_candidat"));
        candidature.setCv(rs.getString("cv"));
        candidature.setLettreMotivation(rs.getString("lettre_motivation"));
        
        int offreId = rs.getInt("id_offre");
        ServiceOffre serviceOffre = new ServiceOffre();
        candidature.setIdOffre(serviceOffre.getOffreById(offreId));
        
        return candidature;
    }
}