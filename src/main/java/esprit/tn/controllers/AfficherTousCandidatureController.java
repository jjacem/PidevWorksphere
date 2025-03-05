package esprit.tn.controllers;

import esprit.tn.entities.Candidature;
import esprit.tn.entities.OffreEmploi;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceCandidature;
import esprit.tn.services.ServiceOffre;
import esprit.tn.services.ServiceUser;
import esprit.tn.services.OCRService;
import esprit.tn.services.TranslationService;
import esprit.tn.services.CvEvaluationService;
import esprit.tn.utils.MarkdownUtils;
import esprit.tn.services.NotificationService;
import esprit.tn.entities.Notification;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.application.Platform;
import javafx.geometry.Pos;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AfficherTousCandidatureController implements Initializable {
    @FXML
    private ListView<Candidature> candidatureListView;
    
    @FXML
    private Button supprimerButton;
    
    @FXML
    private Button retourButton;
    
    private int offreId;
    private ObservableList<Candidature> candidatures = FXCollections.observableArrayList();
    private ServiceCandidature serviceCandidature = new ServiceCandidature();
    private ServiceUser serviceUser = new ServiceUser();
    
    // Add services for CV operations
    private OCRService ocrService = new OCRService();
    private TranslationService translationService = new TranslationService();
    private CvEvaluationService cvEvaluationService = new CvEvaluationService();
    private NotificationService notificationService = new NotificationService();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupListView();
        
        // Disable delete button until a selection is made
        candidatureListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            supprimerButton.setDisable(newValue == null);
        });
    }
    
    public void loadCandidaturesForOffer(int offreId) {
        this.offreId = offreId;
        try {
            // Get candidatures for this offer
            List<Candidature> offerCandidatures = serviceCandidature.getCandidaturesByOffre(offreId);
            
            // Clear and populate the observable list
            candidatures.clear();
            candidatures.addAll(offerCandidatures);
            
            // Set the items to the ListView
            candidatureListView.setItems(candidatures);
            
            // Show message if no candidatures
            if (candidatures.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("Aucune candidature trouvée pour cette offre.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des candidatures: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible de charger les candidatures: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void setupListView() {
        candidatureListView.setCellFactory(param -> new ListCell<Candidature>() {
            @Override
            protected void updateItem(Candidature candidature, boolean empty) {
                super.updateItem(candidature, empty);
                
                if (empty || candidature == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        // Get user information
                        User candidate = serviceUser.findbyid(candidature.getIdCandidat());
                        
                        // Create cell content
                        VBox cellContent = new VBox(5);
                        
                        // User info
                        Text candidateInfo = new Text("Candidat: " + candidate.getNom() + " " + candidate.getPrenom());
                        candidateInfo.setStyle("-fx-font-weight: bold;");
                        
                        // Create PDF previews
                        Image cvImage = generatePdfPreview(candidature.getCv());
                        Image lettreImage = generatePdfPreview(candidature.getLettreMotivation());
                        
                        ImageView cvImageView = new ImageView(cvImage);
                        cvImageView.setFitHeight(100);
                        cvImageView.setFitWidth(75);
                        cvImageView.setPreserveRatio(true);
                        
                        ImageView lettreImageView = new ImageView(lettreImage);
                        lettreImageView.setFitHeight(100);
                        lettreImageView.setFitWidth(75);
                        lettreImageView.setPreserveRatio(true);
                        
                        // Create documents container
                        HBox documents = new HBox(10);
                        documents.getChildren().addAll(
                            new VBox(5, new Text("CV"), cvImageView),
                            new VBox(5, new Text("Lettre"), lettreImageView)
                        );
                        
                        // Create action buttons
                        Button analyzeButton = new Button("📝 Analyze");
                        Button translateButton = new Button("🌐 Translate");
                        Button evaluateButton = new Button("⭐ Evaluate");
                        
                        // Configure button actions
                        analyzeButton.setOnAction(event -> analyzeCandidature(candidature));
                        translateButton.setOnAction(event -> showTranslationOptions(candidature));
                        evaluateButton.setOnAction(event -> evaluateCandidature(candidature));
                        
                        HBox actionButtons = new HBox(10, analyzeButton, translateButton, evaluateButton);
                        actionButtons.setStyle("-fx-padding: 5 0 5 0;");
                        
                        // Add everything to cell content
                        cellContent.getChildren().addAll(candidateInfo, documents, actionButtons);
                        cellContent.setStyle("-fx-padding: 5;");
                        
                        setGraphic(cellContent);
                    } catch (SQLException e) {
                        setText("Erreur de chargement");
                    }
                }
            }
        });
    }
    
    // Analyze CV et lettre de motivation en utilisant OCR
    private void analyzeCandidature(Candidature candidature) {
        ProgressIndicator progress = new ProgressIndicator();
        VBox loadingBox = new VBox(10, new Label("Analyzing documents..."), progress);
        loadingBox.setAlignment(Pos.CENTER);

        Stage loadingStage = new Stage(StageStyle.UNDECORATED);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setScene(new Scene(loadingBox, 200, 100));
        loadingStage.show();

        CompletableFuture.supplyAsync(() -> {
            try {
                String cvPath = candidature.getCv();
                String lettrePath = candidature.getLettreMotivation();
                
                double[] progressValue = {0.0};
                String cvText = ocrService.extractTextFromPDF(cvPath, 
                    p -> {
                        progressValue[0] = p * 0.5;
                        Platform.runLater(() -> progress.setProgress(progressValue[0]));
                    });
                
                String lettreText = ocrService.extractTextFromPDF(lettrePath, 
                    p -> {
                        progressValue[0] = 0.5 + p * 0.5;
                        Platform.runLater(() -> progress.setProgress(progressValue[0]));
                    });
                
                return new String[]{cvText, lettreText};
            } catch (Exception e) {
                throw new RuntimeException("Analysis failed: " + e.getMessage(), e);
            }
        }).whenComplete((texts, error) -> {
            Platform.runLater(() -> {
                loadingStage.close();
                if (error != null) {
                    showAlert(Alert.AlertType.ERROR, "Analysis Error", error.getMessage());
                } else {
                    showTextArea("Document Analysis Results :", 
                        String.format("* CV Content:\n\n%s\n\n" +
                                    "------------------------------------\n\n" +
                                    "* Lettre de Motivation:\n\n%s", 
                        texts[0], texts[1]));
                }
            });
        });
    }

    // Show translation options dialog
    private void showTranslationOptions(Candidature candidature) {
        Alert choiceDialog = new Alert(Alert.AlertType.CONFIRMATION);
        choiceDialog.setTitle("Choose Document");
        choiceDialog.setHeaderText("Select document to translate");
        choiceDialog.setContentText("Which document would you like to translate?");

        ButtonType cvButton = new ButtonType("CV");
        ButtonType lettreButton = new ButtonType("Lettre de Motivation");
        ButtonType cancelButton = ButtonType.CANCEL;

        choiceDialog.getButtonTypes().setAll(cvButton, lettreButton, cancelButton);

        choiceDialog.showAndWait().ifPresent(response -> {
            if (response == cvButton) {
                translateDocument(candidature.getCv(), "CV");
            } else if (response == lettreButton) {
                translateDocument(candidature.getLettreMotivation(), "Lettre de Motivation");
            }
        });
    }

    // Show language selection dialog
    private void translateDocument(String filePath, String documentType) {
        ChoiceDialog<String> langDialog = new ChoiceDialog<>("English", 
            Arrays.asList("English", "Spanish", "German", "Arabic"));
        langDialog.setTitle("Choose Target Language");
        langDialog.setHeaderText("Select translation language for " + documentType);
        
        langDialog.showAndWait().ifPresent(targetLang -> {
            String langCode = switch (targetLang) {
                case "English" -> "en";
                case "Spanish" -> "es";
                case "German" -> "de";
                case "Arabic" -> "ar";
                default -> "en";
            };

            ProgressIndicator progress = new ProgressIndicator();
            VBox loadingBox = new VBox(10, new Label("Translating " + documentType + "..."), progress);
            loadingBox.setAlignment(Pos.CENTER);

            Stage loadingStage = new Stage(StageStyle.UNDECORATED);
            loadingStage.initModality(Modality.APPLICATION_MODAL);
            loadingStage.setScene(new Scene(loadingBox, 200, 100));
            loadingStage.show();

            CompletableFuture.supplyAsync(() -> {
                try {
                    double[] progressValue = {0.0};
                    String extractedText = ocrService.extractTextFromPDF(filePath, 
                        p -> {
                            progressValue[0] = p * 0.5;
                            Platform.runLater(() -> progress.setProgress(progressValue[0]));
                        });
                    
                    return translationService.translate(extractedText, "fr", langCode, 
                        p -> {
                            progressValue[0] = 0.5 + p * 0.5;
                            Platform.runLater(() -> progress.setProgress(progressValue[0]));
                        });
                } catch (Exception e) {
                    throw new RuntimeException("Translation failed: " + e.getMessage(), e);
                }
            }).whenComplete((translatedText, error) -> {
                Platform.runLater(() -> {
                    loadingStage.close();
                    if (error != null) {
                        showAlert(Alert.AlertType.ERROR, "Translation Error", error.getMessage());
                    } else {
                        showTextArea("Translated " + documentType, translatedText);
                    }
                });
            });
        });
    }

    // Evaluate CV against job requirements
    private void evaluateCandidature(Candidature candidature) {
        ProgressIndicator progress = new ProgressIndicator();
        VBox loadingBox = new VBox(10, new Label("Evaluating CV..."), progress);
        loadingBox.setAlignment(Pos.CENTER);

        Stage loadingStage = new Stage(StageStyle.UNDECORATED);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setScene(new Scene(loadingBox, 200, 100));
        loadingStage.show();

        CompletableFuture.supplyAsync(() -> {
            try {
                // Extract CV text
                String cvText = ocrService.extractTextFromPDF(candidature.getCv(), 
                    p -> Platform.runLater(() -> progress.setProgress(p)));
                
                // Log extracted text for debugging
                System.out.println("Extracted CV text: " + cvText);
                
                if (cvText == null || cvText.trim().isEmpty()) {
                    throw new RuntimeException("Failed to extract text from CV");
                }
                
                return cvText;
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract CV text: " + e.getMessage(), e);
            }
        }).thenCompose(cvText -> 
            cvEvaluationService.evaluateCV(candidature.getIdOffre(), cvText)
        ).whenComplete((result, error) -> {
            Platform.runLater(() -> {
                loadingStage.close();
                if (error != null) {
                    showAlert(Alert.AlertType.ERROR, "Evaluation Error", 
                        "Failed to evaluate CV: " + error.getMessage());
                } else {
                    showEvaluationResult(result);
                }
            });
        });
    }

    // Show evaluation result in formatted HTML
    private void showEvaluationResult(String evaluation) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("CV Evaluation Results");
        
        // Convert evaluation to markdown and then to HTML
        String markdown = MarkdownUtils.formatEvaluationAsMarkdown(evaluation);
        String html = MarkdownUtils.markdownToHtml(markdown);
        
        // Create a pretty HTML document with styling
        String styledHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { 
                        font-family: Arial, sans-serif; 
                        line-height: 1.6;
                        margin: 20px;
                        color: #333;
                    }
                    h1 { color: #2c3e50; }
                    h2 { color: #3498db; margin-top: 20px; }
                    h3 { color: #16a085; }
                    ul, ol { margin-bottom: 15px; }
                    li { margin-bottom: 5px; }
                    .match-percentage { 
                        font-size: 1.2em; 
                        font-weight: bold;
                        color: #27ae60;
                    }
                    .missing-skills {
                        color: #e74c3c;
                    }
                    .strengths {
                        color: #27ae60;
                    }
                </style>
            </head>
            <body>
            %s
            </body>
            </html>
            """.formatted(html);
        
        // Use WebView to display HTML content
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.loadContent(styledHtml);
        
        webView.setPrefSize(700, 500);
        
        VBox content = new VBox(10);
        content.getChildren().add(webView);
        content.setStyle("-fx-padding: 10px;");
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefSize(720, 550);
        
        // Set dialog modality and show
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.show();
    }
    
    private Image generatePdfPreview(String filePath) {
        try {
            PDDocument document = PDDocument.load(new File(filePath));
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage bufferedImage = renderer.renderImageWithDPI(0, 100); // Render first page at 100 DPI
            document.close();
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (IOException e) {
            System.err.println("Erreur de prévisualisation du PDF: " + e.getMessage());
            // Return a placeholder image
            return new Image(getClass().getResourceAsStream("/Images/pdf_icon.png"));
        }
    }
    
    @FXML
    private void supprimerCandidature(ActionEvent event) {
        Candidature selectedCandidature = candidatureListView.getSelectionModel().getSelectedItem();
        if (selectedCandidature == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setContentText("Veuillez sélectionner une candidature à supprimer.");
            alert.showAndWait();
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette candidature ?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                // Get offer and user info for notification
                OffreEmploi offre = selectedCandidature.getIdOffre();
                int candidatId = selectedCandidature.getIdCandidat();
                
                // Delete the candidature from the database
                serviceCandidature.supprimer(selectedCandidature.getIdCandidature());
                
                // Send notification to the candidate
                String message = "Votre candidature pour l'offre '" + offre.getTitre() + 
                                 "' a été supprimée par le recruteur, à cause du fait qu'elle ne respectait pas nos condidtions. \n\n" +
                                 "Cordialement, \n" +
                                 "Le Service Recrutement.";
                
                Notification notification = new Notification(candidatId, message, "candidature_deleted");
                notificationService.addNotification(notification);
                
                // Remove from the list
                candidatures.remove(selectedCandidature);
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Succès");
                success.setContentText("Candidature supprimée avec succès et notification envoyée au candidat.");
                success.showAndWait();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la suppression: " + e.getMessage());
                
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setContentText("Impossible de supprimer la candidature: " + e.getMessage());
                error.showAndWait();
            }
        }
    }
    
    @FXML
    private void retourVersOffres(ActionEvent event) {
        try {
            // Load AfficherOffre view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherOffre.fxml"));
            Parent root = loader.load();
            
            // Switch back to the offer view
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du retour à la liste des offres: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Display text in a dialog
    private void showTextArea(String title, String content) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(600);
        textArea.setPrefHeight(400);
        
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }

    // Show alert dialogs
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
