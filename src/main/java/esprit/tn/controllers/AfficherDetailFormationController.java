package esprit.tn.controllers;

import esprit.tn.entities.Formation;
import esprit.tn.services.ServiceFormation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class AfficherDetailFormationController {
    @FXML
    private Label DescID;
    @FXML
    private Label hdID;
    @FXML
    private Label hfID;
    @FXML
    private Label titreID;
    @FXML
    private Label tfID;
    @FXML
    private Label nbplaceID;
    @FXML
    private Label DateID;

    private Formation formation;

    public void setFormation(Formation id) {
        ServiceFormation serviceFormation = new ServiceFormation();
        try {
            // Appeler la méthode du service pour récupérer les détails de la formation
            formation = serviceFormation.getFormationById(id.getId_f());

            // Mettre à jour l'UI avec les informations de la formation
            if (formation != null) {
                titreID.setText(formation.getTitre());
                DescID.setText(formation.getDescription());
                hdID.setText(formation.getHeure_debut().toString());
                hfID.setText(formation.getHeure_fin().toString());
                tfID.setText(formation.getType().toString());
                nbplaceID.setText(String.valueOf(formation.getNb_place()));
                DateID.setText(formation.getDate().toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

