package service;

import java.sql.SQLException;
import java.util.List;

public interface IServiceEquipe  <T>{
    public void ajouterEquipe(T t) throws SQLException;
    public void modifierEquipe(T t) throws SQLException;
    public void supprimerEquipe(int id) throws SQLException;
    public List<T> afficherEquipe() throws SQLException;
}
