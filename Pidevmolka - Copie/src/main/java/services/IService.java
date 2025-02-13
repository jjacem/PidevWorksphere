package services;

import entities.User;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    void ajouteruser (T t) throws SQLException;
     void modifier(T t) throws SQLException;
     void supprimer(int id) throws SQLException;
     User getuserByid(int id) throws SQLException;
     List<T> afficher() throws SQLException;
}
