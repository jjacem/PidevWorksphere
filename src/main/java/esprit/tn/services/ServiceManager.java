package esprit.tn.services;

import esprit.tn.entities.*;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceManager implements IService<Manager> {
    private Connection connection;

    public ServiceManager() {
        connection = MyDatabase.getInstance().getConnection();
    }



ServiceUser usersevice=new ServiceUser();
    @Override
    public void ajouter(Manager manager) throws SQLException {
        User u1=usersevice.extractuser(manager);
        int i= usersevice.ajouterwithid(u1);
        if (i>-1){ String req = "INSERT INTO Manager (id_user,nombreProjet,budget,departement_manage) " +
                "VALUES ('" + i + "', '" + manager.getNombreprojet() + "', '" + manager.getBudget() + "', '" + manager.getDapartement_manage() + "')";
            Statement statement=connection.createStatement();
            statement.executeUpdate(req);


            System.out.println("Candidat ajouté avec succès.");}
    }

    @Override
    public void modifier(Manager manager) throws SQLException {
        String req = "UPDATE Manager SET  nombreProjet=?, budget=?,departement_manage=? WHERE id_manager=?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {

            statement.setInt(1, manager.getNombreprojet());
            statement.setDouble(2, manager.getBudget());

            statement.setString(3, manager.getDapartement_manage());
            statement.setInt(4, manager.getId_manager());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Emplye modifié avec succès.");
            } else {
                System.out.println("Aucun Employe trouvé avec cet ID.");
            }
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM Manager WHERE id_manager=?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Manager supprimé avec succès.");
            } else {
                System.out.println("Aucun manager trouvé avec cet ID.");
            }
        }
    }

    @Override
    public List<Manager> afficher() throws SQLException
    {
        List<Manager> managers = new ArrayList<>();
        String req = "SELECT * FROM Manager";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                User u1 = usersevice.findbyid(rs.getInt("id_user"));
                u1.setIdUser(rs.getInt("id_user"));

                Manager manager = new Manager(
                        u1,
                        rs.getInt("nombreProjet"),
                        rs.getDouble("budget"),
                        rs.getString("departement_manage")

                );

                manager.setId_manager(rs.getInt("id_manager"));
                managers.add(manager);
            }
        }

        return managers;
    }


    public int findidbyemail(String email){
        String req = "SELECT e.id_manager FROM Manager e JOIN User u ON e.id_user = u.id_user WHERE u.email = ?;"
                ;
        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_manager");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
