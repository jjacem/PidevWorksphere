package esprit.tn.services;
import esprit.tn.entities.Role;
import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;
import esprit.tn.entities.Status;

import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ServiceUser implements IService<User> {
    Connection connection;

    public ServiceUser
            (){
        connection= MyDatabase.getInstance().getConnection();

    }
    public Sexe changetexttosexe(String text) {
        if (text == "FEMME") {
            return Sexe.FEMME;
        } else {
            return Sexe.HOMME;
        }
    }
    public Role changetexttorole(String text){
        if (text == "EMPLOYE") {
            return Role.EMPLOYE;
        } else if (text == "MANAGER") {
            return Role.MANAGER;
        } else if (text == "CANDIDAT") {
            return Role.CANDIDAT;
        } else {
            return Role.RH;
        }
    }
public Status changetexttostatus(String text){
        if (text == "Candidature") {
            return Status.Candidature;
        } else if (text == "Entretien") {
            return Status.Entretien;
        } else if (text == "programmé") {
            return Status.programmé;
        } else if(text == "Embauché"){
            return Status.Embauché;
        }else {
            return Status.Refusé;
        }
    }




    public int ajouterwithid(User user) throws SQLException {
        String req = "INSERT INTO User (nom, prenom, email, mdp, role, adresse, sexe) " +
                "VALUES ('" + user.getNom() + "', '" + user.getPrenom() + "', '" + user.getEmail() + "', '" +
                user.getMdp() + "', '" + user.getRole() + "', '" + user.getAdresse() + "', '" +
                user.getSexe() + "')";

        Statement statement = connection.createStatement();
        int rowsAffected = statement.executeUpdate(req, Statement.RETURN_GENERATED_KEYS);

        if (rowsAffected > 0) {

            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {

                return resultSet.getInt(1);
            }
        }

        return -1;
    }
    @Override
        public void ajouter(User user) throws SQLException {
        String req = "INSERT INTO User (nom, prenom, email, mdp, role, adresse, sexe, image_profil, status, salaire_attendu, poste, salaire, experience_travail, departement, competence, nombreProjet, budget, departement_géré, ans_experience, specialisation) " +
                "VALUES ('" + user.getNom() + "', '" + user.getPrenom() + "', '" + user.getEmail() + "', '" +
                user.getMdp() + "', '" + user.getRole().name() + "', '" + user.getAdresse() + "', '" +
                user.getSexe().name() + "', '" + user.getImageProfil() + "', " +
                (user.getStatus() != null ? "'" + user.getStatus().name() + "'" : "NULL") + ", " +
                (user.getSalaireAttendu() != null ? user.getSalaireAttendu() : "NULL") + ", " +
                (user.getPoste() != null ? "'" + user.getPoste() + "'" : "NULL") + ", " +
                (user.getSalaire() != null ? user.getSalaire() : "NULL") + ", " +
                (user.getExperienceTravail() != 0 ? user.getExperienceTravail() : "NULL") + ", " +
                (user.getDepartement() != null ? "'" + user.getDepartement() + "'" : "NULL") + ", " +
                (user.getCompetence() != null ? "'" + user.getCompetence() + "'" : "NULL") + ", " +
                (user.getNombreProjet() != 0 ? user.getNombreProjet() : "NULL") + ", " +
                (user.getBudget() != null ? user.getBudget() : "NULL") + ", " +
                (user.getDepartementGere() != null ? "'" + user.getDepartementGere() + "'" : "NULL") + ", " +
                (user.getAnsExperience() != 0 ? user.getAnsExperience() : "NULL") + ", " +
                (user.getSpecialisation() != null ? "'" + user.getSpecialisation() + "'" : "NULL") + ")";


        Statement statement=connection.createStatement();
            statement.executeUpdate(req);
            System.out.println("user ajoute");
        }

    @Override
    public void modifier(User user) throws SQLException {
        String req = "UPDATE User SET nom=?, prenom=?, email=?, mdp=?, role=?, adresse=?, sexe=? WHERE id_user=?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setString(1, user.getNom());
            statement.setString(2, user.getPrenom());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getMdp());
            statement.setString(5, user.getRole().name());
            statement.setString(6, user.getAdresse());
            statement.setString(7, user.getSexe().name());
            statement.setInt(8, user.getIdUser());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("user modifiée avec succès.");
            } else {
                System.out.println("Aucune user trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
String req="delete from User where id_user=?";
PreparedStatement statement= connection.prepareStatement(req);
statement.setInt(1,id);
statement.executeUpdate();
        System.out.println("User supprimee");
    }

    @Override
    public List<User> afficher() throws SQLException {
        List<User> Users= new ArrayList<>();
        String req="select * from User";
        Statement statement= connection.createStatement();

        ResultSet rs= statement.executeQuery(req);
        while (rs.next()){
Role role = changetexttorole(rs.getString("role"));
      Sexe      sexe = changetexttosexe(rs.getString("sexe"));
      Status status = changetexttostatus(rs.getString("status"));

                    User user = new User(
                    rs.getString("nom"),
                    role,
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mdp"),
                    rs.getString("adresse"),
                    sexe,
                    rs.getString("image_profil"),
                    status,
                    rs.getDouble("salaire_attendu"),
                    rs.getString("poste"),
                    rs.getDouble("salaire"),
                    rs.getInt("experience_travail"),
                    rs.getString("departement"),
                    rs.getString("competence"),
                    rs.getInt("nombreProjet"),
                    rs.getDouble("budget"),
                    rs.getString("departement_géré"),
                    rs.getInt("ans_experience"),
                    rs.getString("specialisation")
            );
user.setIdUser(rs.getInt("id_user"));


            Users.add(user);
        }


        return Users;
    }
public User extractuser(Object o){
        if(o instanceof User){
            return (User) o;
        }
        return null;
}
    public User findbyid(int id) throws SQLException {
        String req = "SELECT * FROM User WHERE id_user=?";
        PreparedStatement statement = connection.prepareStatement(req);
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            Role role = changetexttorole(rs.getString("role"));
            Sexe      sexe = changetexttosexe(rs.getString("sexe"));
            Status status = changetexttostatus(rs.getString("status"));

            User user = new User(
                    rs.getString("nom"),
                    role,
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mdp"),
                    rs.getString("adresse"),
                    sexe,
                    rs.getString("image_profil"),
                    status,
                    rs.getDouble("salaire_attendu"),
                    rs.getString("poste"),
                    rs.getDouble("salaire"),
                    rs.getInt("experience_travail"),
                    rs.getString("departement"),
                    rs.getString("competence"),
                    rs.getInt("nombreProjet"),
                    rs.getDouble("budget"),
                    rs.getString("departement_géré"),
                    rs.getInt("ans_experience"),
                    rs.getString("specialisation")
            );

            user.setIdUser(rs.getInt("id_user"));
            return user;
        }

        return null;
}
public int findidbyemail(String email) throws SQLException {
        String req="select id_user from User where email=?";
        PreparedStatement statement= connection.prepareStatement(req);
        statement.setString(1,email);
        ResultSet rs= statement.executeQuery();
        while (rs.next()){
           return (rs.getInt("id_user"));
        }
        return -1;}}
