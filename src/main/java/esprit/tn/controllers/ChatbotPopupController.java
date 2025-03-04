package esprit.tn.controllers;
import esprit.tn.services.ChatbotService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
/*public class ChatbotPopupController {

    @FXML
    private ListView<HBox> questionsList; // Liste des questions

    @FXML
    private ListView<HBox> chatList; // Liste des messages (questions et réponses)

    @FXML
    private TextField questionField;

    private ChatbotService chatbotService = new ChatbotService();

    @FXML
    public void initialize() {
        // Ajouter des exemples de questions
        List<String> questions = Arrays.asList(
                "Quel est le nombre total d'équipes ?",
                "Quel est le nombre total de membres ?",
                "Quel est le nombre moyen de membres par équipe ?",
                "Quelle est l'équipe avec le plus de membres ?",
                "Quelle est l'équipe avec le moins de membres ?"
        );

        for (String question : questions) {
            HBox questionCard = creerCarteQuestion(question);
            questionsList.getItems().add(questionCard);
        }

        // Gérer les clics sur les questions
        questionsList.setOnMouseClicked(event -> {
            HBox selectedCard = questionsList.getSelectionModel().getSelectedItem();
            if (selectedCard != null) {
                Label questionLabel = (Label) selectedCard.getChildren().get(0);
                String question = questionLabel.getText();
                afficherMessageUtilisateur(question);
                simulerChargementReponse(question);
            }
        });
    }

    @FXML
    public void handleAskButton() {
        String question = questionField.getText().trim();
        if (!question.isEmpty()) {
            afficherMessageUtilisateur(question);
            simulerChargementReponse(question);
            questionField.clear();
        }
    }

    private void afficherMessageUtilisateur(String message) {
        HBox messageBox = creerBulleMessage(message, Pos.CENTER_RIGHT, "#0078FF", "white", null);
        chatList.getItems().add(messageBox);
    }

    private void afficherMessageChatbot(String message) {
        ImageView botIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/profil.png")));
        botIcon.setFitWidth(30);
        botIcon.setFitHeight(30);

        HBox messageBox = creerBulleMessage(message, Pos.CENTER_LEFT, "#E5E5EA", "black", botIcon);
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
        questionLabel.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 10px; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-color: #CCCCCC; -fx-border-width: 1px;");
        questionLabel.setMaxWidth(350);

        HBox card = new HBox(questionLabel);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(5));
        return card;
    }

    private void simulerChargementReponse(String question) {
        // Afficher l'effet de chargement
        HBox loadingBox = creerBulleMessage("...", Pos.CENTER_LEFT, "#E5E5EA", "black", null);
        chatList.getItems().add(loadingBox);

        // Simuler un délai de chargement
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            chatList.getItems().remove(loadingBox); // Retirer l'effet de chargement
            String response = chatbotService.repondre(question);
            afficherMessageChatbot(response);
        });
        pause.play();
    }
}*/


public class ChatbotPopupController {

    @FXML
    private HBox questionsContainer; // Conteneur pour les questions

    @FXML
    private ListView<HBox> chatList; // Liste des messages (questions et réponses)

    @FXML
    private TextField questionField;

    private ChatbotService chatbotService = new ChatbotService();

    @FXML
    public void initialize() {
        // Afficher un message d'accueil
        afficherMessageChatbot("Choisissez une question pour commencer !", true);

        // Ajouter des exemples de questions
        List<String> questions = Arrays.asList(
                "Quel est le nombre total d'équipes ?",
                "Quel est le nombre total de membres ?",
                "Quel est le nombre moyen de membres par équipe ?",
                "Quelle est l'équipe avec le plus de membres ?",
                "Quelle est l'équipe avec le moins de membres ?"
        );

        // Afficher les questions par paires
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
        }
    }

    /*@FXML
    public void handleAskButton() {
        String question = questionField.getText().trim();
        if (!question.isEmpty()) {
            afficherMessageUtilisateur(question);
            simulerChargementReponse(question);
            questionField.clear();
        }
    }*/

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
        questionLabel.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 10px; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-color: #CCCCCC; -fx-border-width: 1px;");
        questionLabel.setMaxWidth(180);

        HBox card = new HBox(questionLabel);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(5));
        card.getStyleClass().add("question-card");

        card.setOnMouseClicked(event -> {
            afficherMessageUtilisateur(question);
            simulerChargementReponse(question);
        });

        return card;
    }

    private void simulerChargementReponse(String question) {
        // Afficher l'effet de chargement
        HBox loadingBox = creerBulleMessage("...", Pos.CENTER_LEFT, "#E5E5EA", "black", null);
        chatList.getItems().add(loadingBox);

        // Simuler un délai de chargement
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            chatList.getItems().remove(loadingBox); // Retirer l'effet de chargement
            String response = chatbotService.repondre(question);
            afficherMessageChatbot(response, false);
        });
        pause.play();
    }
}