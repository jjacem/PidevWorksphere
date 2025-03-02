package esprit.tn.controllers;

import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.SessionManager;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import esprit.tn.entities.Candidature;
import esprit.tn.services.OCRService;
import esprit.tn.services.ServiceCandidature;
import esprit.tn.services.TranslationService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.geometry.Pos;

public class AfficherCandidatureController implements Initializable {
    @FXML
    private ListView<Candidature> lv_candidatures;

    @FXML
    private Button retourButton;

    @FXML
    private Button supprimerButton;

    private OCRService ocrService = new OCRService();
    private TranslationService translationService = new TranslationService();

    private ObservableList<Candidature> candidaturesList = FXCollections.observableArrayList();

    // Generate preview image from PDF file
    private Image generatePdfPreview(String filePath) {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage bufferedImage = renderer.renderImageWithDPI(0, 100); // 100 DPI preview
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Set up ListView cell factory with preview images and action buttons
    private void setupListView() {
        lv_candidatures.setCellFactory(lv -> new ListCell<Candidature>() {
            private final Button analyzeButton = new Button("📝 Analyze");
            private final Button translateButton = new Button("🌐 Translate");
            
            {
                analyzeButton.setOnAction(event -> {
                    Candidature candidature = getItem();
                    if (candidature != null) {
                        analyzeCandidature(candidature);
                    }
                });
                
                translateButton.setOnAction(event -> {
                    Candidature candidature = getItem();
                    if (candidature != null) {
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
                });
            }
            

            @Override
            protected void updateItem(Candidature candidature, boolean empty) {
                super.updateItem(candidature, empty);
                if (empty || candidature == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label offerLabel = new Label("Offre: " + candidature.getIdOffre().getTitre());

                    offerLabel.setStyle("-fx-font-weight: bold;");
                    
                    Image cvImage = generatePdfPreview(candidature.getCv());
                    Image lettreImage = generatePdfPreview(candidature.getLettreMotivation());
                    
                    ImageView cvView = new ImageView(cvImage);
                    cvView.setFitWidth(100);
                    cvView.setFitHeight(100);
                    ImageView lettreView = new ImageView(lettreImage);
                    lettreView.setFitWidth(100);
                    lettreView.setFitHeight(100);
                    

                    HBox imagesBox = new HBox(10, 
                        new VBox(5, new Label("CV"), cvView),
                        new VBox(5, new Label("Lettre"), lettreView)
                    );
                    
                    HBox buttonBox = new HBox(10, analyzeButton, translateButton);
                    buttonBox.setStyle("-fx-padding: 5 0 0 0;");
                    
                    VBox cellBox = new VBox(5, offerLabel, imagesBox, buttonBox);
                    cellBox.setStyle("-fx-padding: 5;");
                    
                    setGraphic(cellBox);
                }
            }
        });
    }


    // Load candidatures from database
    private void chargerCandidatures() {
        ServiceUser serviceUser = new ServiceUser();
        ServiceCandidature serviceCandidature = new ServiceCandidature();

        try {
            User currentUser = SessionManager.extractuserfromsession();
            if (currentUser != null) {

                System.out.println("Current user ID: " + currentUser.getIdUser());
                List<Candidature> candidatures = serviceCandidature.getCandidaturesByUser(currentUser.getIdUser());
                System.out.println("Found " + candidatures.size() + " candidatures");
                
                candidaturesList.clear();
                candidaturesList.addAll(candidatures);
                lv_candidatures.setItems(candidaturesList);
                
                if (candidatures.isEmpty()) {
                    System.out.println("No candidatures found");
                } else {
                    for (Candidature c : candidatures) {
                        System.out.println("Candidature: " + c.getCv());
                    }
                }
            } else {
                System.out.println("No user found");
            }
        } catch (SQLException e) {
            System.out.println("Error loading candidatures: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Analyze CV and motivation letter using OCR
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
                    showTextArea("Document Analysis Results", 
                        String.format("CV Content:\n\n%s\n\n" +
                                    "-------------------\n\n" +
                                    "Lettre de Motivation:\n\n%s", 
                        texts[0], texts[1]));
                }
            });
        });
    }

    // Show translation options dialog
    private void showTranslationOptions(Candidature candidature) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Translation Options");
        dialog.setHeaderText("Choose document to translate");
        dialog.setContentText("Select which document you want to translate:");

        ButtonType cvButton = new ButtonType("CV");
        ButtonType lettreButton = new ButtonType("Lettre de Motivation");
        ButtonType cancelButton = ButtonType.CANCEL;

        dialog.getButtonTypes().setAll(cvButton, lettreButton, cancelButton);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            String filePath = null;
            String documentType = null;
            
            if (result.get() == cvButton) {
                filePath = candidature.getCv();
                documentType = "CV";
            } else if (result.get() == lettreButton) {
                filePath = candidature.getLettreMotivation();
                documentType = "Lettre de Motivation";
            } else {
                return;
            }
            
            translateDocument(filePath, documentType);
        }
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

    // Perform document translation
    private void translateDocumentToLanguage(String filePath, String documentType, String sourceLang, String targetLang) {
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
                // First extract text with progress callback
                String extractedText = ocrService.extractTextFromPDF(filePath, 
                    p -> {
                        progressValue[0] = p * 0.5;
                        Platform.runLater(() -> progress.setProgress(progressValue[0]));
                    });
                
                // Then translate with progress callback
                return translationService.translate(extractedText, sourceLang, targetLang, 
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
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

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

    @FXML
    private void retourVersOffres(ActionEvent event) {
        try {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherOffreCandidat.fxml"));
            Parent root = loader.load();
            
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading AfficherOffreCandidat.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerCandidature(ActionEvent event) {
        Candidature selectedCandidature = lv_candidatures.getSelectionModel().getSelectedItem();
        
        if (selectedCandidature == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", 
                "Veuillez sélectionner une candidature à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette candidature ?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            ServiceCandidature serviceCandidature = new ServiceCandidature();
            try {
                serviceCandidature.supprimer(selectedCandidature.getIdCandidature());

                candidaturesList.remove(selectedCandidature);
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                    "La candidature a été supprimée avec succès.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur est survenue lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupListView();
        chargerCandidatures();
        
        // Enable/disable delete button based on selection
        lv_candidatures.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            supprimerButton.setDisable(newSelection == null);
        });
    }
}
