package esprit.tn.controllers;

import esprit.tn.entities.Candidature;
import esprit.tn.entities.Entretien;
import esprit.tn.entities.OffreEmploi;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceCandidature;
import esprit.tn.services.ServiceOffre;
import esprit.tn.services.ServiceUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class VoirDetailsEntretienController {


    private final ServiceOffre offreService = new ServiceOffre();
    private final ServiceUser userService = new ServiceUser();
    private final ServiceCandidature candidatureService = new ServiceCandidature();
    @FXML
    private Label lblDatePublication;
    @FXML
    private Label lblTitre;
    @FXML
    private Label lblDateLimite;
    @FXML
    private Label lblPrenom;
    @FXML
    private Label lblNom;
    @FXML
    private Label lblLieuTravail;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblSalaire;
    @FXML
    private Label lblDescription;

    public void setEntretien(Entretien entretien) throws SQLException {
        if (entretien == null) {
            System.out.println("Erreur : Entretien null.");
            return;
        }


        if (entretien.getIdOffre() > 0) {
            OffreEmploi offre = offreService.getOffreById(entretien.getIdOffre());
            if (offre != null) {
                lblTitre.setText(offre.getTitre());
                lblDescription.setText(offre.getDescription() != null ? offre.getDescription().toString() : "Non défini");
                lblDatePublication.setText(offre.getDatePublication() != null ? offre.getDatePublication().toString() : "Non défini");
            } else {
                System.out.println("Aucune offre trouvée pour cet entretien.");
            }
        }

        if (entretien.getCandidatId() > 0) {
            User candidat = userService.findbyid(entretien.getCandidatId());
            if (candidat != null) {
                lblNom.setText(candidat.getNom());
                lblPrenom.setText(candidat.getPrenom());
                lblEmail.setText(candidat.getEmail());
            } else {
                System.out.println("Aucun candidat trouvé pour cet entretien.");
            }
        }

//        // Récupération et affichage des détails de la candidature
//        Candidature candidature = candidatureService.getCandidatureByIdEntretien(entretien.getId());
//        if (candidature != null) {
//            tf_candidature_lettreMotivation.setText(candidature.getLettreMotivation());
//            tf_candidature_cv.setText(candidature.getCvPath()); // Modifier selon la structure réelle
//        } else {
//            System.out.println("Aucune candidature trouvée pour cet entretien.");
//        }
//    }
    }

    public void retour(ActionEvent actionEvent) {

        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
