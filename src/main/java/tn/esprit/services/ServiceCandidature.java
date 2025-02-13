package tn.esprit.services;

import tn.esprit.entities.Candidature;
import tn.esprit.entities.OffreEmploi;
import tn.esprit.entities.Role;
import tn.esprit.entities.User;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCandidature implements IService<Candidature>{
    Connection connection;
    public ServiceCandidature() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Candidature candidature) throws SQLException {
        String req = "insert into candidature (id_offre, id_candidat, cv, lettre_motivation) " +
                "values(" + candidature.getIdOffre().getIdOffre() + ", " + candidature.getIdCandidat().getIdU() +
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
        preparedStatement.setInt(2, candidature.getIdCandidat().getIdU());
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
        String req = "select * from candidature c join offre o on c.id_offre = o.id_offre join user u on c.id_candidat = u.id_u where u.role = 'candidat'";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Candidature candidature = new Candidature();
            candidature.setIdCandidature(rs.getInt("id_candidature"));
            candidature.setIdOffre(new OffreEmploi(rs.getInt("id_offre"),rs.getInt("salaire"),rs.getString("titre"),rs.getString("description"),rs.getString("type_contrat"),rs.getString("lieu_travail"),rs.getString("statut_offre"),rs.getString("experience"),rs.getDate("date_publication"),rs.getDate("date_limite")));
            candidature.setIdCandidat(new User(rs.getInt("id_candidat"),rs.getString("nom_u"),rs.getString("prenom_u"),rs.getString("mdp_u"),rs.getString("email_u"), Role.valueOf(rs.getString("role").toUpperCase())));
            candidature.setCv(rs.getString("cv"));
            candidature.setLettreMotivation(rs.getString("lettre_motivation"));

            candidatures.add(candidature);
        }

        return candidatures;
    }
}
