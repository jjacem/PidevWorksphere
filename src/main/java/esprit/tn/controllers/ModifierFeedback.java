package esprit.tn.controllers;

import esprit.tn.entities.Feedback;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import esprit.tn.services.FeedbackService;

import java.io.IOException;
import java.sql.SQLException;

public class ModifierFeedback {
    @FXML
    private Button btnModifierFeedback;
    @FXML
    private Spinner sp_rate;
    @FXML
    private TextArea tf_message;


    private int feedbackId;
    private FeedbackService feedbackService = new FeedbackService();
    private int currentFeedbackId;

//    @FXML
//    public void fermerFenetre(ActionEvent actionEvent) {
//
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
//            Parent voirFeedbackView = loader.load();
//
//            voirFeedbackController controller = loader.getController();
//            controller.chargerFeedback(currentFeedbackId);
//            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
//
//            stage.setScene(new Scene(voirFeedbackView));
//            stage.show();
//
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    @FXML
    public void fermerFenetre(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretienbyemployeeId.fxml"));
            Parent voirFeedbackView = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(voirFeedbackView));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void modifierFeedback(ActionEvent actionEvent) {

        try {
            String message = tf_message.getText();
            int rate = (int) sp_rate.getValue();


            if (!validateInput(message, rate)) {
                return;
            }

            Feedback feedback = feedbackService.getFeedbackById(feedbackId);

            feedback.setMessage(message);
            feedback.setRate(rate);

            feedbackService.modifier1(feedback);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Feedback Modifié");
            alert.setHeaderText("Le feedback a été mis à jour avec succès !");
            alert.showAndWait();
            fermerFenetre(actionEvent);
        } catch (SQLException e) {
            e.printStackTrace();
        }



    }



    public void chargerFeedback(int feedbackId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/voirFeedback.fxml"));
            Parent voirFeedbackView = loader.load();

            voirFeedbackController controller = loader.getController();
            controller.chargerFeedback(currentFeedbackId);
            this.feedbackId = feedbackId;
            Feedback feedback = feedbackService.getFeedbackById(feedbackId);

            if (feedback != null) {
                tf_message.setText(feedback.getMessage());
                sp_rate.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, feedback.getRate())); // Assuming rating is between 1-5
            }


//            tf_message.getScene().getWindow().hide();
//
//            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
//
//            stage.setScene(new Scene(voirFeedbackView));
//            stage.show();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }




    private boolean validateInput(String message, Integer rate) {
        if (message.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", "Le message ne peut pas être vide.");
            return false;
        }
        if (message.length() < 10) {
            showAlert(Alert.AlertType.WARNING, "Message trop court", "Le message doit contenir au moins 10 caractères.");
            return false;
        }
        if (rate == null || rate < 1 || rate > 5) {
            showAlert(Alert.AlertType.WARNING, "Note invalide", "La note doit être comprise entre 1 et 5.");
            return false;
        }
        return true;
    }



    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }


















}
