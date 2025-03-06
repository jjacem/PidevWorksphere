package esprit.tn.controllers;

import esprit.tn.services.ChatbotEquipeService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

public class ChatbotPopupController {

    @FXML
    private VBox questionsContainer;

    @FXML
    private ListView<HBox> chatList;

    @FXML
    private TextField questionField;

    private ChatbotEquipeService chatbotService = new ChatbotEquipeService();
    @FXML
    public void initialize() {
        // Afficher un message d'accueil
        afficherMessageChatbot("Choisissez une question pour commencer !", true);

        // Ajouter des exemples de questions
        List<String> questions = Arrays.asList(
                "Quel est le nombre total d'équipes ?",
                "Quel est le nombre total des employés ?",
                "Quel est le nombre moyen d'employé par équipe ?",
                "Quelle est l'équipe avec le plus d'employés ?",
                "Quelle est l'équipe avec le moins d'employés ?"
        );


        for (int i = 0; i < questions.size(); i += 2) {
            HBox questionRow = new HBox(10);
            questionRow.setAlignment(Pos.CENTER);

            if (i < questions.size()) {
                questionRow.getChildren().add(creerCarteQuestion(questions.get(i)));
            }
            if (i + 1 < questions.size()) {
                questionRow.getChildren().add(creerCarteQuestion(questions.get(i + 1)));
            }

            questionsContainer.getChildren().add(questionRow);
            VBox.setMargin(questionRow, new Insets(5, 0, 5, 0));
        }
    }


    private void afficherMessageUtilisateur(String message) {
        HBox messageBox = creerBulleMessage(message, Pos.CENTER_RIGHT, "#0078FF", "white", null);
        chatList.getItems().add(messageBox);
    }


    private void afficherMessageChatbot(String message, boolean isWelcome) {
        ImageView botIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/profil.png")));
        botIcon.setFitWidth(30);
        botIcon.setFitHeight(30);

        HBox messageBox = creerBulleMessage(message, Pos.CENTER_LEFT, "#E5E5EA", "black", isWelcome ? botIcon : null);
        chatList.getItems().add(messageBox);
    }


    private HBox creerBulleMessage(String texte, Pos alignement, String bgColor, String textColor, ImageView icon) {
        Label messageLabel = new Label(texte);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 10px; -fx-background-radius: 15px; -fx-text-fill: " + textColor + ";");
        messageLabel.setMaxWidth(300);

        HBox messageBox = new HBox(10);
        messageBox.setAlignment(alignement);

        if (icon != null) {
            messageBox.getChildren().addAll(icon, messageLabel);
        } else {
            messageBox.getChildren().add(messageLabel);
        }

        return messageBox;
    }


    private HBox creerCarteQuestion(String question) {
        Label questionLabel = new Label(question);
        questionLabel.setWrapText(true);
        questionLabel.setStyle("-fx-font-size: 11px; -fx-text-fill:black;");
        questionLabel.setMaxWidth(300);

        HBox card = new HBox(questionLabel);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10)); // Padding réduit
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-color: #0078FF; -fx-border-width: 1px;");
        card.getStyleClass().add("question-card");

        card.setOnMouseClicked(event -> {
            afficherMessageUtilisateur(question);
            simulerChargementReponse(question);
        });

        return card;
    }

    private void simulerChargementReponse(String question) {

        HBox loadingBox = creerBulleMessage("...", Pos.CENTER_LEFT, "#E5E5EA", "black", null);
        chatList.getItems().add(loadingBox);


        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            chatList.getItems().remove(loadingBox); // Retirer l'effet de chargement
            String response = chatbotService.repondre(question); // Obtenir la réponse du service
            afficherMessageChatbot(response, false); // Afficher la réponse
        });
        pause.play();
    }
}