package esprit.tn.services;

import esprit.tn.entities.*;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProjet implements IServiceProjet<Projet> {
    Connection connection;

    public ServiceProjet() {
        connection = MyDatabase.getInstance().getConnection();

    }

    /*@Override
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
    }*/

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

        // Incrémenter nbrProjet pour l'équipe associée
        if (projet.getEquipe() != null) {
            String updateReq = "UPDATE equipe SET nbrProjet = nbrProjet + 1 WHERE id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateReq);
            updateStatement.setInt(1, projet.getEquipe().getId());
            updateStatement.executeUpdate();
        }

        System.out.println("Projet ajouté avec succès.");
    }

    /*@Override
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

    }*/

    @Override
    public void modifierProjet(Projet projet) throws SQLException {
        // Récupérer l'ancienne équipe associée au projet
        String selectReq = "SELECT equipe_id FROM projet WHERE id = ?";
        PreparedStatement selectStatement = connection.prepareStatement(selectReq);
        selectStatement.setInt(1, projet.getId());
        ResultSet rs = selectStatement.executeQuery();

        int ancienneEquipeId = -1;
        if (rs.next()) {
            ancienneEquipeId = rs.getInt("equipe_id");
        }

        // Mettre à jour le projet
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

        // Mettre à jour nbrProjet pour l'ancienne équipe
        if (ancienneEquipeId != -1) {
            String updateAncienneReq = "UPDATE equipe SET nbrProjet = nbrProjet - 1 WHERE id = ?";
            PreparedStatement updateAncienneStatement = connection.prepareStatement(updateAncienneReq);
            updateAncienneStatement.setInt(1, ancienneEquipeId);
            updateAncienneStatement.executeUpdate();
        }

        // Mettre à jour nbrProjet pour la nouvelle équipe
        String updateNouvelleReq = "UPDATE equipe SET nbrProjet = nbrProjet + 1 WHERE id = ?";
        PreparedStatement updateNouvelleStatement = connection.prepareStatement(updateNouvelleReq);
        updateNouvelleStatement.setInt(1, projet.getEquipe().getId());
        updateNouvelleStatement.executeUpdate();

        System.out.println("Projet mis à jour avec succès.");
    }


    /*@Override
    public void supprimerProjet(int id) throws SQLException {
        String req = "DELETE FROM projet WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Projet supprimée avec succès.");

    }*/

    @Override
    public void supprimerProjet(int id) throws SQLException {
        // Récupérer l'équipe associée au projet
        String selectReq = "SELECT equipe_id FROM projet WHERE id = ?";
        PreparedStatement selectStatement = connection.prepareStatement(selectReq);
        selectStatement.setInt(1, id);
        ResultSet rs = selectStatement.executeQuery();

        if (rs.next()) {
            int equipeId = rs.getInt("equipe_id");

            // Décrémenter nbrProjet uniquement s'il est supérieur à 0
            String updateReq = "UPDATE equipe SET nbrProjet = nbrProjet - 1 WHERE id = ? AND nbrProjet > 0";
            PreparedStatement updateStatement = connection.prepareStatement(updateReq);
            updateStatement.setInt(1, equipeId);
            updateStatement.executeUpdate();
        }

        // Supprimer le projet
        String req = "DELETE FROM projet WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Projet supprimé avec succès.");
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


    public Equipe getEquipeAvecEmployesParProjet(int projetId) throws SQLException {
        Equipe equipe = null;

        // Requête pour récupérer l'équipe associée au projet
        String equipeReq = "SELECT e.id, e.nom_equipe, e.imageEquipe FROM equipe e " +
                "JOIN projet p ON e.id = p.equipe_id WHERE p.id = ?";
        PreparedStatement equipeStmt = connection.prepareStatement(equipeReq);
        equipeStmt.setInt(1, projetId);
        ResultSet equipeRs = equipeStmt.executeQuery();

        if (equipeRs.next()) {
            int equipeId = equipeRs.getInt("id");
            String nomEquipe = equipeRs.getString("nom_equipe");
            String imageEquipe = equipeRs.getString("imageEquipe");

            // Requête pour récupérer les employés de l'équipe
            String employesReq = "SELECT u.id_user, u.nom, u.prenom, u.role, u.image_profil, u.email " +
                    "FROM user u " +
                    "JOIN equipe_employee ee ON u.id_user = ee.id_user " +
                    "WHERE ee.equipe_id = ?";
            PreparedStatement employesStmt = connection.prepareStatement(employesReq);
            employesStmt.setInt(1, equipeId);
            ResultSet employesRs = employesStmt.executeQuery();

            List<User> employes = new ArrayList<>();
            while (employesRs.next()) {
                User user = new User(
                        employesRs.getInt("id_user"),
                        employesRs.getString("nom"),
                        employesRs.getString("prenom"),
                        Role.valueOf(employesRs.getString("role").toUpperCase()),
                        employesRs.getString("image_profil"),
                        employesRs.getString("email")
                );
                employes.add(user);
            }

            equipe = new Equipe(equipeId, nomEquipe, employes, imageEquipe);
        }

        return equipe;
    }

    public List<Projet> rechercherProjetParEtat(String nomProjet, String etat) throws SQLException {
        List<Projet> projets = new ArrayList<>();
        StringBuilder req = new StringBuilder("SELECT p.*, e.nom_equipe FROM projet p LEFT JOIN equipe e ON p.equipe_id = e.id WHERE 1=1");

        // Ajouter une condition pour rechercher par nom de projet
        if (nomProjet != null && !nomProjet.isEmpty()) {
            req.append(" AND LOWER(p.nom) LIKE LOWER('%").append(nomProjet).append("%')");
        }

        // Ajouter une condition pour filtrer par état
        if (etat != null && !etat.equals("TOUS")) {
            req.append(" AND p.etat = '").append(etat).append("'");
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
}
