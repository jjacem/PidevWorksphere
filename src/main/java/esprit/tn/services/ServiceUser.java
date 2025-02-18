package esprit.tn.services;

import esprit.tn.entities.Role;
import esprit.tn.entities.User;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements IService<User>{
    Connection connection;
    public ServiceUser (){
        connection= MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouterUser(User user) throws SQLException {
        String req = "INSERT INTO user (nom, prenom, email, mdp, role, adresse) " +
                "VALUES ('" + user.getNom() + "', '" + user.getPrenom() + "', '" + user.getEmail() + "', '" +
                user.getMdp() + "', '" + user.getRole() + "', '" + user.getAdresse() + "')";

        Statement statement = connection.createStatement();
        statement.executeUpdate(req);

        System.out.println("Utilisateur ajouté");
    }

    @Override
    public User getuserByid(int id) throws SQLException {
        String req = "SELECT * FROM user WHERE id =?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setRole(Role.valueOf(rs.getString("role")));
                    user.setEmail(rs.getString("email"));
                    user.setMdp(rs.getString("mdp"));
                    user.setAdresse(rs.getString("adresse"));

                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public List<User> getAllusers() throws SQLException {
        List<User> users= new ArrayList<>();
        String req="select * from user";
        Statement statement= connection.createStatement();

        ResultSet rs= statement.executeQuery(req);
        while (rs.next()){
            User u= new User();
            u.setNom(rs.getString("nom"));
            u.setPrenom(rs.getString("prenom"));
            u.setId(rs.getInt("id_user"));
            //convertir le role en chaine de caractère
            String roleStr = rs.getString("role");
            if (roleStr != null) {
                u.setRole(Role.valueOf(roleStr));
            }
            u.setEmail(rs.getString("email"));
            u.setMdp(rs.getString("mdp"));
            u.setAdresse(rs.getString("adresse"));

            users.add(u);
        }
        return users;
    }
}
