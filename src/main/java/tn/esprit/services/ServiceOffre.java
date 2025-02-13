package tn.esprit.services;

import tn.esprit.entities.OffreEmploi;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceOffre implements IService<OffreEmploi> {
    Connection connection;

    public ServiceOffre() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(OffreEmploi offre) throws SQLException {
        String req = "insert into offre (titre, description, type_contrat, salaire, lieu_travail, date_publication, date_limite, statut_offre, experience) " +
                "values('" + offre.getTitre() + "', '" + offre.getDescription() + "', '" + offre.getTypeContrat() + "', " +
                offre.getSalaire() + ", '" + offre.getLieuTravail() + "', '" + new java.sql.Date(offre.getDatePublication().getTime()) + "', '" +
                new java.sql.Date(offre.getDateLimite().getTime()) + "', '" + offre.getStatutOffre() + "', '" + offre.getExperience() + "')";

        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("Offre ajoutée avec succès !");
    }


    @Override
    public void modifier(OffreEmploi offre) throws SQLException {
        String req = "update offre set titre=?, description=?, type_contrat=?, salaire=?, lieu_travail=?, date_publication=?, date_limite=?, statut_offre=?, experience=? where id_offre=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);

        preparedStatement.setString(1, offre.getTitre());
        preparedStatement.setString(2, offre.getDescription());
        preparedStatement.setString(3, offre.getTypeContrat());
        preparedStatement.setInt(4, offre.getSalaire());
        preparedStatement.setString(5, offre.getLieuTravail());
        preparedStatement.setDate(6, new java.sql.Date(offre.getDatePublication().getTime())); // Conversion LocalDate to SQL Date
        preparedStatement.setDate(7, new java.sql.Date(offre.getDateLimite().getTime())); // Conversion LocalDate to SQL Date
        preparedStatement.setString(8, offre.getStatutOffre());
        preparedStatement.setString(9, offre.getExperience());
        preparedStatement.setInt(10, offre.getIdOffre());

        preparedStatement.executeUpdate();
        System.out.println("Offre modifiée avec succès !");
    }


    @Override
    public void supprimer(int id) throws SQLException {
        String req = "delete from offre where id_offre=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Offre supprimée avec succès !");
    }

    @Override
    public List<OffreEmploi> afficher() throws SQLException {
        List<OffreEmploi> offres = new ArrayList<>();
        String req = "select * from offre";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            OffreEmploi offre = new OffreEmploi();
            offre.setIdOffre(rs.getInt("id_offre"));
            offre.setTitre(rs.getString("titre"));
            offre.setDescription(rs.getString("description"));
            offre.setTypeContrat(rs.getString("type_contrat"));
            offre.setSalaire(rs.getInt("salaire"));
            offre.setLieuTravail(rs.getString("lieu_travail"));
            offre.setDatePublication(rs.getDate("date_publication"));
            offre.setDateLimite(rs.getDate("date_limite"));
            offre.setStatutOffre(rs.getString("statut_offre"));
            offre.setExperience(rs.getString("experience"));

            offres.add(offre);
        }

        return offres;
    }
}

