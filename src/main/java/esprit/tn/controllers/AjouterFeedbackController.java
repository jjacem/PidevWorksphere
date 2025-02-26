package esprit.tn.controllers;

import esprit.tn.entities.Feedback;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private Spinner<Integer> sp_rate;
    @FXML
    private TextArea tf_message;

    private FeedbackService feedbackService = new FeedbackService();
    private EntretienService entSer = new EntretienService();
    private int entretienId;

    public void setEntretienId(int entretienId) {
        this.entretienId = entretienId;
    }

    @FXML
    public void initialize() {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3);
        sp_rate.setValueFactory(valueFactory);

        sp_rate.getEditor().setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

        sp_rate.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                sp_rate.getValueFactory().setValue(1);
            }
        });
    }

    @FXML
    public void ajouterFeedback(ActionEvent actionEvent) {
        String message = tf_message.getText();
        Integer rate = sp_rate.getValue();

        String messagev = tf_message.getText();


        if (!validateInput(messagev, rate)) {
            return;
        }



        Date date = new Date(System.currentTimeMillis());

        Feedback feedback = new Feedback(message, rate, entretienId, date);

        try {
            int id = feedbackService.ajouterwithid(feedback);
            entSer.assignerFeedback(entretienId, id);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Feedback ajouté avec succès !");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretienbyemployeeId.fxml"));
            Parent entretienView = loader.load();

            AffichageEntretienbyemployeeId controller = loader.getController();
            controller.refreshDatas();

            Scene scene = btnAjouterFeedback.getScene();
            scene.setRoot(entretienView);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du feedback.");
        } catch (IOException e) {
            throw new RuntimeException(e);
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




}
