package esprit.tn.services;

import esprit.tn.entities.Reclamation;
import esprit.tn.entities.Role;
import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation implements IService <Reclamation> {
    Connection connection;
    @Override
    public void ajouter(Reclamation reclamation) throws SQLException {
        String req = "INSERT INTO Reclamation (status, description,id_candidat) " +
                "VALUES ('" + reclamation.getStatus() + "', '" + reclamation.getDescription() + "' + '" + reclamation.getId_candidat() + "')";
        Statement statement=connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("reclamtation ajoute");

    }

    @Override
    public void modifier(Reclamation reclamation) throws SQLException {
String req = "UPDATE Reclamation SET  status=?, description=? WHERE id_reclamation=?";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(req);
            System.out.println("reclamation modifie");
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM Reclamation WHERE id_reclamation=?";
        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("reclamation supprime");

    }

    @Override
    public List<Reclamation> afficher() throws SQLException {
       String req = "SELECT * FROM Reclamation";
        ArrayList<Reclamation> reclamations = new ArrayList<>();
        Statement statement= connection.createStatement();

        ResultSet rs= statement.executeQuery(req);
        while (rs.next()){

            Reclamation reclamation = new Reclamation(

                    rs.getString("status"),
                    rs.getString("description")
            );
            reclamation.setId_reclamation(rs.getInt("id_reclamation"));


            reclamations.add(reclamation);
        }


        return reclamations;
    }

}
