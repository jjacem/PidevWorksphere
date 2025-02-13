package service;

import entities.*;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEquipe implements IServiceEquipe<Equipe>{
    Connection connection;
    public ServiceEquipe(){
        connection= MyDatabase.getInstance().getConnection();

    }


    @Override
    public void ajouterEquipe(Equipe equipe) throws SQLException {
        // hne insertion te3 equipe
        String req = "INSERT INTO equipe (nom_equipe) VALUES (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, equipe.getNomEquipe());
        preparedStatement.executeUpdate();

        // hne 9a3din ne5dhou f id te3 equipe insérer
        String selectReq = "SELECT id FROM equipe WHERE nom_equipe = ? ORDER BY id DESC LIMIT 1";
        PreparedStatement selectStatement = connection.prepareStatement(selectReq);
        selectStatement.setString(1, equipe.getNomEquipe());
        ResultSet rs = selectStatement.executeQuery();

        if (!rs.next()) throw new SQLException("Erreur lors de la récupération de l'ID de l'équipe.");
        int equipeId = rs.getInt("id");

        // hne 3malna association bin equipe w employe puisque kol equipe feha akthe men employé
        for (Employee emp : equipe.getEmployes()) {
            PreparedStatement assocStatement = connection.prepareStatement("INSERT INTO equipe_employee (equipe_id, employee_id) VALUES (?, ?)");
            assocStatement.setInt(1, equipeId);
            assocStatement.setInt(2, emp.getId());
            assocStatement.executeUpdate();
        }
    }

    @Override
    public void modifierEquipe(Equipe equipe) throws SQLException {

        String req = "UPDATE equipe SET nom_equipe = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setString(1, equipe.getNomEquipe());
        preparedStatement.setInt(2, equipe.getId());
        preparedStatement.executeUpdate();

        // hne fas5na association le9dima
        String deleteAndInsert = "DELETE FROM equipe_employee WHERE equipe_id = ?; ";
        PreparedStatement statement = connection.prepareStatement(deleteAndInsert);
        statement.setInt(1, equipe.getId());
        statement.executeUpdate();

        //hne 3malna association bin equipe w employe el jdida

        String assocReq = "INSERT INTO equipe_employee (equipe_id, employee_id) VALUES (?, ?)";
        PreparedStatement assocStatement = connection.prepareStatement(assocReq);
        for (Employee emp : equipe.getEmployes()) {
            assocStatement.setInt(1, equipe.getId());
            assocStatement.setInt(2, emp.getId());
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
        int equipesupprimer = deleteEquipe.executeUpdate();

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

            String employesReq = "SELECT e.id, e.nom, e.prenom FROM employee e " +
                    "JOIN equipe_employee ee ON e.id = ee.employee_id WHERE ee.equipe_id = ?";
            PreparedStatement employesStmt = connection.prepareStatement(employesReq);
            employesStmt.setInt(1, id);
            ResultSet employesRs = employesStmt.executeQuery();

            List<Employee> employes = new ArrayList<>();
            while (employesRs.next()) {
                employes.add(new Employee(
                        employesRs.getInt("id"),
                        employesRs.getString("nom"),
                        employesRs.getString("prenom")
                ));
            }

            equipes.add(new Equipe(id, nomEquipe, employes));
        }
        return equipes;
    }







}
