package esprit.tn.controllers;

import esprit.tn.entities.Meetings;
import esprit.tn.services.JitsiMeetService;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class JitsiMeetController {
    @FXML
    private TextField roomNameField;
    @FXML
    private TextField userNameField;
    @FXML
    private CheckBox muteCheckBox;
    private final JitsiMeetService jitsiMeetService = new JitsiMeetService();
    @FXML
    public void startMeeting(int reservationId) { // Ajout de l'ID de réservation
        String roomName = roomNameField.getText().trim();
        String displayName = userNameField.getText().trim();
        boolean startMuted = muteCheckBox.isSelected();

        if (!roomName.isEmpty() && !displayName.isEmpty()) {
            String meetingUrl = jitsiMeetService.generateMeetingUrl(roomName, displayName, startMuted);
            openInBrowser(meetingUrl);

            // Enregistrer le meeting dans la base avec reservationId
            Meetings meeting = new Meetings(0, roomName, meetingUrl, reservationId);
            jitsiMeetService.saveMeeting(meeting);
        }
    }

    private void openInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture du navigateur : " + e.getMessage());
        }
    }

    @FXML
    public void rejoindreMeeting(int reservationId) {
        String meetingUrl = jitsiMeetService.getMeetingUrlByReservationId(reservationId);
        if (meetingUrl != null && !meetingUrl.isEmpty()) {
            openInBrowser(meetingUrl);
        } else {
            System.err.println("Aucun meeting enregistré pour la réservation ID : " + reservationId);
        }
    }
}
