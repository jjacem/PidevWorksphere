package esprit.tn.services;

import esprit.tn.entities.Langue;
import esprit.tn.entities.Reservation;
import esprit.tn.entities.User;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation implements IServiceReservation<Reservation> {
    Connection connection;

    public ServiceReservation() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouterReservation(Reservation reservation) throws SQLException {
        String req = "INSERT INTO reservation (date, id_f, id_user, motif_r, attente, langue) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setDate(1, java.sql.Date.valueOf(reservation.getDate()));
            statement.setInt(2, reservation.getFormationId());
            statement.setInt(3, reservation.getUserId());
            statement.setString(4, reservation.getMotif());
            statement.setString(5, reservation.getAttente());
            statement.setString(6, reservation.getLang().toString());

            statement.executeUpdate();
            System.out.println("Réservation ajoutée.");
        }
    }

    @Override
    public void modifierReservation(Reservation reservation) throws SQLException {
        String req = "UPDATE reservation SET date=?, id_f=?, id_user=?, motif_r=?, attente=?, langue=? WHERE id_r=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setDate(1, java.sql.Date.valueOf(reservation.getDate()));
            preparedStatement.setInt(2, reservation.getFormationId());
            preparedStatement.setInt(3, reservation.getUserId());
            preparedStatement.setString(4, reservation.getMotif());
            preparedStatement.setString(5, reservation.getAttente());
            preparedStatement.setString(6, reservation.getLang().name());


            preparedStatement.executeUpdate();
            System.out.println("Réservation mise à jour avec succès.");
        }
    }

    @Override
    public void supprimeReservation(Reservation reservation) throws SQLException {
        String req = "DELETE FROM reservation WHERE id_r=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, reservation.getId_r());
            preparedStatement.executeUpdate();
            System.out.println("Réservation supprimée avec succès.");
        }
    }

    @Override
    public List<Reservation> getListReservation() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId_r(rs.getInt("id_r"));
                r.setDate(rs.getDate("date").toLocalDate());
                r.setFormationId(rs.getInt("id_f"));
                r.setUserId(rs.getInt("id_user"));
                r.setMotif(rs.getString("motif_r"));
                r.setAttente(rs.getString("attente"));

                String langStr = rs.getString("langue"); // Correction ici
                if (langStr != null) {
                    r.setLang(Langue.valueOf(langStr));
                }

                reservations.add(r);
            }
        }
        return reservations;
    }
    public List<Reservation> getReservationsByUser(int userId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation WHERE id_user = ? OR id_f IN (SELECT id_f FROM formation WHERE id_user = ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId_r(rs.getInt("id_r"));
                reservation.setDate(rs.getDate("date").toLocalDate());
                reservation.setUserId(rs.getInt("id_user"));
                reservation.setFormationId(rs.getInt("id_f"));
                reservation.setMotif(rs.getString("motif_r"));
                reservation.setAttente(rs.getString("attente"));

                // Récupération de la langue
                String langStr = rs.getString("langue");
                if (langStr != null) {
                    reservation.setLang(Langue.valueOf(langStr));
                }

                reservations.add(reservation);
            }
        }
        return reservations;
    }



    public List<User> getUsersWhoReservedFormation(int formationId) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.* FROM reservation r JOIN user u ON r.id_user = u.id_user WHERE r.id_f = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, formationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("id"));  // Assure-toi que la colonne s'appelle bien "id" dans la table `user`
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }
        }

        return users;
    }



}
