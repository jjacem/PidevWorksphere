package esprit.tn.services;

import esprit.tn.entities.Reservation;

import java.sql.SQLException;
import java.util.List;

public interface IServiceReservation <T> {

    void ajouterReservation(Reservation reservation) throws SQLException;
    void modifierReservation(T t) throws SQLException;
    void supprimeReservation(T t) throws SQLException;
    List<T> getListReservation() throws SQLException;
}

