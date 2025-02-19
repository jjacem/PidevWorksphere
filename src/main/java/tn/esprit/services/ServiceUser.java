package tn.esprit.services;

import tn.esprit.entities.Role;
import tn.esprit.entities.User;
import tn.esprit.utils.MyDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ServiceUser {

    Connection connection;
    public ServiceUser() {
        connection = MyDatabase.getInstance().getConnection();
    }
    // Récupérer un utilisateur avec le rôle "candidat"
    public User getCandidat() throws SQLException {
        String req = "SELECT * FROM user WHERE role = 'candidat' LIMIT 1"; // On prend juste un exemple de candidat
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        if (rs.next()) {
            User candidat = new User();
            candidat.setIdU(rs.getInt("id_u"));
            candidat.setNomU(rs.getString("nom_u"));
            candidat.setPrenomU(rs.getString("prenom_u"));
            candidat.setEmailU(rs.getString("email_u"));
            candidat.setMdpU(rs.getString("mdp_u"));
            candidat.setRole(Role.valueOf(rs.getString("role").toUpperCase()));

            return candidat; // Retourner l'utilisateur
        }

        return null; // Si aucun utilisateur n'est trouvé
    }

}
