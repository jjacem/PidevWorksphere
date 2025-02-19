package controllers;

import entities.Feedback;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;
import services.EntretienService;
import services.FeedbackService;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

public class AjouterFeedbackController {
    @FXML
    private Button btnAjouterFeedback;
    @FXML
    private Spinner<Integer> sp_rate; // ✅ Ensure type safety with <Integer>
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

        if (message.isEmpty() || rate == null || rate == 0) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", "Veuillez remplir tous les champs !");
            return;
        }

        Date date = new Date(System.currentTimeMillis()); // ✅ Use current date

        Feedback feedback = new Feedback(message, rate, entretienId, date);

        try {
            int id = feedbackService.ajouterwithid(feedback);
            entSer.assignerFeedback(entretienId, id);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Feedback ajouté avec succès !");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
            Parent entretienView = loader.load();

            AffichageEntretineController controller = loader.getController();
            controller.refreshDatas();

            // Set the root to AffichageEntretien.fxml
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






}
