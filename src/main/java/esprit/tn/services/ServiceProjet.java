package esprit.tn.services;

import esprit.tn.entities.Equipe;
import esprit.tn.entities.EtatProjet;
import esprit.tn.entities.Projet;
import esprit.tn.utils.MyDatabase;

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
        String req = "INSERT INTO projet (nom, description, datecréation, deadline, etat, equipe_id) " +
                "VALUES ('" + projet.getNom() + "', '" + projet.getDescription() + "', '" +
                new java.sql.Date(projet.getDatecréation().getTime()) + "', '" +
                new java.sql.Date(projet.getDeadline().getTime()) + "', " +
                "'" + projet.getEtat().name() + "', " +
                (projet.getEquipe() != null ? projet.getEquipe().getId() : "NULL") + ")";

        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("projet ajoute");

    }


    @Override
    public void modifierProjet(Projet projet) throws SQLException {
        String req = "update projet set nom=?, description=?, datecréation=?, deadline=?,etat=?, equipe_id=? where id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);

        preparedStatement.setString(1, projet.getNom());
        preparedStatement.setString(2, projet.getDescription());
        preparedStatement.setDate(3, new java.sql.Date(projet.getDatecréation().getTime()));
        preparedStatement.setDate(4, new java.sql.Date(projet.getDeadline().getTime()));
        preparedStatement.setString(5, projet.getEtat().name());
        preparedStatement.setInt(6, projet.getEquipe().getId());
        preparedStatement.setInt(7, projet.getId());

        preparedStatement.executeUpdate();
        System.out.println("Projet mise à jour avec succès.");

    }


    @Override
    public void supprimerProjet(int id) throws SQLException {
        String req = "DELETE FROM projet WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Projet supprimée avec succès.");

    }



    public void supprimerTousProjet() throws SQLException {
        String req = "DELETE FROM projet"; // Requête pour supprimer tous les projets
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.executeUpdate(); // Exécuter la requête
            System.out.println("Tous les projets ont été supprimés avec succès.");
        }
    }

    @Override
    public List<Projet> afficherProjet() throws SQLException {
        List<Projet> projets = new ArrayList<>();
        String req = "SELECT * " +
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
            projet.setEtat(EtatProjet.valueOf(rs.getString("etat")));

            Equipe equipe = new Equipe();
            equipe.setNomEquipe(rs.getString("nom_equipe"));
            projet.setEquipe(equipe);

            projets.add(projet);
        }

        return projets;
    }


    public List<Equipe> getEquipes() throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        String req = "SELECT * FROM equipe";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Equipe equipe = new Equipe();
            equipe.setId(rs.getInt("id"));
            equipe.setNomEquipe(rs.getString("nom_equipe"));
            equipes.add(equipe);
        }

        return equipes;
    }

    public List<Projet> rechercherProjet(String nomProjet, String nomEquipe) throws SQLException {
        List<Projet> projets = new ArrayList<>();
        StringBuilder req = new StringBuilder("SELECT * FROM projet p LEFT JOIN equipe e ON p.equipe_id = e.id WHERE 1=1");

        // Ajouter des conditions en fonction des critères fournis
        if (nomProjet != null && !nomProjet.isEmpty()) {
            req.append(" AND LOWER(p.nom) LIKE LOWER('%").append(nomProjet).append("%')");
        }
        if (nomEquipe != null && !nomEquipe.isEmpty()) {
            req.append(" AND LOWER(e.nom_equipe) LIKE LOWER('%").append(nomEquipe).append("%')");
        }

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req.toString());

        while (rs.next()) {
            Projet projet = new Projet();
            projet.setId(rs.getInt("id"));
            projet.setNom(rs.getString("nom"));
            projet.setDescription(rs.getString("description"));
            projet.setDeadline(rs.getDate("deadline"));
            projet.setEtat(EtatProjet.valueOf(rs.getString("etat")));
            Equipe equipe = new Equipe();
            equipe.setNomEquipe(rs.getString("nom_equipe"));
            projet.setEquipe(equipe);

            projets.add(projet);
        }

        return projets;
    }

    public boolean projetExiste(String nomProjet) throws SQLException {
        String req = "SELECT COUNT(*) FROM projet WHERE nom = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, nomProjet);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retourne true si un projet avec ce nom existe déjà
            }
        }
        return false;
    }
}
