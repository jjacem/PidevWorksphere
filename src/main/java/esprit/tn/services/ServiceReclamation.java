package esprit.tn.services;

import esprit.tn.entities.Reclamation;
import esprit.tn.entities.Reponse;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceReclamation implements IService<Reclamation> {
    private final Connection connection;

    public ServiceReclamation() {
        connection = MyDatabase.getInstance().getConnection();
    }

    public void ajouter(Reclamation reclamation) throws SQLException {
        String req = "INSERT INTO Reclamation (status, titre, description, type, id_user, id_user2) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, reclamation.getStatus());
            statement.setString(2, reclamation.getTitre());
            statement.setString(3, reclamation.getDescription());
            statement.setString(4, reclamation.getType());
            statement.setInt(5, reclamation.getId_user());
            statement.setInt(6, reclamation.getId_user2());

            statement.executeUpdate();
            System.out.println("Réclamation ajoutée avec succès.");
        }
    }

    public List<Reclamation> getReclamationsByUser(int id_user) {
        String req = "SELECT * FROM Reclamation WHERE id_user=?";
        List<Reclamation> reclamations = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, id_user);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Reclamation reclamation = new Reclamation(
                        rs.getString("status"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getInt("id_user"),
                        rs.getInt("id_user2")
                );
                reclamation.setId_reclamation(rs.getInt("id_reclamation"));
                reclamation.setDatedepot(rs.getTimestamp("datedepot")); // ✅ Récupérer datedepot
                reclamations.add(reclamation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reclamations;
    }

    public List<Reclamation> getReclamationsByUser2(int id_user2) {
        String req = "SELECT * FROM Reclamation WHERE id_user2=?";
        List<Reclamation> reclamations = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, id_user2);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Reclamation reclamation = new Reclamation(
                        rs.getString("status"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getInt("id_user"),
                        rs.getInt("id_user2")
                );
                reclamation.setId_reclamation(rs.getInt("id_reclamation"));
                reclamation.setDatedepot(rs.getTimestamp("datedepot")); // ✅ Récupérer datedepot
                reclamations.add(reclamation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reclamations;
    }

    public void modifier(Reclamation reclamation) throws SQLException {
        String req = "UPDATE Reclamation SET status=?, titre=?, description=?, type=?, id_user=?, id_user2=? WHERE id_reclamation=?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setString(1, reclamation.getStatus());
            statement.setString(2, reclamation.getTitre());
            statement.setString(3, reclamation.getDescription());
            statement.setString(4, reclamation.getType());
            statement.setInt(5, reclamation.getId_user());
            statement.setInt(6, reclamation.getId_user2());
            statement.setInt(7, reclamation.getId_reclamation());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Réclamation modifiée avec succès.");
            } else {
                System.out.println("Aucune réclamation trouvée avec cet ID.");
            }
        }
    }

    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM Reclamation WHERE id_reclamation=?";
        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Réclamation supprimée.");
        }
    }

    public List<Reclamation> afficher() throws SQLException {
        String req = "SELECT * FROM Reclamation";
        List<Reclamation> reclamations = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Reclamation reclamation = new Reclamation(
                        rs.getString("status"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getInt("id_user"),
                        rs.getInt("id_user2")
                );
                reclamation.setId_reclamation(rs.getInt("id_reclamation"));
                reclamation.setDatedepot(rs.getTimestamp("datedepot"));
             String   req2 = "SELECT * FROM Reponse WHERE id_reclamation=?";
                try (PreparedStatement statement2 = connection.prepareStatement(req2)) {
                    statement2.setInt(1, reclamation.getId_reclamation());
                    ResultSet rs2 = statement2.executeQuery();
                    while (rs2.next()) {
                        ServiceReponse serviceReponse = new ServiceReponse();
                        reclamation.setReponse(serviceReponse.getresponsebyid( rs2.getInt("id_reponse")));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                reclamations.add(reclamation);
            }
        }
        return reclamations;
    }

    // ✅ Trouver l'ID d'une réclamation en fonction de l'utilisateur et du destinataire
    public int findIdReclamation(int id_user, int id_user2) {
        String req = "SELECT id_reclamation FROM Reclamation WHERE id_user=? AND id_user2=?";
        int id = 0;
        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, id_user);
            statement.setInt(2, id_user2);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id_reclamation");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }
    public Reclamation getReclamationById(int id) throws SQLException {
        String query = "SELECT * FROM reclamation WHERE id_reclamation = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            Reclamation r= new Reclamation(
                    rs.getString("status"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getString("type"),
                    rs.getInt("id_user"),
                    rs.getInt("id_user2")
            );

            r.setId_reclamation(rs.getInt("id_reclamation"));
            return r;

        }
        return null;
    }
    public List<Reclamation> filterbytitle(String title) throws SQLException {
        List<Reclamation> responses = this.afficher();
        responses = responses.stream()
                .filter(r -> r.getStatus().equals(title))
                .collect(Collectors.toList());
        return responses;
    }

    public List<Reclamation> filterbystats(String status) throws SQLException {
        List<Reclamation> responses = this.afficher();
        responses = responses.stream()
                .filter(r -> r.getStatus().equals(status))
                .collect(Collectors.toList());
        return responses;
    }

}
