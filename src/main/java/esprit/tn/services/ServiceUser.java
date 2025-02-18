package esprit.tn.services;
import esprit.tn.entities.Role;
import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;
import esprit.tn.entities.Status;

import esprit.tn.utils.Emailsend;
import esprit.tn.utils.MyDatabase;
import org.mindrot.jbcrypt.BCrypt;
import esprit.tn.utils.JwtUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    public Role changetexttorole(String text) {
        if (text.equalsIgnoreCase("Employe")) {
            return Role.EMPLOYE;
        } else if (text.equalsIgnoreCase("Manager")) {
            return Role.MANAGER;
        } else if (text.equalsIgnoreCase("Candidat")) {
            return Role.CANDIDAT;
        } else if (text.equalsIgnoreCase("RH")) {
            return Role.RH;
        }
        return null; // Return null only if no match is found
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
        String hashpass= BCrypt.hashpw(user.getMdp(),BCrypt.gensalt());
        String req = "INSERT INTO User (nom, prenom, email, mdp, role, adresse, sexe, image_profil, status, salaire_attendu, poste, salaire, experience_travail, departement, competence, nombreProjet, budget, departement_géré, ans_experience, specialisation) " +
                "VALUES ('" + user.getNom() + "', '" + user.getPrenom() + "', '" + user.getEmail() + "', '" +
                hashpass + "', '" + user.getRole().name() + "', '" + user.getAdresse() + "', '" +
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
        String req = "UPDATE User SET nom=?, prenom=?, email=?, role=?, adresse=?, sexe=? WHERE id_user=?";
 String mdp=BCrypt.hashpw(user.getMdp(),BCrypt.gensalt());

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setString(1, user.getNom());
            statement.setString(2, user.getPrenom());
            statement.setString(3, user.getEmail());

            statement.setString(4, user.getRole().name());
            statement.setString(5, user.getAdresse());
            statement.setString(6, user.getSexe().name());
            statement.setInt(7, user.getIdUser());

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
public void changermdp(String mdp, int id){
        String hashpass=BCrypt.hashpw(mdp,BCrypt.gensalt());
        String req="update User set mdp=? where id_user=?";
        try {
            PreparedStatement statement=connection.prepareStatement(req);
            statement.setString(1,hashpass);
            statement.setInt(2,id);
            statement.executeUpdate();
            System.out.println("Mot de passe changé");
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

            Sexe      sexe = changetexttosexe(rs.getString("sexe"));
            Status status = changetexttostatus(rs.getString("status"));
            Role role = changetexttorole(rs.getString("role"));

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
            System.out.println("User trouvé: " + user);
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
        return -1;}
    public User login(String email, String mdp) throws SQLException {
        String req = "SELECT id_user, email, mdp, role FROM User WHERE email=?";
        PreparedStatement statement = connection.prepareStatement(req);
        statement.setString(1, email);
        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            String hashpass = rs.getString("mdp");

            if (BCrypt.checkpw(mdp, hashpass)) {
                int id = rs.getInt("id_user");
                String role = rs.getString("role");
                Role r=changetexttorole(role);
                String token = JwtUtil.generateToken(id, email, r);
                System.out.println("Login successful! Token: " + token);

                return this.findbyid(id);
            } else {
                System.out.println("Invalid credentials!");
            }
        } else {
            System.out.println("User not found!");
        }
        return null;
    }

    public void signup(User user) throws SQLException {
        String hashpass = BCrypt.hashpw(user.getMdp(), BCrypt.gensalt());
        user.setMdp(hashpass);
        ajouter(user);
    }
    public void sendmail (String email, String subject, String content) {
        Emailsend e = new Emailsend();
        e.sendEmail(email, subject, content);
    }
public void sendforgot(String email)throws SQLException{
        int i1= this.findidbyemail(email);
        User u1=this.findbyid(i1);
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    Random rand = new Random();
    StringBuilder password = new StringBuilder();

    for (int i = 0; i < 10; i++) {
        password.append(chars.charAt(rand.nextInt(chars.length())));
    }
       u1.setMdp(password.toString());
        this.modifier(u1);
        Emailsend e = new Emailsend();
        e.sendEmail(email, "Forgot password", "Your new password is: " + password.toString());
}
public List<User> chercherparnom(String query) throws SQLException {
        List<User> users = this.afficher();
    return users.stream()
            .filter(user -> user.getNom().toLowerCase().contains(query.toLowerCase()) )
            .collect(Collectors.toList());
}
    public List<User> chercherparmail(String query) throws SQLException {
        List<User> users = this.afficher();
        return users.stream()
                .filter(user -> user.getNom().toLowerCase().contains(query.toLowerCase()) )
                .collect(Collectors.toList());
    }
    public boolean changepassword(int id,String password){
        String hashpass=BCrypt.hashpw(password,BCrypt.gensalt());
        String req="update User set mdp=? where id_user=?";
        try {
            PreparedStatement statement=connection.prepareStatement(req);
            statement.setString(1,hashpass);
            statement.setInt(2,id);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


