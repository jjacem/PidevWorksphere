package esprit.tn.services;

import esprit.tn.entities.Candidat;
import esprit.tn.entities.Sexe;
import esprit.tn.entities.Status;
import esprit.tn.entities.User;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCandidat implements IService<Candidat> {
    private Connection connection;
private ServiceUser usersevice=new ServiceUser();
    public ServiceCandidat() {
        connection = MyDatabase.getInstance().getConnection();
    }





    @Override
    public void ajouter(Candidat candidat) throws SQLException {
    User u1=usersevice.extractuser(candidat);
   int i= usersevice.ajouterwithid(u1);
   if (i>-1){ String req = "INSERT INTO Candidat (id_user, status, salaire_attendu) " +
           "VALUES ('" + i + "', '" + candidat.getStatus() + "', '" + candidat.getSalaireAttendu() + "')";
       Statement statement=connection.createStatement();
       statement.executeUpdate(req);


       System.out.println("Candidat ajouté avec succès.");}


    }

    @Override
    public void modifier(Candidat candidat) throws SQLException {
        String req = "UPDATE Candidat SET  status=?, salaire_attendu=? WHERE id_candidat=?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {

            statement.setString(1, candidat.getStatus().name());
            statement.setDouble(2, candidat.getSalaireAttendu());
            statement.setInt(3, candidat.getIdCandidat());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Candidat modifié avec succès.");
            } else {
                System.out.println("Aucun candidat trouvé avec cet ID.");
            }
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM Candidat WHERE id_candidat=?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Candidat supprimé avec succès.");
            } else {
                System.out.println("Aucun candidat trouvé avec cet ID.");
            }
        }
    }

    @Override
    public List<Candidat> afficher() throws SQLException {
        List<Candidat> candidats = new ArrayList<>();
        String req = "SELECT * FROM Candidat";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {

            while (rs.next()) {
                User u1= usersevice.findbyid(rs.getInt("id_user"));
               u1.setIdUser(rs.getInt("id_user"));
               Status st=   changestatus(rs.getString("status"));
                Candidat candidat = new Candidat(
                       u1,
                        st,
                        rs.getDouble("salaire_attendu")
                );

                candidat.setIdCandidat(rs.getInt("id_candidat"));
                candidats.add(candidat);
            }
        }

        return candidats;
    }
    public Status changestatus(String text) {
        switch (text) {
            case "Candidature":
                return Status.Candidature;
            case "Accepte":
                return Status.Embauché;
            case "Refuse":
                return Status.Refusé;
            case "programmé":
                return Status.programmé;
            case "Entretien":
                return Status.Entretien;
        }

        return null;
    }
    public String chnagestatustotext(Status status){
        switch (status){
            case Candidature:
                return "Candidature";
            case Embauché:
                return "Embauché";
            case Refusé:
                return "Refusé";
                case programmé:
                    return "programmé";
            case Entretien:
                return "Entretien";
        }
        return null;
    }
    public int findidbyemail(String email){
        String req = "SELECT e.id_candidat FROM Employe e JOIN User u ON e.id_user = u.id_user WHERE u.email = ?;"
                ;
        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_candidat");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
