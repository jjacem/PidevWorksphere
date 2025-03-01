package esprit.tn.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import esprit.tn.entities.Equipe;
import esprit.tn.services.ServiceEquipe;

import java.sql.SQLException;
import java.util.List;

public class StatistiquesEquipeController {

    @FXML
    private BarChart<String, Number> membresChart;

    @FXML
    private PieChart projetsChart;

    @FXML
    private VBox tempsMoyenContainer; // Remplace le TableView par une VBox

    private ServiceEquipe serviceEquipe;

    public StatistiquesEquipeController() {
        serviceEquipe = new ServiceEquipe();
    }

    @FXML
    public void initialize() {
        try {
            List<Equipe> equipes = serviceEquipe.afficherEquipe();
            afficherMembresParEquipe(equipes);
            afficherProjetsParEquipe(equipes);
            afficherTempsMoyenProjets(equipes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherMembresParEquipe(List<Equipe> equipes) {
        membresChart.getData().clear(); // Effacer les données existantes
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Membres");

        for (Equipe equipe : equipes) {
            int nombreMembres = equipe.getEmployes() != null ? equipe.getEmployes().size() : 0;
            series.getData().add(new XYChart.Data<>(equipe.getNomEquipe(), nombreMembres));
        }

        membresChart.getData().add(series);
    }

    private void afficherProjetsParEquipe(List<Equipe> equipes) {
        projetsChart.getData().clear(); // Effacer les données existantes
        for (Equipe equipe : equipes) {
            PieChart.Data slice = new PieChart.Data(equipe.getNomEquipe(), equipe.getNbrProjet());
            projetsChart.getData().add(slice);
        }
    }

    private void afficherTempsMoyenProjets(List<Equipe> equipes) {
        tempsMoyenContainer.getChildren().clear(); // Effacer les données existantes

        for (Equipe equipe : equipes) {
            double tempsMoyen = calculerTempsMoyenProjets(equipe);
            Label label = new Label(equipe.getNomEquipe() + " : " + String.format("%.2f", tempsMoyen) + " jours");
            tempsMoyenContainer.getChildren().add(label); // Ajouter un Label pour chaque équipe
        }
    }

    private double calculerTempsMoyenProjets(Equipe equipe) {
        if (equipe.getProjets() != null && !equipe.getProjets().isEmpty()) {
            return equipe.getProjets().stream()
                    .mapToDouble(projet -> {
                        long diffInMillies = Math.abs(projet.getDeadline().getTime() - projet.getDatecréation().getTime());
                        return (double) diffInMillies / (1000 * 60 * 60 * 24); // Conversion en jours
                    })
                    .average()
                    .orElse(0);
        }
        return 0;
    }
}