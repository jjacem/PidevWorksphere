package esprit.tn.services;

import esprit.tn.entities.Feedback;
import services.Iservice;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackService implements Iservice<Feedback> {

    private Connection conn;

    public FeedbackService() {
        conn = MyDatabase.getInstance().getConnection();
    }



    @Override
    public void ajouter(Feedback feedback) throws SQLException {

        String sql = "INSERT INTO feedback (message, rate, date_feedback, entretien_id) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, feedback.getMessage());
        pstmt.setInt(2, feedback.getRate());
        pstmt.setDate(3, new java.sql.Date(feedback.getDate_feedback().getTime()));
        pstmt.setInt(4, feedback.getEntretienId());
        pstmt.executeUpdate();
        System.out.println("Feedback ajouté avec succès!");

    }

    public int ajouterwithid(Feedback feedback) throws SQLException {
        String query = "INSERT INTO feedback (message, rate, date_feedback, entretien_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, feedback.getMessage());
            ps.setInt(2, feedback.getRate());
            ps.setDate(3, new java.sql.Date(feedback.getDate_feedback().getTime()));
            ps.setInt(4, feedback.getEntretienId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    @Override
    public void modifier(Feedback feedback) throws SQLException {

        String sql = "UPDATE feedback SET message=?, rate=?, date_feedback=?, WHERE id=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, feedback.getMessage());
        pstmt.setInt(2, feedback.getRate());
        pstmt.setDate(3, new java.sql.Date(feedback.getDate_feedback().getTime()));
        pstmt.setInt(5, feedback.getId());

        pstmt.executeUpdate();
        System.out.println("Feedback modifié avec succès!");

    }


    public void modifier1(Feedback feedback) throws SQLException {

        String sql = "UPDATE feedback SET message = ?, rate = ? WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, feedback.getMessage());
        pstmt.setInt(2, feedback.getRate());
        pstmt.setInt(3, feedback.getId());
        pstmt.executeUpdate();
        System.out.println("Feedback modifié avec succès!");

    }




    @Override
    public void supprimer(int id) throws SQLException {

        String sql = "DELETE FROM feedback WHERE id=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
        System.out.println("feedback supprimé avec succes ");

    }

    @Override
    public List<Feedback> afficher() throws SQLException {

        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT * FROM feedback";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Feedback feedback = new Feedback(
                    rs.getString("message") ,
                    rs.getInt("rate"),
                    rs.getInt("entretien_id") ,
                    rs.getDate("date_feedback")

            );

            feedbacks.add(feedback);
        }

        return feedbacks;
    }

    public Feedback getFeedbackById(int feedbackId) throws SQLException {
        String query = "SELECT * FROM feedback WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, feedbackId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Feedback(
                        rs.getInt("id"),
                        rs.getString("message"),
                        rs.getInt("rate"),
                        rs.getDate("date_feedback"),
                        rs.getInt("entretien_id")
                );
            }
        }
        return null;
    }





}
