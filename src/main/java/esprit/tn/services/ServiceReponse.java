package esprit.tn.services;

import esprit.tn.entities.Reclamation;
import esprit.tn.entities.Reponse;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceReponse implements IService<Reponse> {
    Connection connection;
    @Override
    public void ajouter(Reponse reponse) throws SQLException {
        String req = "INSERT INTO Reponse (message, id_employe, id_reclamation) " +
                "VALUES ('" + reponse.getMessage() + "', '" + reponse.getId_employe() + "', '" + reponse.getId_reclamation() + "')";
        Statement statement=connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("reponse ajoute");


    }

    @Override
    public void modifier(Reponse reponse) throws SQLException {
        String req = "UPDATE Reponse SET  message=? WHERE id_reponse=?";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(req);
            System.out.println("reponse modifie");
        }

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM Reponse WHERE id_reponse=?";
        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("reponse supprime");
    }

    @Override
    public List<Reponse> afficher() throws SQLException {
        String req = "SELECT * FROM Reponse";
        ArrayList<Reponse> reponses = new ArrayList<>();
        Statement statement= connection.createStatement();
        ResultSet rs= statement.executeQuery(req);

        while (rs.next()){

            Reponse reclamation = new Reponse(

                    rs.getString("message"),
                    rs.getInt("id_employe")
                    ,rs.getInt("id_reclamation")
            );
            reclamation.setId_reclamation(rs.getInt("id_Reponse"));


            reponses.add(reclamation);
        }


        return reponses;
}}
