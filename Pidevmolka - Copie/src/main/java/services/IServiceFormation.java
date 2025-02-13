package services;

import entities.Formation;

import java.sql.SQLException;
import java.util.List;

public interface IServiceFormation <T>  {
    void ajouterFormation(T t) throws SQLException;
    void modifierFormation(T t) throws SQLException;
    void supprimeFormation(T t) throws SQLException;
    List<T> getListFormation() throws SQLException;


}
