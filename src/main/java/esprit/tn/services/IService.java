package esprit.tn.services;

import esprit.tn.entities.User;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    public void ajouterUser(T t) throws SQLException;
    User getuserByid(int id) throws SQLException;
    List<T> getAllusers() throws SQLException;
}
