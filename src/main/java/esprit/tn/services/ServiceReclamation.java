package esprit.tn.services;

import esprit.tn.entities.Reclamation;
import esprit.tn.entities.Role;
import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation implements IService <Reclamation> {

    Connection connection;
    public ServiceReclamation(){connection = MyDatabase.getInstance().getConnection();}
    @Override
    public void ajouter(Reclamation reclamation) throws SQLException {
        String req = "INSERT INTO Reclamation (status, message_rec, id_user) " +
                "VALUES ('" + reclamation.getStatus() + "', '" + reclamation.getDescription() + "', '" + reclamation.getId_candidat() + "')";

        Statement statement=connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("reclamtation ajoute");



    }

    @Override
    public void modifier(Reclamation reclamation) throws SQLException {
String req = "UPDATE Reclamation SET  status=?, message_rec=? WHERE id_reclamation=?";
        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setString(1, reclamation.getStatus());
            statement.setString(2, reclamation.getDescription());
            statement.setInt(3, reclamation.getId_reclamation());



            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("reclamation modifiée avec succès.");
            } else {
                System.out.println("Aucune reclamation trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM Reclamation WHERE id_reclamation=?";
        PreparedStatement statement= connection.prepareStatement(req);
        statement.setInt(1,id);
        statement.executeUpdate();
        System.out.println("rec supprimee");

    }

    @Override
    public List<Reclamation> afficher() throws SQLException {
       String req = "SELECT * FROM Reclamation";
       String req2 = "SELECT * FROM User";
        ArrayList<Reclamation> reclamations = new ArrayList<>();
        Statement statement= connection.createStatement();

        ResultSet rs= statement.executeQuery(req);
        while (rs.next()){

            Reclamation reclamation = new Reclamation(

                    rs.getString("status"),
                    rs.getString("message_rec")
            );
            reclamation.setId_reclamation(rs.getInt("id_reclamation"));


            reclamations.add(reclamation);
        }


        return reclamations;
    }

}
