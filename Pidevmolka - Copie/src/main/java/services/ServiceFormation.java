package services;

import entities.Formation;
import entities.Typeformation;
import entities.User;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFormation implements IServiceFormation <Formation>  {
    Connection connection;
    public ServiceFormation() {
        connection= MyDatabase.getInstance().getConnection();
    }


    @Override
    public void ajouterFormation(Formation formation) throws SQLException {
        String req="INSERT INTO formation(titre,description,date,heure_debut,heure_fin,nb_place,type,id_user)"+
                "VALUES ('"+formation.getTitre()+"','"+formation.getDescription()+"','"+formation.getDate()+"', '"+formation.getHeure_debut()+"','"+formation.getHeure_fin()+"','"+formation.getNb_place()+"','"+formation.getType()+"','"+ formation.getUserId()+"')";
        Statement statement=connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("formation ajoute");
    }

    @Override
    public void modifierFormation(Formation formation) throws SQLException {
        String req = "UPDATE formation SET titre=?, description=?, date=?, heure_debut=?, heure_fin=?, nb_place=?, type=?, id_user=? WHERE id_f=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);

        preparedStatement.setString(1, formation.getTitre());
        preparedStatement.setString(2, formation.getDescription());
        preparedStatement.setDate(3, java.sql.Date.valueOf(formation.getDate()));
        preparedStatement.setTime(4, java.sql.Time.valueOf(formation.getHeure_debut()));
        preparedStatement.setTime(5, java.sql.Time.valueOf(formation.getHeure_fin()));
        preparedStatement.setInt(6, formation.getNb_place());
        preparedStatement.setString(7, formation.getType().toString());
        preparedStatement.setInt(8, formation.getUserId());
        preparedStatement.setInt(9, formation.getId_f());

        preparedStatement.executeUpdate();
        System.out.println("Formation mise à jour avec succès.");
        preparedStatement.close();
    }

    @Override
    public void supprimeFormation(Formation formation) throws SQLException {
        String req = "DELETE FROM formation WHERE id_f = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, formation.getId_f());
        preparedStatement.executeUpdate();
        System.out.println("Formation supprimée avec succès.");
        preparedStatement.close();
    }

    @Override
    public List getListFormation() throws SQLException {
        List<Formation> formations = new ArrayList<>();
        String req="select * from formation";
        Statement statement= connection.createStatement();
        ResultSet rs= statement.executeQuery(req);
        while (rs.next()){
            Formation f= new Formation ();
            f.setId_f(rs.getInt("id_f"));
            f.setTitre(rs.getString("titre"));
            f.setDescription(rs.getString("description"));
            f.setDate(rs.getDate(4).toLocalDate());
            f.setHeure_debut(rs.getTime(5).toLocalTime());
            f.setHeure_fin(rs.getTime(6).toLocalTime());
            f.setNb_place(rs.getInt("nb_place"));
            String typeStr = rs.getString("type");
            if (typeStr != null) {
                f.setType(Typeformation.valueOf(typeStr));
            }
            f.setUserId(rs.getInt("id_user"));

            formations.add(f);
        }

        return formations;

    }
}
