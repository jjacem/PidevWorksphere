package esprit.tn.controllers;

import esprit.tn.entities.Entretien;
import esprit.tn.services.EntretienService;
import esprit.tn.services.GoogleCalendarService;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class AffichageGoogleCalendar {
    @FXML
    private Button btn_retour;
    @javafx.fxml.FXML
    private ListView lv_entretien;

    @FXML
    private WebView webView;


    private EntretienService entretienService = new EntretienService();
    private GoogleCalendarService googleCalendarService = new GoogleCalendarService();

    @FXML
    public void initialize() {
        afficherEntretiensDansGoogleCalendar();
    }

    /**
     * Affiche les entretiens dans Google Calendar.
     */
    private void afficherEntretiensDansGoogleCalendar() {
        try {
            List<Entretien> entretiens = entretienService.getEntretiensByEmployeId(SessionManager.extractuserfromsession().getIdUser());

            for (Entretien entretien : entretiens) {
                googleCalendarService.ajouterEntretienDansGoogleCalendar(entretien);
            }

//            webView.getEngine().load("https://calendar.google.com/calendar/embed?src=hbaieb.houssem999@gmail.com");
            openGoogleCalendarInBrowser("https://calendar.google.com/calendar/embed?src=hbaieb.houssem999@gmail.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openGoogleCalendarInBrowser(String url) {
        try {
            URI uri = new URI(url);
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(uri);  // Opens the URL in the default browser
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Redirige vers l'interface précédente.
     */
    @FXML
    public void retour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardEmploye.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btn_retour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Entretiens");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
