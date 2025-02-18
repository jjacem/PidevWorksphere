package esprit.tn.services;

import esprit.tn.entities.Reponse;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReponse implements IService<Reponse> {
    private Connection connection;

    public ServiceReponse() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Reponse reponse) throws SQLException {
        String req = "INSERT INTO Reponse (message, id_user , id_reclamation) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, reponse.getMessage());
            preparedStatement.setInt(2, reponse.getId_employe());
            preparedStatement.setInt(3, reponse.getId_reclamation());

            preparedStatement.executeUpdate();
            System.out.println("Réponse ajoutée avec succès.");
        }
    }

    @Override
    public void modifier(Reponse reponse) throws SQLException {
        String req = "UPDATE Reponse SET message = ? WHERE id_reponse = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, reponse.getMessage());
            preparedStatement.setInt(2, reponse.getId_reponse());

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
                        rs.getInt("id_reclamation")
                );
                reponse.setId_reponse(rs.getInt("id_reponse"));

                reponses.add(reponse);
            }
        }

        return reponses;
    }
}
