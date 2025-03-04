package esprit.tn.controllers;

import esprit.tn.services.ServiceEquipe;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class AfficherStatistiqueController {

    @FXML
    private WebView webView;

    private ServiceEquipe serviceEquipe = new ServiceEquipe();

    @FXML
    public void initialize() {
        try {
            // Récupérer les données des équipes au format JSON
            String jsonData = serviceEquipe.getEquipeStatsJson();
            // Générer le contenu HTML/JavaScript pour les graphiques
            String htmlContent = generateHtmlContent(jsonData);
            // Charger le contenu dans le WebView
            WebEngine webEngine = webView.getEngine();
            webEngine.loadContent(htmlContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateHtmlContent(String jsonData) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "        google.charts.load('current', {'packages':['corechart']});\n" +
                "        google.charts.setOnLoadCallback(drawCharts);\n" +
                "\n" +
                "        function drawCharts() {\n" +
                "            var equipes = " + jsonData + ";\n" +
                "\n" +
                "            // Données pour la répartition des employés (Pie Chart)\n" +
                "            var dataEmployes = new google.visualization.DataTable();\n" +
                "            dataEmployes.addColumn('string', 'Equipe');\n" +
                "            dataEmployes.addColumn('number', 'Nombre d\\'employés');\n" +
                "            equipes.forEach(function(equipe) {\n" +
                "                dataEmployes.addRow([equipe.nomEquipe, equipe.employes.length]);\n" +
                "            });\n" +
                "\n" +
                "            // Options pour le graphique des employés\n" +
                "            var optionsEmployes = {\n" +
                "                title: 'Répartition des employés par équipe',\n" +
                "                pieHole: 0.4,\n" +
                "            };\n" +
                "\n" +
                "            // Dessiner le graphique des employés\n" +
                "            var chartEmployes = new google.visualization.PieChart(document.getElementById('chart_employes'));\n" +
                "            chartEmployes.draw(dataEmployes, optionsEmployes);\n" +
                "\n" +
                "            // Données pour la répartition des projets (Line Chart)\n" +
                "            var dataProjets = new google.visualization.DataTable();\n" +
                "            dataProjets.addColumn('string', 'Equipe');\n" +
                "            dataProjets.addColumn('number', 'Nombre de projets');\n" +
                "            equipes.forEach(function(equipe) {\n" +
                "                dataProjets.addRow([equipe.nomEquipe, equipe.nbrProjet]);\n" +
                "            });\n" +
                "\n" +
                "\n" +
                "\n" +
                "            // Options pour le graphique des projets\n" +
                "            var optionsProjets = {\n" +
                "                title: 'Répartition des projets par équipe',\n" +
                "                curveType: 'function', // Courbe lissée\n" +
                "                legend: { position: 'bottom' },\n" +
                "                hAxis: { title: 'Équipes' },\n" +
                "                vAxis: { title: 'Nombre de projets' }\n" +
                "            };\n" +
                "\n" +
                "\n" +

                "            // Dessiner le graphique des projets\n" +
                "            var chartProjets = new google.visualization.LineChart(document.getElementById('chart_projets'));\n" +
                "            chartProjets.draw(dataProjets, optionsProjets);\n" +
                "        }\n" +
                "    </script>\n" +
                "    <style>\n" +
                "        .chart {\n" +
                "            width: 100%;\n" +
                "            height: 500px;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"chart_employes\" class=\"chart\"></div>\n" +
                "    <div id=\"chart_projets\" class=\"chart\"></div>\n" +
                "</body>\n" +
                "</html>";
    }
}