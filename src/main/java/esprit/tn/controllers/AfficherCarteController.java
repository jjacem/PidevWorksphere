
package esprit.tn.controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class AfficherCarteController {

    @FXML
    private WebView webViewMap;

    public void initData(String coordinates) {
        if (webViewMap == null) {
            System.out.println("Erreur : webViewMap est null");
            return;
        }

        WebEngine webEngine = webViewMap.getEngine();
        String htmlMap = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='utf-8' />" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.7.1/dist/leaflet.css'/>" +
                "<script src='https://unpkg.com/leaflet@1.7.1/dist/leaflet.js'></script>" +
                "</head>" +
                "<body style='margin:0;'>" +
                "<div id='map' style='width: 100%; height: 100vh;'></div>" +
                "<script>" +
                "var map = L.map('map').setView([" + coordinates + "], 13);" +
                "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
                "    attribution: '© OpenStreetMap contributors'" +
                "}).addTo(map);" +
                "L.marker([" + coordinates + "]).addTo(map)" +
                "    .bindPopup('Lieu de l\\'événement')" +
                "    .openPopup();" +
                "</script>" +
                "</body>" +
                "</html>";

        webEngine.loadContent(htmlMap);
    }
}