package services;

import entities.Feedback;
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

    @Override
    public void modifier(Feedback feedback) throws SQLException {

        String sql = "UPDATE feedback SET message=?, rate=?, date_feedback=?, entretien_id=? WHERE id=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, feedback.getMessage());
        pstmt.setInt(2, feedback.getRate());
        pstmt.setDate(3, new java.sql.Date(feedback.getDate_feedback().getTime()));
        pstmt.setInt(4, feedback.getEntretienId());
        pstmt.setInt(5, feedback.getId());

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
}
