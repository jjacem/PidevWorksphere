package esprit.tn.services;

import esprit.tn.entities.Formation;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IServiceFormation <T>  {
    void ajouterFormation(T t) throws SQLException;
    void modifierFormation(T t) throws SQLException;
    void supprimeFormation(T t) throws SQLException;
    List<T> getListFormation() throws SQLException;
    Formation getFormationById(int id) throws SQLException;
}
