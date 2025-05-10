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
        // 1. Vérifier si le nom existe déjà
        if (nomEquipeExiste(equipe.getNomEquipe())) {
            throw new SQLException("Une équipe avec ce nom existe déjà.");
        }

        // 2. Vérifier que l'utilisateur existe
        if (!userExists(equipe.getId_user())) {
            throw new SQLException("L'utilisateur manager spécifié n'existe pas.");
        }

        // 3. Utiliser une transaction pour plus de sécurité
        try {
            connection.setAutoCommit(false);

            // Insertion de l'équipe
            String req = "INSERT INTO equipe (nom_equipe, imageEquipe, nbrProjet, id_user) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, equipe.getNomEquipe());
                preparedStatement.setString(2, equipe.getImageEquipe());
                preparedStatement.setInt(3, 0); // Initialiser nbrProjet à 0

                // Gestion du cas où id_user serait null
                if (equipe.getId_user() > 0) {
                    preparedStatement.setInt(4, equipe.getId_user());
                } else {
                    preparedStatement.setNull(4, Types.INTEGER);
                }

                preparedStatement.executeUpdate();

                // Récupérer l'ID généré
                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Échec de la création de l'équipe, aucun ID obtenu.");
                    }
                    int equipeId = rs.getInt(1);

                    // Associer les employés
                    associerEmployes(equipeId, equipe.getEmployes());
                }
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Méthode pour vérifier l'existence d'un utilisateur
    private boolean userExists(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE id_user = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Méthode pour associer les employés (optimisée en batch)
    private void associerEmployes(int equipeId, List<User> employes) throws SQLException {
        String sql = "INSERT INTO equipe_employee (equipe_id, id_user) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (User user : employes) {
                if (user.getRole() != Role.EMPLOYE) {
                    System.out.println("L'utilisateur " + user.getNom() + " n'a pas le rôle EMPLOYE. Ajout annulé.");
                    continue;
                }
                stmt.setInt(1, equipeId);
                stmt.setInt(2, user.getIdUser());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    @Override
    public void modifierEquipe(Equipe equipe) throws SQLException {
        String req = "UPDATE equipe SET nom_equipe = ?, imageEquipe = ? , id_user = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, equipe.getNomEquipe());
        preparedStatement.setString(2, equipe.getImageEquipe());
        preparedStatement.setInt(3, equipe.getId_user());
        preparedStatement.setInt(4, equipe.getId());
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
        String req = "SELECT e.* FROM equipe e";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Equipe equipe = new Equipe();
                equipe.setId(rs.getInt("id"));
                equipe.setNomEquipe(rs.getString("nom_equipe"));
                equipe.setImageEquipe(rs.getString("imageEquipe"));
                equipe.setId_user(rs.getInt("id_user"));

                // Charger les employés et projets...
                equipe.setEmployes(getEmployesByEquipeId(equipe.getId()));
                equipe.setProjets(getProjetsByEquipeId(equipe.getId()));

                equipes.add(equipe);
            }
        }
        return equipes;
    }
    private List<Projet> getProjetsByEquipeId(int equipeId) throws SQLException {
        List<Projet> projets = new ArrayList<>();
        String req = "SELECT p.* FROM projet p " +
                "JOIN equipe_projet ep ON p.id = ep.projet_id " +
                "WHERE ep.equipe_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setInt(1, equipeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Projet projet = new Projet();
                projet.setId(rs.getInt("id"));
                projet.setNom(rs.getString("nom"));
                projet.setDescription(rs.getString("description"));
                projet.setDatecréation(rs.getDate("datecréation"));
                projet.setDeadline(rs.getDate("deadline"));
                projet.setEtat(EtatProjet.valueOf(rs.getString("etat")));
                projet.setImageProjet(rs.getString("imageProjet"));
                projets.add(projet);
            }
        }
        return projets;
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

    /////employee
    public List<Equipe> getEquipesByUserId(int userId) throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        String req = "SELECT e.id, e.nom_equipe, e.imageEquipe, e.nbrProjet " +
                "FROM equipe e " +
                "JOIN equipe_employee ee ON e.id = ee.equipe_id " +
                "WHERE ee.id_user = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, userId);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String nomEquipe = rs.getString("nom_equipe");
            String imageEquipe = rs.getString("imageEquipe");
            int nbrProjet = rs.getInt("nbrProjet");

            // Récupérer les employés de l'équipe
            List<User> employes = getEmployesByEquipeId(id);

            Equipe equipe = new Equipe(id, nomEquipe, employes, imageEquipe, nbrProjet);
            equipes.add(equipe);
        }

        return equipes;
    }

    private List<User> getEmployesByEquipeId(int equipeId) throws SQLException {
        List<User> employes = new ArrayList<>();
        String req = "SELECT u.id_user, u.nom, u.prenom, u.role, u.image_profil, u.email " +
                "FROM user u " +
                "JOIN equipe_employee ee ON u.id_user = ee.id_user " +
                "WHERE ee.equipe_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, equipeId);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            User user = new User(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    Role.valueOf(rs.getString("role").toUpperCase()),
                    rs.getString("image_profil"),
                    rs.getString("email")
            );
            employes.add(user);
        }

        return employes;
    }
}