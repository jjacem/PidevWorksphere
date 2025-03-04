package esprit.tn.services;

import esprit.tn.entities.Reponse;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReponse implements IService<Reponse> {
    private final Connection connection;

    public ServiceReponse() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Reponse reponse) throws SQLException {
        String req = "INSERT INTO Reponse (message, id_user, id_reclamation, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, reponse.getMessage());
            preparedStatement.setInt(2, reponse.getId_user());
            preparedStatement.setInt(3, reponse.getId_reclamation());
            preparedStatement.setString(4, reponse.getStatus());

            preparedStatement.executeUpdate();
            System.out.println("Réponse ajoutée avec succès.");
        }
    }

    @Override
    public void modifier(Reponse reponse) throws SQLException {
        String req = "UPDATE Reponse SET message = ?, status = ? WHERE id_reponse = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, reponse.getMessage());
            preparedStatement.setString(2, reponse.getStatus());
            preparedStatement.setInt(3, reponse.getId_reponse());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Réponse modifiée avec succès.");
            } else {
                System.out.println("Aucune réponse trouvée avec cet ID.");
            }
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM Reponse WHERE id_reponse = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Réponse supprimée avec succès.");
            } else {
                System.out.println("Aucune réponse trouvée avec cet ID.");
            }
        }
    }

    @Override
    public List<Reponse> afficher() throws SQLException {
        String req = "SELECT * FROM Reponse";
        List<Reponse> reponses = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Reponse reponse = new Reponse(
                        rs.getString("message"),
                        rs.getInt("id_user"),
                        rs.getInt("id_reclamation"),
                        rs.getString("status")
                );
                reponse.setId_reponse(rs.getInt("id_reponse"));
                reponse.setDatedepot(rs.getTimestamp("datedepot"));

                reponses.add(reponse);
            }
        }

        return reponses;
    }
    public Reponse getresponsebyid(int id){
        String req = "SELECT * FROM Reponse WHERE id_reponse = ?";
        Reponse reponse = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    reponse = new Reponse(
                            rs.getString("message"),
                            rs.getInt("id_user"),
                            rs.getInt("id_reclamation"),
                            rs.getString("status")
                    );
                    reponse.setId_reponse(rs.getInt("id_reponse"));
                    reponse.setDatedepot(rs.getTimestamp("datedepot"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reponse;
    }

    public Reponse checkForRepInRec(int idrec) {
        String req = "SELECT * FROM Reponse WHERE id_reclamation = ?";
        Reponse response = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, idrec);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) { // Fetch only the first response (since each reclamation has only one response)
                    response = new Reponse(
                            rs.getString("message"),
                            rs.getInt("id_user"),
                            rs.getInt("id_reclamation"),
                            rs.getString("status")
                    );
                    response.setId_reponse(rs.getInt("id_reponse"));
                    response.setDatedepot(rs.getTimestamp("datedepot"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return response; // Return the found response or null if no response exists
    }
}