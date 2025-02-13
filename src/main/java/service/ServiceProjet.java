package service;

import entities.*;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProjet implements IServiceProjet<Projet> {
    Connection connection;
    public ServiceProjet() {
        connection = MyDatabase.getInstance().getConnection();

    }



    @Override
    public void ajouterProjet(Projet projet) throws SQLException {
        String req = "insert into projet (nom, description, datecréation, deadline, equipe_id) " +
                "values ('" + projet.getNom() + "', '" + projet.getDescription() + "', '" +
                new java.sql.Date(projet.getDatecréation().getTime()) + "', '" +
                new java.sql.Date(projet.getDeadline().getTime()) + "', " +
                (projet.getEquipe() != null ? projet.getEquipe().getId() : "NULL") + ")";

        Statement statement = connection.createStatement();
        statement.executeUpdate(req);

    }


    @Override
    public void modifierProjet(Projet projet) throws SQLException {
        String req = "update projet set nom=?, description=?, datecréation=?, deadline=?, equipe_id=? where id=?";        PreparedStatement preparedStatement= connection.prepareStatement(req);

        preparedStatement.setString(1,projet.getNom());
        preparedStatement.setString(2,projet.getDescription());
        preparedStatement.setDate(3, new java.sql.Date(projet.getDatecréation().getTime()));
        preparedStatement.setDate(4, new java.sql.Date(projet.getDeadline().getTime()));

        preparedStatement.setInt(5,projet.getEquipe().getId());
        preparedStatement.setInt(6,projet.getId());
        preparedStatement.executeUpdate();

    }


    @Override
    public void supprimerProjet(int id) throws SQLException {
        String req = "DELETE FROM projet WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        int projetsupprimer= preparedStatement.executeUpdate();

    }



    @Override
    public List<Projet> afficherProjet() throws SQLException {
        List<Projet> projets = new ArrayList<>();
        String req = "SELECT p.id, p.nom, p.description, p.datecréation, p.deadline, e.nom_equipe " +
                "FROM projet p " +
                "LEFT JOIN equipe e ON p.equipe_id = e.id";  // Join with equipe table to get team information

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Projet projet = new Projet();
            projet.setId(rs.getInt("id"));
            projet.setNom(rs.getString("nom"));
            projet.setDescription(rs.getString("description"));
            projet.setDatecréation(rs.getDate("datecréation"));
            projet.setDeadline(rs.getDate("deadline"));


            Equipe equipe = new Equipe();
            equipe.setNomEquipe(rs.getString("nom_equipe"));
            projet.setEquipe(equipe);

            projets.add(projet);
        }

        return projets;
    }
}
