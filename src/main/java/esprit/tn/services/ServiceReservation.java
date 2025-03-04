package esprit.tn.services;

import esprit.tn.entities.Reservation;
import esprit.tn.utils.MyDatabase;

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
        String req = "INSERT INTO reservation (date, id_f, id_user) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setDate(1, java.sql.Date.valueOf(reservation.getDate()));
            statement.setInt(2, reservation.getFormationId()); // Assurez-vous que la formation est correctement définie
            statement.setInt(3, reservation.getUserId()); // Utilisez l'ID de l'utilisateur

            statement.executeUpdate();
            System.out.println("Réservation ajoutée.");
        }
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
