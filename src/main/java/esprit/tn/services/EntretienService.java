package esprit.tn.services;

import esprit.tn.entities.Candidature;
import esprit.tn.entities.Entretien;
import esprit.tn.entities.TypeEntretien;
import esprit.tn.entities.User;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntretienService implements IService<Entretien> {


    private Connection conn;



    public EntretienService()  {
        conn = MyDatabase.getInstance().getConnection();
    }


    @Override
    public void ajouter(Entretien entretien) throws SQLException {

        String sql = "INSERT INTO entretiens " +
            "(titre , description , date_entretien, heure_entretien, type_entretien, status) VALUES (?,?,?,?,?,?)";
        PreparedStatement pstmt  = conn.prepareStatement(sql);
        pstmt.setString(1, entretien.getTitre());
        pstmt.setString(2, entretien.getDescription());
        pstmt.setDate(3, new java.sql.Date(entretien.getDate_entretien().getTime()));
        pstmt.setTime(4, entretien.getHeure_entretien());
        pstmt.setString(5, entretien.getType_entretien().name());
        pstmt.setBoolean(6, entretien.isStatus());
        pstmt.executeUpdate();
        System.out.println("entretien ajouté avec sucées");
    }



    public void ajouterwithId_offre(Entretien entretien, int idOffre) throws SQLException {
        String getCandidatQuery = "SELECT id_candidat, id_candidature FROM candidature WHERE id_Offre = ?";
        int idCandidat = -1;
        int idCandidature = -1;

        try (PreparedStatement stmt = conn.prepareStatement(getCandidatQuery)) {
            stmt.setInt(1, idOffre);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                idCandidat = resultSet.getInt("id_candidat");
                idCandidature = resultSet.getInt("id_candidature");
            }
        }

        if (idCandidat == -1 || idCandidature == -1) {
            System.out.println("Aucun candidat ou candidature trouvée pour cette offre !");
            return;
        }

        String sql = "INSERT INTO entretiens (titre, description, date_entretien, heure_entretien, type_entretien, status, employe_id, idOffre, candidatId, idCandidature) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entretien.getTitre());
            pstmt.setString(2, entretien.getDescription());
            pstmt.setDate(3, new java.sql.Date(entretien.getDate_entretien().getTime()));
            pstmt.setTime(4, entretien.getHeure_entretien());
            pstmt.setString(5, entretien.getType_entretien().name());
            pstmt.setBoolean(6, entretien.isStatus());
            pstmt.setInt(7, entretien.getEmployeId());
            pstmt.setInt(8, idOffre);
            pstmt.setInt(9, idCandidat);
            pstmt.setInt(10, idCandidature);
            pstmt.executeUpdate();
            System.out.println("Entretien ajouté avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout de l'entretien !");
        }
    }




    public void ajouterEntretienAvecCandidature(Entretien entretien, int idOffre) throws SQLException {
        String insertEntretienSQL = "INSERT INTO entretiens (titre, description, date_entretien, heure_entretien, type_entretien, status, employe_id, idOffre) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        int entretienId = -1;

        try (PreparedStatement pstmt = conn.prepareStatement(insertEntretienSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, entretien.getTitre());
            pstmt.setString(2, entretien.getDescription());
            pstmt.setDate(3, new java.sql.Date(entretien.getDate_entretien().getTime()));
            pstmt.setTime(4, entretien.getHeure_entretien());
            pstmt.setString(5, entretien.getType_entretien().name());
            pstmt.setBoolean(6, entretien.isStatus());
            pstmt.setInt(7, entretien.getEmployeId());
            pstmt.setInt(8, idOffre);

            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                entretienId = generatedKeys.getInt(1);
            }
        }

        if (entretienId == -1) {
            System.out.println("Erreur lors de l'insertion de l'entretien !");
            return;
        }

        int idCandidat = entretien.getCandidatId();

        String selectCandidatureSQL = "SELECT id_candidature FROM candidature WHERE id_Offre = ? AND id_candidat = ? LIMIT 1";
        int idCandidature = -1;

        try (PreparedStatement stmt = conn.prepareStatement(selectCandidatureSQL)) {
            stmt.setInt(1, idOffre);
            stmt.setInt(2, idCandidat);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                idCandidature = resultSet.getInt("id_candidature");
            }
        }

        if (idCandidature == -1) {
            System.out.println("Aucune candidature trouvée pour cette offre et ce candidat !");
            return;
        }

        String updateEntretienSQL = "UPDATE entretiens SET candidatId = ?, idCandidature = ? WHERE id = ?";

        try (PreparedStatement updateStmt = conn.prepareStatement(updateEntretienSQL)) {
            updateStmt.setInt(1, idCandidat);
            updateStmt.setInt(2, idCandidature);
            updateStmt.setInt(3, entretienId);
            updateStmt.executeUpdate();

            System.out.println("Entretien ajouté et mis à jour avec succès !");
        }
    }




    @Override
    public void modifier(Entretien entretien) throws SQLException {

        String sql = "UPDATE entretiens SET titre=?, description=?, date_entretien=?, heure_entretien=?, type_entretien=?, status=? WHERE id=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, entretien.getTitre());
        pstmt.setString(2, entretien.getDescription());
        pstmt.setDate(3, new java.sql.Date(entretien.getDate_entretien().getTime()));
        pstmt.setTime(4, entretien.getHeure_entretien());
        pstmt.setString(5, entretien.getType_entretien().name());
        pstmt.setBoolean(6, entretien.isStatus());
        pstmt.setInt(7, entretien.getId());

        pstmt.executeUpdate();
        System.out.println("Entretien modifié avec succès!");

    }



    public void updateEntretienWithId_offre(int idEntretienOld, Entretien entretien) throws SQLException {
        String getCandidatQuery = "SELECT candidatId, idCandidature, idOffre FROM entretiens WHERE id= ?";
        int idCandidat = -1;
        int idCandidature = -1;
        int idOffre = -1;

        try (PreparedStatement stmt = conn.prepareStatement(getCandidatQuery)) {
            stmt.setInt(1, idEntretienOld);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                idCandidat = resultSet.getInt("candidatId");
                idCandidature = resultSet.getInt("idCandidature");
                idOffre = resultSet.getInt("idOffre");
            }
        }

        if (idCandidat == -1 || idCandidature == -1) {
            System.out.println("Aucun candidat ou candidature trouvée pour cet entretien !");
            return;
        }

        String sql = "UPDATE entretiens SET titre = ?, description = ?, date_entretien = ?, heure_entretien = ?, type_entretien = ?, " +
                "status = ?, employe_id = ?, idOffre = ?, idCandidature = ?, candidatId = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entretien.getTitre());
            pstmt.setString(2, entretien.getDescription());
            pstmt.setDate(3, new java.sql.Date(entretien.getDate_entretien().getTime()));
            pstmt.setTime(4, entretien.getHeure_entretien());
            pstmt.setString(5, entretien.getType_entretien().name());
            pstmt.setBoolean(6, entretien.isStatus());
            pstmt.setInt(7, entretien.getEmployeId());
            pstmt.setInt(8, idOffre);
            pstmt.setInt(9, idCandidature);
            pstmt.setInt(10, idCandidat);
            pstmt.setInt(11, idEntretienOld);
            pstmt.executeUpdate();
            System.out.println("Entretien mis à jour avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la mise à jour de l'entretien !");
        }
    }


    @Override
    public void supprimer(int id) throws SQLException {

        String sql = "DELETE FROM entretiens WHERE id=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
        System.out.println("employes suprimé avec sucées");

    }

    @Override
    public List<Entretien> afficher() throws SQLException {
        List<Entretien> entretiens = new ArrayList<>();
        String sql = "SELECT * FROM entretiens";

        try (Statement stmt = conn.createStatement(); ResultSet resultSet = stmt.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String titre = resultSet.getString("titre");
                String description = resultSet.getString("description");
                Date dateEntretien = resultSet.getDate("date_entretien");
                Time heureEntretien = resultSet.getTime("heure_entretien");
                TypeEntretien typeEntretien = TypeEntretien.valueOf(resultSet.getString("type_entretien"));
                boolean status = resultSet.getBoolean("status");
                int feedbackId = resultSet.getInt("feedbackId");
                int employeId = resultSet.getInt("employe_id");
                int idOffre = resultSet.getInt("idOffre");
                int candidatId = resultSet.getInt("candidatId");
                int idCandidature = resultSet.getInt("idCandidature");

                Entretien entretien = new Entretien(id, titre, description, dateEntretien, heureEntretien, typeEntretien, status, candidatId, employeId, feedbackId, idOffre, idCandidature);
                entretiens.add(entretien);
            }
        }

        return entretiens;
    }

    public List<Entretien> rechercher(String keyword) throws SQLException {
        List<Entretien> entretiens = afficher();

        return entretiens.stream()
                .filter(entretien -> entretien.getTitre().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void affecterEntretien(int employeId, int entretienId) {
        String sql = "UPDATE entretiens SET employe_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeId);
            stmt.setInt(2, entretienId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(" Entretien " + entretienId + " affecté à l'employé " + employeId);
            } else {
                System.out.println(" Aucun entretien mis à jour. Vérifiez l'ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Entretien> getEntretiensByEmployeId(int employeId) {
        List<Entretien> entretiens = new ArrayList<>();
        String sql = "SELECT * FROM entretiens WHERE employe_id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, employeId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String titre = resultSet.getString("titre");
                String description = resultSet.getString("description");
                Date dateEntretien = resultSet.getDate("date_entretien");
                Time heureEntretien = resultSet.getTime("heure_entretien");
                TypeEntretien typeEntretien = TypeEntretien.valueOf(resultSet.getString("type_entretien"));
                boolean status = resultSet.getBoolean("status");
                int feedbackId = resultSet.getInt("feedbackId");
                int idOffre = resultSet.getInt("idOffre");
                int idCandidature = resultSet.getInt("idCandidature");
                int idCandidat = resultSet.getInt("candidatId");

                Entretien entretien = new Entretien(id, titre, description, dateEntretien, heureEntretien, typeEntretien, status, idCandidat
                        , employeId, feedbackId, idOffre, idCandidature);
                entretiens.add(entretien);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entretiens;
    }

    public Entretien getEntretienById(int id) {
        String sql = "SELECT * FROM entretiens WHERE id = ?";
        Entretien entretien = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                String titre = resultSet.getString("titre");
                String description = resultSet.getString("description");
                Date dateEntretien = resultSet.getDate("date_entretien");
                Time heureEntretien = resultSet.getTime("heure_entretien");
                TypeEntretien typeEntretien = TypeEntretien.valueOf(resultSet.getString("type_entretien"));
                boolean status = resultSet.getBoolean("status");
                int feedbackId = resultSet.getInt("feedbackId");
                int employeId = resultSet.getInt("employe_id");
                int idOffre = resultSet.getInt("idOffre");
                int idCandidature = resultSet.getInt("idCandidature");
                int idCandidat = resultSet.getInt("candidatId");

                entretien = new Entretien(id, titre, description, dateEntretien, heureEntretien, typeEntretien, status, idCandidat, employeId, feedbackId, idOffre, idCandidature);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entretien;
    }



    public void assignerFeedback(int entretienId, int feedbackId) throws SQLException {
        String query = "UPDATE entretiens SET feedbackId = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, feedbackId);
            pstmt.setInt(2, entretienId);
            pstmt.executeUpdate();
        }
    }


    public void reaassignerFeedback(int entretienId, int feedbackId) throws SQLException {
        String query = "UPDATE entretiens SET feedbackId = null  WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, feedbackId);
            pstmt.setInt(2, entretienId);
            pstmt.executeUpdate();
        }
    }


    public Entretien getEntretienByFeedbackId(int feedbackId) {
        String sql = "SELECT * FROM entretiens WHERE feedbackId = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, feedbackId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Entretien entretien = new Entretien();
                entretien.setTitre(resultSet.getString("titre"));
                entretien.setDescription(resultSet.getString("description"));
                entretien.setDate_entretien(resultSet.getDate("date_entretien"));
                entretien.setHeure_entretien(resultSet.getTime("heure_entretien"));
                entretien.setType_entretien(TypeEntretien.valueOf(resultSet.getString("type_entretien")));
                entretien.setStatus(resultSet.getBoolean("status"));
                entretien.setEmployeId(resultSet.getInt("employe_id"));
                entretien.setFeedbackId(resultSet.getInt("feedbackId"));
                return entretien;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no entretien is found
    }



    public List<User> getAllCandidatsSansEntretien(int idOffre) {
        ServiceCandidature serviceCandidature = new ServiceCandidature();
        ServiceUser userService = new ServiceUser();
        List<Candidature> candidatures = null;
        List<Entretien> entretiens = null;

        // Fetching all candidatures
        try {
            candidatures = serviceCandidature.afficher();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching candidatures", e);
        }

        try {
            entretiens = afficher();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching interviews", e);
        }

        List<Integer> candidatsAyantPostuleIds = candidatures.stream()
                .filter(c -> c.getIdOffre().getIdOffre() == idOffre)
                .map(Candidature::getIdCandidat)
                .collect(Collectors.toList());

        List<Integer> candidatsAvecEntretienIds = entretiens.stream()
                .filter(e -> e.getIdOffre() == idOffre && candidatsAyantPostuleIds.contains(e.getCandidatId()))
                .map(Entretien::getCandidatId)
                .collect(Collectors.toList());

        List<Integer> candidatsSansEntretienIds = candidatsAyantPostuleIds.stream()
                .filter(id -> !candidatsAvecEntretienIds.contains(id))
                .collect(Collectors.toList());

        return candidatsSansEntretienIds.stream()
                .map(id -> {
                    User user = null;
                    try {
                        user = userService.findbyid(id);
                    } catch (SQLException e) {
                        throw new RuntimeException("Error fetching user with ID: " + id, e);
                    }
                    if (user == null) {
                        System.err.println("User not found for ID: " + id);
                    }
                    return user;
                })
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }











}




































