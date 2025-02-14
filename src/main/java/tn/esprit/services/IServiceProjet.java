package tn.esprit.services;

import java.sql.SQLException;
import java.util.List;

public interface IServiceProjet <T>{
    public void ajouterProjet(T t) throws SQLException;
    public void modifierProjet(T t) throws SQLException;
    public void supprimerProjet(int id) throws SQLException;
    public List<T> afficherProjet() throws SQLException;
}

