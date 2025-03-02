package esprit.tn.controllers;

import esprit.tn.services.JitsiMeetService;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.awt.*;
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
    public void startMeeting() {
        String roomName = roomNameField.getText().trim();
        String displayName = userNameField.getText().trim();
        boolean startMuted = muteCheckBox.isSelected();

        if (!roomName.isEmpty() && !displayName.isEmpty()) {
            String meetingUrl = jitsiMeetService.generateMeetingUrl(roomName, displayName, startMuted);
            openInBrowser(meetingUrl);
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
}
