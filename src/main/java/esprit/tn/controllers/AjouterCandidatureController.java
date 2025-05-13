package esprit.tn.controllers;

import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import esprit.tn.entities.Candidature;
import esprit.tn.entities.OffreEmploi;

import esprit.tn.services.ServiceCandidature;
import javafx.stage.FileChooser;
import java.io.File;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterCandidatureController {
    private OffreEmploi selectedOffre; // Stocker l'offre sélectionnée
    private AfficherOffreCandidatController parentController; // Référence au contrôleur parent
    @FXML
    private ListView<Candidature> lv_candidatures;

    // Méthode pour définir le contrôleur parent
    public void setParentController(AfficherOffreCandidatController controller) {
        this.parentController = controller;
    }

    // Méthode pour passer l'offre sélectionnée depuis le contrôleur précédent
    public void setOffre(OffreEmploi offre) {
        this.selectedOffre = offre;
    }
    @FXML
    private TextField cvField;

    @FXML
    private TextField lettreMotivationField;

    @FXML
    private Button retourButton;
    @FXML
    private void okpostuler() {
        // Récupérer les chemins des fichiers sélectionnés
        String cvFilePath = cvField.getText();
        String lettreMotivationFilePath = lettreMotivationField.getText();

        // Vérifier si les champs sont vides
        if (cvFilePath.isEmpty() || lettreMotivationFilePath.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        // Vérification du format du CV
        if (!cvFilePath.toLowerCase().endsWith(".pdf")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Format de CV invalide");
            alert.setContentText("Le CV doit être un fichier au format .pdf.");
            alert.showAndWait();
            return;
        }

        try {
            // Récupérer l'utilisateur connecté
            User candidat = SessionManager.extractuserfromsession();
            if (candidat == null) {
                System.out.println("Aucun candidat trouvé.");
                return;
            }

            // Extraire les noms de fichiers
            File cvSourceFile = new File(cvFilePath);
            File lmSourceFile = new File(lettreMotivationFilePath);
            String cvFileName = cvSourceFile.getName();
            String lmFileName = lmSourceFile.getName();

            // Générer des noms uniques pour éviter les conflits
            String uniqueCvFileName = System.currentTimeMillis() + "-" + cvFileName;
            String uniqueLmFileName = System.currentTimeMillis() + "-" + lmFileName;

            // Définir le chemin du dossier de destination
            String uploadsDir = "C:/Users/jacem/OneDrive/Documents/GitHub/symfony/PIDevWorksphereWeb/public/uploads/";

            // Créer le dossier s'il n'existe pas
            File uploadsDirFile = new File(uploadsDir);
            if (!uploadsDirFile.exists()) {
                uploadsDirFile.mkdirs();
            }

            // Créer les objets File pour la destination
            File cvDestination = new File(uploadsDir + uniqueCvFileName);
            File lmDestination = new File(uploadsDir + uniqueLmFileName);

            // Copier les fichiers avec gestion des erreurs
            copyFileWithRetry(cvSourceFile, cvDestination, 3);
            copyFileWithRetry(lmSourceFile, lmDestination, 3);

            // Créer la candidature avec les nouveaux noms de fichiers
            Candidature candidature = new Candidature(selectedOffre, candidat, uniqueCvFileName, uniqueLmFileName);

            // Enregistrer la candidature
            ServiceCandidature serviceCandidature = new ServiceCandidature();
            serviceCandidature.ajouter(candidature);            // Confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Candidature envoyée");
            alert.setContentText("Votre candidature a été envoyée avec succès.");
            alert.showAndWait();

            // Mettre à jour le contrôleur parent
            if (parentController != null) {
                parentController.setOffrePostulee(selectedOffre.getIdOffre());
                parentController.refreshData();
            }

            // Fermer la fenêtre actuelle
            Stage stage = (Stage) cvField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Une erreur est survenue: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Méthode utilitaire pour copier des fichiers avec retentatives
    private void copyFileWithRetry(File source, File destination, int maxRetries) throws IOException {
        IOException lastException = null;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                // Utiliser une approche de copie plus robuste
                try (java.io.InputStream in = new java.io.FileInputStream(source);
                     java.io.OutputStream out = new java.io.FileOutputStream(destination)) {

                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }

                // Si la copie a réussi, sortir de la boucle
                return;
            } catch (IOException e) {
                lastException = e;
                System.out.println("Tentative " + (attempt + 1) + " échouée: " + e.getMessage());

                // Attendre un moment avant de réessayer
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interruption pendant la pause entre les tentatives", ie);
                }
            }
        }

        // Si nous arrivons ici, c'est que toutes les tentatives ont échoué
        if (lastException != null) {
            throw lastException;
        }
    }

    // Method to open a filepicker for selecting a CV file
    @FXML
    private void browseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier PDF");
        
        // Set extension filter for PDF files
        FileChooser.ExtensionFilter extFilter = 
                new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        
        // Get the source of the event (which button was clicked)
        Button sourceButton = (Button) event.getSource();
        
        // Show file dialog and get the selected file
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        
        if (selectedFile != null) {
            // Check if the CV field is empty, update it first
            if (cvField.getText().isEmpty()) {
                cvField.setText(selectedFile.getAbsolutePath());
            } else {
                // Otherwise, update the lettre de motivation field
                lettreMotivationField.setText(selectedFile.getAbsolutePath());
            }
        }
    }

    // New method for lettre de motivation file upload
    @FXML
    private void browseLettreFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Lettre de Motivation PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(lettreMotivationField.getScene().getWindow());
        if (selectedFile != null) {
            lettreMotivationField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    public void retourVersOffres(ActionEvent event) {
        try {
            // Get the source of the event instead of using the button directly
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
}
