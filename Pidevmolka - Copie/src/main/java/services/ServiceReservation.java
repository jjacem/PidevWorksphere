package services;

import entities.Reservation;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation implements IServiceReservation <Reservation>{
    Connection connection;
    public ServiceReservation() {
        connection= MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouterReservation(Reservation reservation) throws SQLException {
        String req="INSERT INTO reservation(date,id_f,id_user)"+
                "VALUES ('"+reservation.getDate()+"','"+reservation.getFormationId()+"','"+reservation.getUserId()+"')";
        Statement statement=connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("Reservation ajoute");
    }

    @Override
    public void modifierReservation(Reservation reservation) throws SQLException {
        String req = "UPDATE reservation SET date=?, id_f=?, id_user=? WHERE id_r=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setDate(1, java.sql.Date.valueOf(reservation.getDate()));
        preparedStatement.setInt(2, reservation.getFormationId());
        preparedStatement.setInt(3, reservation.getUserId());
        preparedStatement.setInt(4, reservation.getId_r());

        preparedStatement.executeUpdate();
        preparedStatement.close();
        System.out.println("Réservation mise à jour avec succès.");
    }

    @Override
    public void supprimeReservation(Reservation reservation) throws SQLException {
        String req = "DELETE FROM reservation WHERE id_r=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, reservation.getId_r());

        preparedStatement.executeUpdate();
        preparedStatement.close();
        System.out.println("Réservation supprimée avec succès.");
    }



    @Override
    public List getListReservation() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String req="select * from reservation";
        Statement statement= connection.createStatement();
        ResultSet rs= statement.executeQuery(req);
        while (rs.next()){
            Reservation r = new Reservation();
            r.setId_r(rs.getInt("id_r"));
            r.setDate(rs.getDate(2).toLocalDate());
            r.setFormationId(rs.getInt("id_f"));
            r.setUserId(rs.getInt("id_user"));

            reservations.add(r);
        }

        return reservations;
    }
}
