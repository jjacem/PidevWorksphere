package esprit.tn.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import esprit.tn.services.QuoteFetcher;
import javafx.util.Duration;

public class QuoteController {

    @FXML
    private Label quoteLabel;

    @FXML
    private Button fetchButton;

    @FXML
    public void initialize() {
        fetchButton.setOnAction(event -> {
            String quote = QuoteFetcher.fetchRandomQuote().toString();
            quoteLabel.setText(quote);

            // Animation de fondu
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), quoteLabel);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();
        });
    }
}