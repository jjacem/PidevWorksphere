package esprit.tn.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import esprit.tn.entities.OffreEmploi;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import esprit.tn.services.ServiceOffre;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.net.URL;
import java.util.ResourceBundle;

public class AjouterOffreController implements Initializable {

    private OffreEmploi derniereOffreAjoutee; // Variable pour stocker l'offre ajoutée
    
    @FXML
    private TextField titreoffre;
    @FXML
    private TextArea descriptionoffre;
    @FXML
    private TextField typeoffre;
    @FXML
    private TextField salaireoffre;
    @FXML
    private TextField lieuoffre;
    @FXML
    private TextField experienceoffre;
    @FXML
    private TextField statutoffre;
    @FXML
    private DatePicker datepublication; // Still needed but hidden from UI
    @FXML
    private DatePicker datelimite;
    
    // Callback interface for refreshing the offer list
    public interface RefreshCallback {
        void refresh();
    }
    
    private RefreshCallback refreshCallback;
    
    public void setRefreshCallback(RefreshCallback callback) {
        this.refreshCallback = callback;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datepublication.setValue(LocalDate.now());
        datelimite.setValue(LocalDate.now().plusDays(30));
    }

    public void AjouterOffre(ActionEvent actionEvent) {
        // Création du service d'ajout d'offre
        ServiceOffre serviceOffreEmploi = new ServiceOffre();

        // Validation des champs visibles (note: datepublication est toujours définie automatiquement)
        if (titreoffre.getText().isEmpty() || typeoffre.getText().isEmpty() ||
                lieuoffre.getText().isEmpty() || statutoffre.getText().isEmpty() || 
                experienceoffre.getText().isEmpty() || descriptionoffre.getText().isEmpty() || 
                salaireoffre.getText().isEmpty() || datelimite.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }
        
        // Validation du salaire
        try {
            Integer.parseInt(salaireoffre.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Le champ salaire doit être un nombre valide.");
            alert.showAndWait();
            return;
        }
        
        // Validation du format de l'expérience
        String experienceText = experienceoffre.getText().trim();
        if (!experienceText.matches("^\\d+\\s*ans$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Le champ expérience doit être un nombre suivi de 'ans' (ex: 3 ans).");
            alert.showAndWait();
            return;
        }

        // Validation de la date limite
        if (datelimite.getValue().isBefore(datepublication.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de date");
            alert.setContentText("La date limite ne peut pas être avant la date de publication (aujourd'hui).");
            alert.showAndWait();
            return;
        }

        // Création de l'objet OffreEmploi avec la date de publication actuelle
        OffreEmploi o1 = new OffreEmploi(
            Integer.parseInt(salaireoffre.getText()),
            titreoffre.getText(),
            descriptionoffre.getText(),
            typeoffre.getText(),
            lieuoffre.getText(),
            statutoffre.getText(),
            experienceoffre.getText(),
            java.sql.Date.valueOf(LocalDate.now()), // Always use today's date for publication
            java.sql.Date.valueOf(datelimite.getValue())
        );

        try {
            // Ajouter l'offre dans la base de données
            serviceOffreEmploi.ajouter(o1);

            // Stocker la dernière offre ajoutée
            derniereOffreAjoutee = o1;

            // Notification de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Offre d'emploi ajoutée avec succès !");
            alert.showAndWait();

            // Rafraîchir la liste des offres si le callback est disponible
            if (refreshCallback != null) {
                refreshCallback.refresh();
            }
            
            // Fermer la fenêtre
            Stage stage = (Stage) titreoffre.getScene().getWindow();
            stage.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Échec de l'ajout de l'offre d'emploi: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
