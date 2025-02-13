package esprit.tn.services;

import esprit.tn.entities.*;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEmploye implements IService<Employe> {
    private Connection connection;

    public ServiceEmploye() {
        connection = MyDatabase.getInstance().getConnection();
    }

    ServiceUser usersevice = new ServiceUser();

    @Override
    public void ajouter(Employe employe) throws SQLException {
        User u1 = usersevice.extractuser(employe);
        int i = usersevice.ajouterwithid(u1);
        if (i > -1) {
            String req = "INSERT INTO employe (id_user, poste, salaire, experience_travail, departement) " +
                    "VALUES ('" + i + "', '" + employe.getPoste() + "', '" + employe.getSalaire() + "', '" +
                    employe.getExperience_travail() + "', '" + employe.getDepartement() + "')";

            Statement statement = connection.createStatement();
            statement.executeUpdate(req);


            System.out.println("employe ajouté avec succès.");
        }


    }

    @Override
    public void modifier(Employe employe) throws SQLException {
        String req = "UPDATE Employe SET  poste=?, salaire=?,experience_travail=?,departement=? WHERE id_employe=?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {

            statement.setString(1, employe.getPoste());
            statement.setDouble(2, employe.getSalaire());
            statement.setInt(3, employe.getExperience_travail());
            statement.setString(4, employe.getDepartement());
            statement.setInt(5, employe.getId_employe());

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
        String req = "DELETE FROM Employe WHERE id_employe=?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Employé supprimé avec succès.");
            } else {
                System.out.println("Aucun employé trouvé avec cet ID.");
            }
        }
    }

    @Override
    public List<Employe> afficher() throws SQLException {
        List<Employe> employes = new ArrayList<>();
        String req = "SELECT * FROM Employe";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                User u1 = usersevice.findbyid(rs.getInt("id_user"));
                u1.setIdUser(rs.getInt("id_user"));

                Employe employe = new Employe(
                        u1,
                        rs.getString("poste"),
                        rs.getDouble("salaire"),
                        rs.getInt("experience_travail"),
                        rs.getString("departement")
                );

                employe.setId_employe(rs.getInt("id_employe"));
                employes.add(employe);
            }
        }

        return employes;
    }
    public int findidbyemail(String email){
        String req = "SELECT e.id_employe FROM Employe e JOIN User u ON e.id_user = u.id_user WHERE u.email = ?;"
                ;
        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_employe");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}