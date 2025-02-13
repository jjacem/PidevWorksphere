package services;
import entities.Role;
import entities.User;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements IService<User>{

    Connection connection;
    public ServiceUser (){
        connection= MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouteruser(User user) throws SQLException {

    }

    @Override
    public void modifier(User user) throws SQLException {

    }

    @Override
    public void supprimer(int id) throws SQLException {

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
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public List<User> afficher() throws SQLException {
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

            users.add(u);
        }


        return users;
    }
}
