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
import esprit.tn.services.CvEvaluationService;
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
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import esprit.tn.utils.MarkdownUtils;

public class AfficherCandidatureController implements Initializable {
    @FXML
    private ListView<Candidature> lv_candidatures;

    @FXML
    private Button retourButton;

    @FXML
    private Button supprimerButton;

    private OCRService ocrService = new OCRService();
    private TranslationService translationService = new TranslationService();
    private CvEvaluationService cvEvaluationService = new CvEvaluationService();

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

    // Set up ListView cell factory with preview images but without action buttons
    private void setupListView() {
        lv_candidatures.setCellFactory(lv -> new ListCell<Candidature>() {
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
                    
//                    // Status information
//                    Label statusLabel = new Label("Statut: " + candidature.getStatusCandidature());
//                    statusLabel.setStyle("-fx-font-style: italic;");
                    
                    VBox cellBox = new VBox(5, offerLabel, imagesBox);
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

    // These methods are kept but no longer used in the ListView
    // ... existing code for analyzeCandidature, showTranslationOptions, translateDocument, etc...

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
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