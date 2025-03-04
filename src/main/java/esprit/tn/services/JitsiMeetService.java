package esprit.tn.services;

import esprit.tn.entities.Meetings;
import esprit.tn.utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JitsiMeetService {
    Connection connection;
    private static final String JITSI_MEET_URL = "https://meet.jit.si/";
    public JitsiMeetService() {
        connection= MyDatabase.getInstance().getConnection();
    }

    /**
     * Génère une URL de réunion avec paramètres personnalisés.
     * @param roomName Nom de la salle
     * @param displayName Nom affiché dans la réunion
     * @param startMuted Si le micro doit être coupé au départ
     * @return URL complète de la réunion Jitsi
     */
    public String generateMeetingUrl(String roomName, String displayName, boolean startMuted) {
        return JITSI_MEET_URL + roomName +
                "#userInfo.displayName=" + displayName.replace(" ", "%20") +
                "&config.startWithAudioMuted=" + startMuted;
    }

    public void saveMeeting(Meetings meeting) {
        String query = "INSERT INTO meetings (room_name, meeting_url, id_reservation ) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, meeting.getRoom_name());
            preparedStatement.setString(2, meeting.getMeeting_url());
            preparedStatement.setInt(3, meeting.getReservation_id()); // Ajout de l'ID de réservation
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'enregistrement du meeting : " + e.getMessage());
        }
    }

    public String getMeetingUrlByReservationId(int reservationId) {
        String query = "SELECT meeting_url FROM meetings WHERE id_reservation = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reservationId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("meeting_url");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération du meeting : " + e.getMessage());
        }
        return null; // Aucun meeting trouvé
    }
}
