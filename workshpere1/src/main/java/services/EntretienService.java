package services;

import entities.Entretien;
import entities.TypeEntretien;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntretienService implements Iservice<Entretien> {


    private Connection conn;

    public EntretienService() {
        conn = MyDatabase.getInstance().getConnection();
    }


    @Override
    public void ajouter(Entretien entretien) throws SQLException {

        String sql = "INSERT INTO entretiens " +
            "(titre , description , date_entretien, heure_entretien, type_entretien, status) VALUES (?,?,?,?,?,?)";
        PreparedStatement pstmt  = conn.prepareStatement(sql);
        pstmt.setString(1, entretien.getTitre());
        pstmt.setString(2, entretien.getDescription());
        pstmt.setDate(3, new java.sql.Date(entretien.getDate_entretien().getTime()));
        pstmt.setTime(4, entretien.getHeure_entretien());
        pstmt.setString(5, entretien.getType_entretien().name());
        pstmt.setBoolean(6, entretien.isStatus());
        pstmt.executeUpdate();
        System.out.println("entretien ajouté avec sucées");
    }

    @Override
    public void modifier(Entretien entretien) throws SQLException {

        String sql = "UPDATE entretiens SET titre=?, description=?, date_entretien=?, heure_entretien=?, type_entretien=?, status=? WHERE id=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, entretien.getTitre());
        pstmt.setString(2, entretien.getDescription());
        pstmt.setDate(3, new java.sql.Date(entretien.getDate_entretien().getTime()));
        pstmt.setTime(4, entretien.getHeure_entretien());
        pstmt.setString(5, entretien.getType_entretien().name());
        pstmt.setBoolean(6, entretien.isStatus());
        pstmt.setInt(7, entretien.getId());

        pstmt.executeUpdate();
        System.out.println("Entretien modifié avec succès!");

    }

    @Override
    public void supprimer(int id) throws SQLException {

        String sql = "DELETE FROM entretiens WHERE id=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
        System.out.println("employes suprimé avec sucées");

    }

    @Override
    public List<Entretien> afficher() throws SQLException {
        List<Entretien> entretiens = new ArrayList<>();
        String sql = "SELECT * FROM entretiens";
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);

        while (resultSet.next()) {
            String titre = resultSet.getString("titre");
            String description = resultSet.getString("description");
            Date dateEntretien = resultSet.getDate("date_entretien");
            Time heureEntretien = resultSet.getTime("heure_entretien");
            TypeEntretien typeEntretien = TypeEntretien.valueOf(resultSet.getString("type_entretien"));
            boolean status = resultSet.getBoolean("status");

            Entretien entretien = new Entretien( titre, description, dateEntretien, heureEntretien, typeEntretien, status);
            entretiens.add(entretien);
        }

        return entretiens;
}


    }







