package esprit.tn.services;

import com.google.gson.Gson;
import esprit.tn.entities.Equipe;
import esprit.tn.entities.Role;
import esprit.tn.entities.*;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEquipe implements IServiceEquipe<Equipe> {
    Connection connection;

    public ServiceEquipe() {
        connection = MyDatabase.getInstance().getConnection();
    }


    @Override
    public void ajouterEquipe(Equipe equipe) throws SQLException {
        if (nomEquipeExiste(equipe.getNomEquipe())) {
            throw new SQLException("Une équipe avec ce nom existe déjà.");
        }

        String req = "INSERT INTO equipe (nom_equipe, imageEquipe, nbrProjet) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, equipe.getNomEquipe());
        preparedStatement.setString(2, equipe.getImageEquipe());
        preparedStatement.setInt(3, 0); // Initialiser nbrProjet à 0
        preparedStatement.executeUpdate();

        // Récupérer l'ID de l'équipe insérée
        String selectReq = "SELECT id FROM equipe WHERE nom_equipe = ? ORDER BY id DESC LIMIT 1";
        PreparedStatement selectStatement = connection.prepareStatement(selectReq);
        selectStatement.setString(1, equipe.getNomEquipe());
        ResultSet rs = selectStatement.executeQuery();

        if (!rs.next()) throw new SQLException("Erreur lors de la récupération de l'ID de l'équipe.");
        int equipeId = rs.getInt("id");

        // Associer les employés à l'équipe
        for (User user : equipe.getEmployes()) {
            if (user.getRole() != Role.EMPLOYE) {
                System.out.println("L'utilisateur " + user.getNom() + " n'a pas le rôle EMPLOYE. Ajout annulé.");
                continue;
            }

            PreparedStatement assocStatement = connection.prepareStatement("INSERT INTO equipe_employee (equipe_id, id_user) VALUES (?, ?)");
            assocStatement.setInt(1, equipeId);
            assocStatement.setInt(2, user.getIdUser());
            assocStatement.executeUpdate();
        }
    }

    @Override
    public void modifierEquipe(Equipe equipe) throws SQLException {
        String req = "UPDATE equipe SET nom_equipe = ?, imageEquipe = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, equipe.getNomEquipe());
        preparedStatement.setString(2, equipe.getImageEquipe());
        preparedStatement.setInt(3, equipe.getId());
        preparedStatement.executeUpdate();

        // hne fas5na association le9dima
        String deleteAndInsert = "DELETE FROM equipe_employee WHERE equipe_id = ?";
        PreparedStatement statement = connection.prepareStatement(deleteAndInsert);
        statement.setInt(1, equipe.getId());
        statement.executeUpdate();

        // hne 3malna association bin equipe w user el jdida
        String assocReq = "INSERT INTO equipe_employee (equipe_id, id_user) VALUES (?, ?)";
        PreparedStatement assocStatement = connection.prepareStatement(assocReq);
        for (User user : equipe.getEmployes()) {
            assocStatement.setInt(1, equipe.getId());
            assocStatement.setInt(2, user.getIdUser());
            assocStatement.executeUpdate();
        }
    }

    @Override
    public void supprimerEquipe(int id) throws SQLException {

        // nfas5ou association
        PreparedStatement deleteAssoc = connection.prepareStatement("DELETE FROM equipe_employee WHERE equipe_id = ?");
        deleteAssoc.setInt(1, id);
        deleteAssoc.executeUpdate();

        PreparedStatement deleteEquipe = connection.prepareStatement("DELETE FROM equipe WHERE id = ?");
        deleteEquipe.setInt(1, id);
        deleteEquipe.executeUpdate();
    }



    @Override
    public List<Equipe> afficherEquipe() throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        String req = "SELECT * FROM equipe";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("id");
            String nomEquipe = rs.getString("nom_equipe");
            String imageEquipe = rs.getString("imageEquipe");
            int nbrProjet = rs.getInt("nbrProjet");

            // Récupérer les employés de l'équipe
            String employesReq = "SELECT e.id_user, e.nom, e.prenom, e.role, e.image_profil, e.email FROM user e " +
                    "JOIN equipe_employee ee ON e.id_user = ee.id_user WHERE ee.equipe_id = ?";
            PreparedStatement employesStmt = connection.prepareStatement(employesReq);
            employesStmt.setInt(1, id);
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

            // Récupérer les projets de l'équipe
            String projetsReq = "SELECT p.id, p.nom, p.description, p.datecréation, p.deadline, p.etat, p.imageProjet FROM projet p " +
                    "WHERE p.equipe_id = ?";
            PreparedStatement projetsStmt = connection.prepareStatement(projetsReq);
            projetsStmt.setInt(1, id);
            ResultSet projetsRs = projetsStmt.executeQuery();

            List<Projet> projets = new ArrayList<>();
            while (projetsRs.next()) {
                Projet projet = new Projet(
                        projetsRs.getInt("id"),
                        projetsRs.getString("nom"),
                        projetsRs.getString("description"),
                        projetsRs.getDate("datecréation"),
                        projetsRs.getDate("deadline"),
                        EtatProjet.valueOf(projetsRs.getString("etat")),
                        projetsRs.getString("imageProjet")
                );
                projets.add(projet);
            }

            Equipe equipe = new Equipe(id, nomEquipe, employes, imageEquipe, nbrProjet);
            equipe.setProjets(projets);
            equipes.add(equipe);
        }

        return equipes;
    }

    // Méthode pour récupérer les données des équipes au format JSON
    public String getEquipeStatsJson() throws SQLException {
        List<Equipe> equipes = afficherEquipe();
        Gson gson = new Gson();
        return gson.toJson(equipes);
    }

    public List<User> getEmployesDisponibles() throws SQLException {
        List<User> employesDisponibles = new ArrayList<>();
        String req = "SELECT id_user, nom, prenom, role, image_profil  FROM user WHERE role = 'Employe'";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            User user = new User(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    Role.valueOf(rs.getString("role").toUpperCase()),
                    rs.getString("image_profil")
            );
            employesDisponibles.add(user);
        }
        return employesDisponibles;
    }


    @Override
    public List<Equipe> rechercherEquipe(String nomEquipe) throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        String req = "SELECT * FROM equipe WHERE nom_equipe LIKE ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, "%" + nomEquipe + "%");
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String nom = rs.getString("nom_equipe");
            String imageEquipe = rs.getString("imageEquipe");
            Equipe equipe = new Equipe(id, nom, new ArrayList<>(), imageEquipe);
            equipes.add(equipe);
        }

        return equipes;
    }



    public List<User> rechercherEmployee(int equipeId, String searchText) throws SQLException {
        List<User> employesTrouves = new ArrayList<>();

        String req = "SELECT u.id_user, u.nom, u.prenom, u.image_profil, u.email " +
                "FROM user u " +
                "JOIN equipe_employee ee ON u.id_user = ee.id_user " +
                "WHERE ee.equipe_id = ? AND (LOWER(u.nom) LIKE ? OR LOWER(u.prenom) LIKE ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, equipeId);
            preparedStatement.setString(2, "%" + searchText.toLowerCase() + "%");
            preparedStatement.setString(3, "%" + searchText.toLowerCase() + "%");

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id_user"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("image_profil"),
                        rs.getString("email")
                );
                employesTrouves.add(user);
            }
        }
        return employesTrouves;
    }

    public void supprimerToutesEquipes() throws SQLException {
        String query = "DELETE FROM equipe";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.executeUpdate();
        }
    }

    public boolean nomEquipeExiste(String nomEquipe) throws SQLException {
        String req = "SELECT COUNT(*) FROM equipe WHERE nom_equipe = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, nomEquipe);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    //hedi cntrl te3 modif si mem nom existe ou non
    public boolean cntrlModifEquipe(String nomEquipe, int idEquipeActuelle) throws SQLException {
        String req = "SELECT COUNT(*) FROM equipe WHERE nom_equipe = ? AND id != ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, nomEquipe);
            preparedStatement.setInt(2, idEquipeActuelle);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}