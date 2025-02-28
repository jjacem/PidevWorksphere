package esprit.tn.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import esprit.tn.entities.Equipe;
import esprit.tn.services.ServiceEquipe;

import java.sql.SQLException;
import java.util.List;

public class StatistiquesEquipesController {

    @FXML
    private PieChart pieChartMembres;

    @FXML
    private BarChart<String, Number> barChartProjets;

    private ServiceEquipe serviceEquipe;

    public StatistiquesEquipesController() {
        serviceEquipe = new ServiceEquipe();
    }

    @FXML
    public void initialize() {
        try {
            // Récupérer la liste des équipes
            List<Equipe> equipes = serviceEquipe.afficherEquipe();

            // Mettre à jour les graphiques
            updatePieChartMembres(equipes);
            //updateBarChartProjets(equipes);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePieChartMembres(List<Equipe> equipes) {
        pieChartMembres.getData().clear(); // Vider les données existantes

        for (Equipe equipe : equipes) {
            int nombreMembres = equipe.getEmployes() != null ? equipe.getEmployes().size() : 0;
            PieChart.Data slice = new PieChart.Data(equipe.getNomEquipe(), nombreMembres);
            pieChartMembres.getData().add(slice);
        }
    }

    /*private void updateBarChartProjets(List<Equipe> equipes) {
        barChartProjets.getData().clear(); // Vider les données existantes

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre de projets");

        for (Equipe equipe : equipes) {
            int nombreProjets = equipe.getNombreProjets(); // Utilisez le champ nombreProjets
            series.getData().add(new XYChart.Data<>(equipe.getNomEquipe(), nombreProjets));
        }

        barChartProjets.getData().add(series);
    }*/
}