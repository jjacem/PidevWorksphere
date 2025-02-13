package esprit.tn.services;

import esprit.tn.entities.*;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRH implements IService<RH> {
    private Connection connection;

    public ServiceRH() {
        connection = MyDatabase.getInstance().getConnection();
    }

    ServiceUser userService = new ServiceUser();

    @Override
    public void ajouter(RH rh) throws SQLException {
        User u1 = userService.extractuser(rh);
        int i = userService.ajouterwithid(u1);
        if (i > -1) {
            String req = "INSERT INTO RH (id_user, competence, specialisation, and_exeprience) " +
                    "VALUES ('" + i + "', '" + rh.getCompetence() + "', '" + rh.getSpecialisation() + "', '" + rh.getAnd_exeprience() + "')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(req);

            System.out.println("RH ajouté avec succès.");
        }
    }

    @Override
    public void modifier(RH rh) throws SQLException {
        String req = "UPDATE RH SET competence=?, specialisation=?, and_exeprience=? WHERE id_rh=?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setString(1, rh.getCompetence());
            statement.setString(2, rh.getSpecialisation());
            statement.setInt(3, rh.getAnd_exeprience());
            statement.setInt(4, rh.getId_rh());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("RH modifié avec succès.");
            } else {
                System.out.println("Aucun RH trouvé avec cet ID.");
            }
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM RH WHERE id_rh=?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("RH supprimé avec succès.");
            } else {
                System.out.println("Aucun RH trouvé avec cet ID.");
            }
        }
    }

    @Override
    public List<RH> afficher() throws SQLException {
        List<RH> rhList = new ArrayList<>();
        String req = "SELECT * FROM RH";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                User u1 = userService.findbyid(rs.getInt("id_user"));
                u1.setIdUser(rs.getInt("id_user"));

                RH rh = new RH(
                        u1,

                        rs.getString("competence"),
                        rs.getString("specialisation"),
                        rs.getInt("and_exeprience")
                );

                rhList.add(rh);
            }
        }

        return rhList;
    }
    public int findidbyemail(String email){
        String req = "SELECT e.id_rh FROM Employe e JOIN User u ON e.id_user = u.id_user WHERE u.email = ?;"
                ;
        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_rh");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
