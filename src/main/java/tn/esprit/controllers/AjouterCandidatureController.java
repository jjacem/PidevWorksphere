package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Candidature;
import tn.esprit.entities.OffreEmploi;
import tn.esprit.entities.User;
import tn.esprit.services.ServiceCandidature;
import tn.esprit.services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterCandidatureController {
    private OffreEmploi selectedOffre; // Stocker l'offre sélectionnée
    @FXML
    private ListView<Candidature> lv_candidatures;

    // Méthode pour passer l'offre sélectionnée depuis le contrôleur précédent
    public void setOffre(OffreEmploi offre) {
        this.selectedOffre = offre;
    }
    @FXML
    private TextField cvField;

    @FXML
    private TextArea lettreMotivationField;

    @FXML
    private Button retourButton;
    @FXML
    private void okpostuler() {
        // Récupérer les informations du formulaire
        String cv = cvField.getText();
        String lettreMotivation = lettreMotivationField.getText();

        // Vérifier si les champs sont vides
        if (cv.isEmpty() || lettreMotivation.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs.");

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return; // Arrêter l'exécution si les champs ne sont pas remplis
        }
        // Vérification du format du CV (doit être un fichier .pdf)
        if (!cv.endsWith(".pdf")) {
            System.out.println("Le CV doit être un fichier au format PDF.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Format de CV invalide");
            alert.setContentText("Le CV doit être un fichier au format .pdf.");
            alert.showAndWait();
            return; // Arrêter l'exécution si le format n'est pas correct
        }

        // Créer une instance de ServiceUser pour récupérer le candidat
        ServiceUser serviceUser = new ServiceUser();
        User candidat = null;
        try {
            candidat = serviceUser.getCandidat(); // Récupérer le candidat avec le rôle "candidat"
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }
        if (candidat == null) {
            System.out.println("Aucun candidat trouvé.");
            return;
        }

        // Créer une nouvelle candidature
        Candidature candidature = new Candidature(selectedOffre,candidat/*Utilisateur connecté*/, cv, lettreMotivation);

        // Appeler le service pour ajouter la candidature
        ServiceCandidature serviceCandidature = new ServiceCandidature();
        try {
            serviceCandidature.ajouter(candidature);
            System.out.println("Candidature envoyée avec succès.");

            // Rediriger l'utilisateur après avoir postulé (par exemple, vers la liste des offres)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherOffreCandidat.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la nouvelle vue
            AfficherOffreCandidatController afficherOffreCandidatController = loader.getController();
            // Passer l'offre sélectionnée au contrôleur
            afficherOffreCandidatController.setOffrePostulee(selectedOffre.getIdOffre());

            Stage stage = (Stage) cvField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
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
