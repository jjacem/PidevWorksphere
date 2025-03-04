package esprit.tn.services;

import esprit.tn.entities.Formation;

import java.sql.SQLException;
import java.util.List;

public interface IServiceFavori<T> {

    void ajouterFavori(int userId, int formationId) throws SQLException;
    void supprimerFavori(int userId, int formationId) throws SQLException;
    boolean estFavori(int userId, int formationId) throws SQLException;
    List<T> getFavoris(int userId) throws SQLException;
}