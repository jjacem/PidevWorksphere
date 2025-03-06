
package esprit.tn.controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class AfficherCarteController {

    @FXML
    //WebView est un composant de JavaFX qui permet d'afficher du contenu
    // web directement dans une application Java. Il agit comme un navigateur
    // intégré dans l'interface graphique de l'application, ce qui permet d'afficher
    // des pages web, des documents HTML, des vidéos, des cartes interactives, ou toute autre ressource web,
    // sans avoir à ouvrir un navigateur externe.
    private WebView webViewMap;
//initData(String coordinates) est une méthode publique qui prend un paramètre coordinates
// (une chaîne de caractères représentant les coordonnées géographiques sous forme de latitude et longitude).
    public void initData(String coordinates) {
        if (webViewMap == null) {
            //Avant de continuer, la méthode vérifie si webViewMap est nul. Si c'est le cas,
            // un message d'erreur est affiché, et la méthode retourne immédiatement.
            System.out.println("Erreur : webViewMap est null");
            return;
        }
        //récupère le moteur WebEngine associé au WebView,
        // qui est responsable du rendu du contenu HTML dans le composant WebView
        WebEngine webEngine = webViewMap.getEngine();
        //Le HTML construit la structure de la page,
        // incluant les métadonnées et les liens vers les fichiers CSS et JS de Leaflet
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
        //charge le contenu HTML dynamique dans webview
        webEngine.loadContent(htmlMap);
    }
}