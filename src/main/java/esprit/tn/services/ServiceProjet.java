package esprit.tn.services;

import esprit.tn.entities.*;
import esprit.tn.utils.MyDatabase;
import esprit.tn.utils.SessionManager;

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
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            // Définir les paramètres
            statement.setString(1, projet.getNom());
            statement.setString(2, projet.getDescription());
            statement.setDate(3, new java.sql.Date(projet.getDatecréation().getTime()));
            statement.setDate(4, new java.sql.Date(projet.getDeadline().getTime()));
            statement.setString(5, projet.getEtat().name());
            if (projet.getEquipe() != null) {
                statement.setInt(6, projet.getEquipe().getId());
            } else {
                statement.setNull(6, java.sql.Types.INTEGER); // Gérer le cas où equipe est null
            }
            statement.setString(7, projet.getImageProjet());

            // Exécuter la requête
            statement.executeUpdate();

            // Incrémenter nbrProjet pour l'équipe associée
            if (projet.getEquipe() != null) {
                String updateReq = "UPDATE equipe SET nbrProjet = nbrProjet + 1 WHERE id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateReq)) {
                    updateStatement.setInt(1, projet.getEquipe().getId());
                    updateStatement.executeUpdate();
                }
            }

            System.out.println("Projet ajouté avec succès.");
        }
    }*/


    @Override
    public void ajouterProjet(Projet projet) throws SQLException {
        // Vérifier que l'équipe existe si elle est spécifiée
        if (projet.getEquipe() != null && !equipeExists(projet.getEquipe().getId())) {
            throw new SQLException("L'équipe spécifiée n'existe pas");
        }

        try {
            connection.setAutoCommit(false); // Démarrer une transaction

            // 1. Insérer le projet
            String req = "INSERT INTO projet (nom, description, datecréation, deadline, etat, imageProjet, id_user) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, projet.getNom());
                statement.setString(2, projet.getDescription());
                statement.setDate(3, new java.sql.Date(projet.getDatecréation().getTime()));
                statement.setDate(4, new java.sql.Date(projet.getDeadline().getTime()));
                statement.setString(5, projet.getEtat().name());
                statement.setString(6, projet.getImageProjet());
                statement.setInt(7, SessionManager.extractuserfromsession().getIdUser());

                statement.executeUpdate();

                // Récupérer l'ID généré
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Échec de la création du projet, aucun ID obtenu");
                    }
                    int projetId = rs.getInt(1);

                    // 2. Associer le projet à l'équipe si spécifiée
                    if (projet.getEquipe() != null) {
                        associerProjetEquipe(projetId, projet.getEquipe().getId());

                        // 3. Mettre à jour le compteur de projets de l'équipe
                        incrementerNbrProjets(projet.getEquipe().getId());
                    }
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

    private boolean equipeExists(int equipeId) throws SQLException {
        String query = "SELECT COUNT(*) FROM equipe WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, equipeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void associerProjetEquipe(int projetId, int equipeId) throws SQLException {
        String req = "INSERT INTO equipe_projet (equipe_id, projet_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setInt(1, equipeId);
            stmt.setInt(2, projetId);
            stmt.executeUpdate();
        }
    }

    private void incrementerNbrProjets(int equipeId) throws SQLException {
        String req = "UPDATE equipe SET nbrProjet = nbrProjet + 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setInt(1, equipeId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void modifierProjet(Projet projet) throws SQLException {
        try {
            connection.setAutoCommit(false); // Démarrer une transaction

            // 1. Récupérer l'ancienne équipe associée
            int ancienneEquipeId = getAncienneEquipeId(projet.getId());

            // 2. Mettre à jour les informations de base du projet
            updateProjetInfo(projet);

            // 3. Gérer le changement d'équipe si nécessaire
            if (projet.getEquipe() != null) {
                if (ancienneEquipeId != -1) {
                    // Supprimer l'ancienne association et mettre à jour le compteur
                    supprimerAncienneAssociation(projet.getId(), ancienneEquipeId);
                    decrementerNbrProjets(ancienneEquipeId);
                }

                // Ajouter la nouvelle association et mettre à jour le compteur
                ajouterNouvelleAssociation(projet.getId(), projet.getEquipe().getId());
                incrementerNbrProjets(projet.getEquipe().getId());
            }

            connection.commit();
            System.out.println("Projet mis à jour avec succès.");
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Méthodes utilitaires
    private int getAncienneEquipeId(int projetId) throws SQLException {
        String req = "SELECT equipe_id FROM equipe_projet WHERE projet_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setInt(1, projetId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("equipe_id") : -1;
            }
        }
    }

    private void updateProjetInfo(Projet projet) throws SQLException {
        String req = "UPDATE projet SET nom=?, description=?, datecréation=?, deadline=?, etat=?, imageProjet=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setString(1, projet.getNom());
            stmt.setString(2, projet.getDescription());
            stmt.setDate(3, new java.sql.Date(projet.getDatecréation().getTime()));
            stmt.setDate(4, new java.sql.Date(projet.getDeadline().getTime()));
            stmt.setString(5, projet.getEtat().name());
            stmt.setString(6, projet.getImageProjet());
            stmt.setInt(7, projet.getId());
            stmt.executeUpdate();
        }
    }

    private void supprimerAncienneAssociation(int projetId, int equipeId) throws SQLException {
        String req = "DELETE FROM equipe_projet WHERE projet_id = ? AND equipe_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setInt(1, projetId);
            stmt.setInt(2, equipeId);
            stmt.executeUpdate();
        }
    }

    private void ajouterNouvelleAssociation(int projetId, int equipeId) throws SQLException {
        String req = "INSERT INTO equipe_projet (projet_id, equipe_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setInt(1, projetId);
            stmt.setInt(2, equipeId);
            stmt.executeUpdate();
        }
    }


    private void decrementerNbrProjets(int equipeId) throws SQLException {
        String req = "UPDATE equipe SET nbrProjet = nbrProjet - 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setInt(1, equipeId);
            stmt.executeUpdate();
        }
    }

    /*@Override
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
*/

    @Override
    public void supprimerProjet(int id) throws SQLException {
        try {
            connection.setAutoCommit(false); // Démarrer une transaction

            // 1. Récupérer l'équipe associée au projet depuis la table d'association
            int equipeId = getEquipeAssociee(id);

            // 2. Supprimer les associations du projet dans equipe_projet
            supprimerAssociationsProjet(id);

            // 3. Décrémenter le compteur de projets de l'équipe si nécessaire
            if (equipeId != -1) {
                decrementerCompteurProjets(equipeId);
            }

            // 4. Supprimer le projet lui-même
            supprimerProjetDeLaBase(id);

            connection.commit();
            System.out.println("Projet supprimé avec succès.");
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Méthodes utilitaires
    private int getEquipeAssociee(int projetId) throws SQLException {
        String req = "SELECT equipe_id FROM equipe_projet WHERE projet_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setInt(1, projetId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("equipe_id") : -1;
            }
        }
    }

    private void supprimerAssociationsProjet(int projetId) throws SQLException {
        String req = "DELETE FROM equipe_projet WHERE projet_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setInt(1, projetId);
            stmt.executeUpdate();
        }
    }

    private void decrementerCompteurProjets(int equipeId) throws SQLException {
        String req = "UPDATE equipe SET nbrProjet = GREATEST(nbrProjet - 1, 0) WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setInt(1, equipeId);
            stmt.executeUpdate();
        }
    }

    private void supprimerProjetDeLaBase(int projetId) throws SQLException {
        String req = "DELETE FROM projet WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(req)) {
            stmt.setInt(1, projetId);
            stmt.executeUpdate();
        }
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
        String req = "SELECT p.id, p.nom, p.description, p.datecréation, p.deadline, p.etat, p.imageProjet, e.id AS equipe_id, e.nom_equipe " +
                "FROM projet p " +
                "LEFT JOIN equipe_projet ep ON p.id = ep.projet_id " +
                "LEFT JOIN equipe e ON ep.equipe_id = e.id";

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
            equipe.setId(rs.getInt("equipe_id"));
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

    /*public List<Projet> rechercherProjet(String nomProjet) throws SQLException {
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
*/

    public List<Projet> rechercherProjet(String nomProjet) throws SQLException {
        List<Projet> projets = new ArrayList<>();

        // Requête de base avec jointure correcte
        String baseReq = "SELECT p.id, p.nom, p.description, p.datecréation, p.deadline, " +
                "p.etat, p.imageProjet, e.id AS equipe_id, e.nom_equipe, e.imageEquipe " +
                "FROM projet p " +
                "LEFT JOIN equipe_projet ep ON p.id = ep.projet_id " +
                "LEFT JOIN equipe e ON ep.equipe_id = e.id " +
                "WHERE 1=1";

        // Construction sécurisée de la requête
        StringBuilder reqBuilder = new StringBuilder(baseReq);

        // Paramètres pour PreparedStatement
        List<Object> params = new ArrayList<>();

        if (nomProjet != null && !nomProjet.trim().isEmpty()) {
            reqBuilder.append(" AND LOWER(p.nom) LIKE LOWER(?)");
            params.add("%" + nomProjet.trim() + "%");
        }

        // Tri par défaut
        reqBuilder.append(" ORDER BY p.datecréation DESC");

        String finalReq = reqBuilder.toString();
        System.out.println("Requête SQL : " + finalReq); // Debug

        try (PreparedStatement statement = connection.prepareStatement(finalReq)) {
            // Set des paramètres de manière sécurisée
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Projet projet = new Projet();
                    projet.setId(rs.getInt("id"));
                    projet.setNom(rs.getString("nom"));
                    projet.setDescription(rs.getString("description"));
                    projet.setDatecréation(rs.getDate("datecréation"));
                    projet.setDeadline(rs.getDate("deadline"));
                    projet.setEtat(EtatProjet.valueOf(rs.getString("etat")));
                    projet.setImageProjet(rs.getString("imageProjet"));

                    // Construction de l'équipe si elle existe
                    if (rs.getObject("equipe_id") != null) {
                        Equipe equipe = new Equipe();
                        equipe.setId(rs.getInt("equipe_id"));
                        equipe.setNomEquipe(rs.getString("nom_equipe"));
                        equipe.setImageEquipe(rs.getString("imageEquipe"));
                        projet.setEquipe(equipe);
                    }

                    projets.add(projet);
                }
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
        // Requête pour récupérer l'équipe associée au projet via la table d'association
        String equipeReq = "SELECT e.id, e.nom_equipe, e.imageEquipe, e.nbrProjet, e.id_user " +
                "FROM equipe e " +
                "JOIN equipe_projet ep ON e.id = ep.equipe_id " +
                "WHERE ep.projet_id = ?";

        try (PreparedStatement equipeStmt = connection.prepareStatement(equipeReq)) {
            equipeStmt.setInt(1, projetId);

            try (ResultSet equipeRs = equipeStmt.executeQuery()) {
                if (equipeRs.next()) {
                    // Création de l'objet Equipe
                    Equipe equipe = new Equipe();
                    equipe.setId(equipeRs.getInt("id"));
                    equipe.setNomEquipe(equipeRs.getString("nom_equipe"));
                    equipe.setImageEquipe(equipeRs.getString("imageEquipe"));
                    equipe.setId_user(equipeRs.getInt("id_user"));

                    // Récupération des employés de l'équipe
                    List<User> employes = getEmployesParEquipe(equipe.getId());
                    equipe.setEmployes(employes);

                    return equipe;
                }
            }
        }
        return null;
    }

    private List<User> getEmployesParEquipe(int equipeId) throws SQLException {
        List<User> employes = new ArrayList<>();

        String employesReq = "SELECT u.id_user, u.nom, u.prenom, u.role, u.image_profil, u.email " +
                "FROM user u " +
                "JOIN equipe_employee ee ON u.id_user = ee.id_user " +
                "WHERE ee.equipe_id = ? AND u.role = 'EMPLOYE'"; // Filtre sur le rôle

        try (PreparedStatement employesStmt = connection.prepareStatement(employesReq)) {
            employesStmt.setInt(1, equipeId);

            try (ResultSet employesRs = employesStmt.executeQuery()) {
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
            }
        }
        return employes;
    }

    public List<Projet> rechercherProjetParEtat(String nomProjet, String etat) throws SQLException {
        List<Projet> projets = new ArrayList<>();

        String baseReq = "SELECT p.id, p.nom, p.description, p.datecréation, p.deadline, " +
                "p.etat, p.imageProjet, e.id AS equipe_id, e.nom_equipe, e.imageEquipe " +
                "FROM projet p " +
                "LEFT JOIN equipe_projet ep ON p.id = ep.projet_id " +
                "LEFT JOIN equipe e ON ep.equipe_id = e.id " +
                "WHERE 1=1";

        StringBuilder reqBuilder = new StringBuilder(baseReq);
        List<Object> params = new ArrayList<>();

        // Filtre par nom
        if (nomProjet != null && !nomProjet.trim().isEmpty()) {
            reqBuilder.append(" AND LOWER(p.nom) LIKE ?");
            params.add("%" + nomProjet.toLowerCase() + "%");
        }

        // Filtre par état
        if (etat != null) {
            reqBuilder.append(" AND p.etat = ?");
            params.add(etat);
        }

        reqBuilder.append(" ORDER BY p.datecréation DESC");

        try (PreparedStatement statement = connection.prepareStatement(reqBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Projet projet = mapResultSetToProjet(rs);
                    projets.add(projet);
                }
            }
        }

        return projets;
    }

    private Projet mapResultSetToProjet(ResultSet rs) throws SQLException {
        Projet projet = new Projet();
        projet.setId(rs.getInt("id"));
        projet.setNom(rs.getString("nom"));
        projet.setDescription(rs.getString("description"));
        projet.setDatecréation(rs.getDate("datecréation"));
        projet.setDeadline(rs.getDate("deadline"));
        projet.setEtat(EtatProjet.valueOf(rs.getString("etat")));
        projet.setImageProjet(rs.getString("imageProjet"));

        if (rs.getObject("equipe_id") != null) {
            Equipe equipe = new Equipe();
            equipe.setId(rs.getInt("equipe_id"));
            equipe.setNomEquipe(rs.getString("nom_equipe"));
            equipe.setImageEquipe(rs.getString("imageEquipe"));
            projet.setEquipe(equipe);
        }

        return projet;
    }
//employee

    public List<Projet> getProjetsByEquipeId(int equipeId) throws SQLException {
        List<Projet> projets = new ArrayList<>();

        // Requête corrigée pour utiliser la table d'association
        String req = "SELECT p.id, p.nom, p.description, p.datecréation, p.deadline, " +
                "p.etat, p.imageProjet, p.id_user, u.nom AS manager_nom, u.prenom AS manager_prenom " +
                "FROM projet p " +
                "JOIN equipe_projet ep ON p.id = ep.projet_id " +
                "LEFT JOIN user u ON p.id_user = u.id_user " +  // Information du manager
                "WHERE ep.equipe_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, equipeId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
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
        }

        return projets;
    }
}
