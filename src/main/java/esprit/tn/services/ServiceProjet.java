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
        String req = "INSERT INTO projet (nom, description, datecréation, deadline, etat, equipe_id, imageProjet) " +
                "VALUES ('" + projet.getNom() + "', '" + projet.getDescription() + "', '" +
                new java.sql.Date(projet.getDatecréation().getTime()) + "', '" +
                new java.sql.Date(projet.getDeadline().getTime()) + "', '" +
                projet.getEtat().name() + "', " +
                (projet.getEquipe() != null ? projet.getEquipe().getId() : "NULL") + ", '" +
                projet.getImageProjet() + "')";

        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("Projet ajouté avec succès.");
    }


    @Override
    public void modifierProjet(Projet projet) throws SQLException {
        String req = "UPDATE projet SET nom=?, description=?, datecréation=?, deadline=?, etat=?, equipe_id=?, imageProjet=? WHERE id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);

        preparedStatement.setString(1, projet.getNom());
        preparedStatement.setString(2, projet.getDescription());
        preparedStatement.setDate(3, new java.sql.Date(projet.getDatecréation().getTime()));
        preparedStatement.setDate(4, new java.sql.Date(projet.getDeadline().getTime()));
        preparedStatement.setString(5, projet.getEtat().name());
        preparedStatement.setInt(6, projet.getEquipe().getId());
        preparedStatement.setString(7, projet.getImageProjet());
        preparedStatement.setInt(8, projet.getId());

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
                "LEFT JOIN equipe e ON p.equipe_id = e.id";

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
            projet.setImageProjet(rs.getString("imageProjet"));


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

    public List<Projet> rechercherProjet(String nomProjet) throws SQLException {
        List<Projet> projets = new ArrayList<>();
        StringBuilder req = new StringBuilder("SELECT p.*, e.nom_equipe FROM projet p LEFT JOIN equipe e ON p.equipe_id = e.id WHERE 1=1");

        // Ajouter une condition pour rechercher par nom de projet
        if (nomProjet != null && !nomProjet.isEmpty()) {
            req.append(" AND LOWER(p.nom) LIKE LOWER('%").append(nomProjet).append("%')");
        }

        // Afficher la requête SQL pour le débogage
        System.out.println("Requête SQL : " + req.toString());

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req.toString())) {

            while (rs.next()) {
                Projet projet = new Projet();
                projet.setId(rs.getInt("id"));
                projet.setNom(rs.getString("nom"));
                projet.setDescription(rs.getString("description"));
                projet.setDatecréation(rs.getDate("datecréation"));
                projet.setDeadline(rs.getDate("deadline"));
                projet.setEtat(EtatProjet.valueOf(rs.getString("etat")));
                projet.setImageProjet(rs.getString("imageProjet"));
                Equipe equipe = new Equipe();
                equipe.setNomEquipe(rs.getString("nom_equipe"));
                projet.setEquipe(equipe);

                projets.add(projet);
            }
        }

        return projets;
    }


    public boolean projetExiste(String nomProjet) throws SQLException {
        String req = "SELECT COUNT(*) FROM projet WHERE nom = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, nomProjet);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
