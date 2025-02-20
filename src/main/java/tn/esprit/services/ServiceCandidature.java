package tn.esprit.services;

import tn.esprit.entities.Candidature;
import tn.esprit.entities.OffreEmploi;
import tn.esprit.entities.Role;
import tn.esprit.entities.User;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCandidature implements IService<Candidature> {
    Connection connection;

    public ServiceCandidature() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Candidature candidature) throws SQLException {
        String req = "insert into candidature (id_offre, id_candidat, cv, lettre_motivation) " +
                "values(" + candidature.getIdOffre().getIdOffre() + ", " + candidature.getIdCandidat().getIdUser() +
                ", '" + candidature.getCv() + "', '" + candidature.getLettreMotivation() + "')";

        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("Candidature ajoutée avec succès !");
    }

    @Override
    public void modifier(Candidature candidature) throws SQLException {
        String req = "update candidature set id_offre=?, id_candidat=?, cv=?, lettre_motivation=? where id_candidature=?";

        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, candidature.getIdOffre().getIdOffre());
        preparedStatement.setInt(2, candidature.getIdCandidat().getIdUser());
        preparedStatement.setString(3, candidature.getCv());
        preparedStatement.setString(4, candidature.getLettreMotivation());
        preparedStatement.setInt(5, candidature.getIdCandidature());

        preparedStatement.executeUpdate();
        System.out.println("Candidature modifiée avec succès !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "delete from candidature where id_candidature=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Candidature supprimée avec succès !");
    }

    @Override
    public List<Candidature> afficher() throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
        String req = "select * from candidature c join offre o on c.id_offre = o.id_offre join user u on c.id_candidat = u.id_user where u.role = 'candidat'";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Candidature candidature = new Candidature();
            candidature.setIdCandidature(rs.getInt("id_candidature"));
            candidature.setIdOffre(new OffreEmploi(rs.getInt("id_offre"), rs.getInt("salaire"), rs.getString("titre"), rs.getString("description"), rs.getString("type_contrat"), rs.getString("lieu_travail"), rs.getString("statut_offre"), rs.getString("experience"), rs.getDate("date_publication"), rs.getDate("date_limite")));
            User candidat = new User();
            candidat.setIdUser(rs.getInt("id_user"));
            candidature.setIdCandidat(candidat);
            candidature.setCv(rs.getString("cv"));
            candidature.setLettreMotivation(rs.getString("lettre_motivation"));

            candidatures.add(candidature);
        }

        return candidatures;
    }

    public boolean hasUserAppliedForOffer(int userId, int offerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM candidature WHERE id_candidat = ? AND id_offre = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, offerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<Integer> getAppliedOfferIds(int userId) throws SQLException {
        List<Integer> appliedOfferIds = new ArrayList<>();
        String query = "SELECT id_offre FROM candidature WHERE id_candidat = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                appliedOfferIds.add(rs.getInt("id_offre"));
            }
        }
        return appliedOfferIds;
    }

    public List<Candidature> getCandidaturesByUser(int userId) throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
        String query = "SELECT c.*, o.titre, o.id_offre, o.description " +
                      "FROM candidature c " +
                      "JOIN offre o ON o.id_offre = c.id_offre " +
                      "WHERE c.id_candidat = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                OffreEmploi offre = new OffreEmploi();
                offre.setIdOffre(rs.getInt("id_offre"));
                offre.setTitre(rs.getString("titre"));
                offre.setDescription(rs.getString("description"));

                Candidature candidature = new Candidature();
                candidature.setIdCandidature(rs.getInt("id_candidature"));
                candidature.setIdOffre(offre);
                candidature.setCv(rs.getString("cv"));
                candidature.setLettreMotivation(rs.getString("lettre_motivation"));
                
                candidatures.add(candidature);
            }
        }
        System.out.println("Found " + candidatures.size() + " candidatures");
        return candidatures;
    }
}
