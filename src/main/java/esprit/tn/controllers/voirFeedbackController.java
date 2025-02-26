package esprit.tn.controllers;

import esprit.tn.entities.Feedback;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import esprit.tn.services.EntretienService;
import esprit.tn.services.FeedbackService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class voirFeedbackController {


    private int currentFeedbackId;


    private FeedbackService feedbackService = new FeedbackService();

    private EntretienService ens = new EntretienService();


    private AffichageEntretineController parentController;
    @FXML
    private TextArea lblMessage;
    @FXML
    private HBox starContainer;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblNoteValue;


    public void chargerFeedback(int feedbackId) {
        try {
            Feedback feedback = feedbackService.getFeedbackById(feedbackId);

            currentFeedbackId = feedbackId;
            if (feedback != null) {
                lblMessage.setText(feedback.getMessage());
                lblDate.setText("Date: " + feedback.getDate_feedback().toString());

                float rating = feedback.getRate();

                lblNoteValue.setText(String.format("%.1f", rating));

                starContainer.getChildren().clear();

                int fullStars = (int) rating;

                float fraction = rating - fullStars;

                for (int i = 0; i < fullStars; i++) {
                    Label star = new Label("★"); // Unicode star
                    star.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #d9e56f;");
                    starContainer.getChildren().add(star);
                }

                if (fraction > 0) {
                    Label partialStar = new Label("★");
                    partialStar.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #d9e56f " + (fraction * 100) + "%, #D3D3D3 " + (fraction * 100) + "%);");
                    starContainer.getChildren().add(partialStar);
                }

                for (int i = fullStars + (fraction > 0 ? 1 : 0); i < 5; i++) {
                    Label star = new Label("★"); // Unicode star
                    star.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #D3D3D3;");
                    starContainer.getChildren().add(star);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void setParentController(AffichageEntretineController parentController) {
        this.parentController = parentController;
    }






    @FXML
    public void fermerFenetre(ActionEvent actionEvent) throws IOException {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardEmploye.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) lblMessage.getScene().getWindow();
        stage.getScene().setRoot(root);
        stage.setTitle("Liste des équipes");
        }


    @Deprecated
    public void modifierFeedback(ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierFeedback.fxml"));
            Parent modifierView = loader.load();

            ModifierFeedback modifierController = loader.getController();

            modifierController.chargerFeedback(currentFeedbackId);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            stage.setScene(new Scene(modifierView));
            stage.show();

//            lblMessage.getScene().setRoot(modifierView);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void supprimerFeedback(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer ce feedback ?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
//                Entretien e = ens.getEntretienByFeedbackId(currentFeedbackId);
//
//                if (e != null) {
//                    ens.reaassignerFeedback(e.getId(), currentFeedbackId);
//                }

                feedbackService.supprimer(currentFeedbackId);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardEmploye.fxml"));
                Parent entretienView = loader.load();

                AffichageEntretienbyemployeeId controller = loader.getController();
                controller.refreshDatas();

                lblMessage.getScene().setRoot(entretienView);

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }







}











