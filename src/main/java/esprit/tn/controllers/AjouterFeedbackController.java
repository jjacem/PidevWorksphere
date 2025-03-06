package esprit.tn.controllers;

import esprit.tn.entities.Feedback;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import esprit.tn.services.EntretienService;
import esprit.tn.services.FeedbackService;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

public class AjouterFeedbackController {
    @FXML
    private Button btnAjouterFeedback;
    @FXML
    private TextArea tf_message;

    private FeedbackService feedbackService = new FeedbackService();
    private EntretienService entSer = new EntretienService();
    private int entretienId;

    @FXML
    private RadioButton q1_r1, q1_r2, q1_r3;
    @FXML
    private RadioButton q2_r1, q2_r2, q2_r3;
    @FXML
    private RadioButton q3_r1, q3_r2, q3_r3;
    @FXML
    private RadioButton q4_r1, q4_r2, q4_r3;

    @FXML
    private Label lblScore;
    @FXML

    public void setEntretienId(int entretienId) {
        this.entretienId = entretienId;
    }

    @FXML
    public void initialize() {

    }

    @FXML
    public void ajouterFeedback(ActionEvent actionEvent) {
        String message = tf_message.getText();
        float rate = calculateScore();

        if (!validateInput(message, rate)) {
            return;
        }

        Date date = new Date(System.currentTimeMillis());
        Feedback feedback = new Feedback(message, rate, entretienId, date);

        try {
            int id = feedbackService.ajouterwithid(feedback);
            entSer.assignerFeedback(entretienId, id);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Feedback ajouté avec succès !");

            refreshAffichageEntretien();


        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du feedback.");
        }
    }

    @FXML
    public void fermerFenetre(ActionEvent actionEvent) {
        ((Button) actionEvent.getSource()).getScene().getWindow().hide();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }


    private boolean validateInput(String message, float rate) {
        if (message.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", "Le message ne peut pas être vide.");
            return false;
        }
        if (message.length() < 10) {
            showAlert(Alert.AlertType.WARNING, "Message trop court", "Le message doit contenir au moins 10 caractères.");
            return false;
        }
        if (  rate < 0 || rate > 5) {
            showAlert(Alert.AlertType.WARNING, "Note invalide", "La note doit être comprise entre 1 et 5.");
            return false;
        }

        if (!(q1_r1.isSelected() || q1_r2.isSelected() || q1_r3.isSelected())) {
            showAlert(Alert.AlertType.WARNING, "Réponse manquante", "Veuillez sélectionner une réponse pour la question 1.");
            return false;
        }
        if (!(q2_r1.isSelected() || q2_r2.isSelected() || q2_r3.isSelected())) {
            showAlert(Alert.AlertType.WARNING, "Réponse manquante", "Veuillez sélectionner une réponse pour la question 2.");
            return false;
        }
        if (!(q3_r1.isSelected() || q3_r2.isSelected() || q3_r3.isSelected())) {
            showAlert(Alert.AlertType.WARNING, "Réponse manquante", "Veuillez sélectionner une réponse pour la question 3.");
            return false;
        }
        if (!(q4_r1.isSelected() || q4_r2.isSelected() || q4_r3.isSelected())) {
            showAlert(Alert.AlertType.WARNING, "Réponse manquante", "Veuillez sélectionner une réponse pour la question 4.");
            return false;
        }



        return true;
    }


    private float calculateScore() {
        float score = 0;

        if (q1_r1.isSelected()) {
            score += 0;
        } else if (q1_r2.isSelected()) {
            score += 0.7;
        } else if (q1_r3.isSelected()) {
            score += 1.25;
        }

        if (q2_r1.isSelected()) {
            score += 0;
        } else if (q2_r2.isSelected()) {
            score += 0.7;
        } else if (q2_r3.isSelected()) {
            score +=1.25;
        }

        if (q3_r1.isSelected()) {
            score += 0;
        } else if (q3_r2.isSelected()) {
            score += 0.7;
        } else if (q3_r3.isSelected()) {
            score += 1.25;
        }

        if (q4_r1.isSelected()) {
            score += 0;
        } else if (q4_r2.isSelected()) {
            score += 0.7;
        } else if (q4_r3.isSelected()) {
            score += 1.25;
        }

        return score;
    }


    private void refreshAffichageEntretien() {
        try {
            Stage stage = (Stage) btnAjouterFeedback.getScene().getWindow();
            stage.close();
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretienbyemployeeId.fxml"));
//            Parent root = loader.load();
//            AffichageEntretineController controller = loader.getController();
//            controller.initialize();
//            Stage stage = (Stage) cb_type_entretien.getScene().getWindow();
//            stage.getScene().setRoot(root);
//            stage.setTitle("Liste des Entretiens");
//            stage.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }







}
