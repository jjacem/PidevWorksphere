package esprit.tn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import esprit.tn.services.QuoteFetcher;

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
        });
    }
}