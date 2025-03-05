package esprit.tn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import esprit.tn.entities.OffreEmploi;
import esprit.tn.services.ServiceOffre;
import esprit.tn.controllers.AjouterOffreController.RefreshCallback;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.net.URL;
import java.util.ResourceBundle;

public class ModifierOffreController implements Initializable {

    @FXML
    private TextField titreField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField typeContratField;
    @FXML
    private TextField salaireField;
    @FXML
    private TextField lieuTravailField;
    @FXML
    private TextField experienceField;
    @FXML
    private TextField statutoffreField;
    @FXML
    private DatePicker datepublicationField; // Hidden in UI but still needed
    @FXML
    private DatePicker datelimiteField;

    private OffreEmploi offre; // L'offre à modifier
    private RefreshCallback refreshCallback;
    
    public void setRefreshCallback(RefreshCallback callback) {
        this.refreshCallback = callback;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Nothing to initialize here
    }
    
    public void remplirChamps(OffreEmploi offre) {
        // Store the offer to be modified
        this.offre = offre;
        
        // Fill the fields with the offer data
        titreField.setText(offre.getTitre());
        descriptionField.setText(offre.getDescription());
        typeContratField.setText(offre.getTypeContrat());
        salaireField.setText(String.valueOf(offre.getSalaire()));
        lieuTravailField.setText(offre.getLieuTravail());
        experienceField.setText(String.valueOf(offre.getExperience()));
        statutoffreField.setText(offre.getStatutOffre());

        // Handle original publication date (hidden from UI but kept for data integrity)
        if (offre.getDatePublication() != null) {
            Date datePublication = offre.getDatePublication();
            LocalDate localDatePublication;

            if (datePublication instanceof java.sql.Date) {
                localDatePublication = ((java.sql.Date) datePublication).toLocalDate();
            } else {
                localDatePublication = datePublication.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            datepublicationField.setValue(localDatePublication);
        }

        // Handle expiration date
        if (offre.getDateLimite() != null) {
            Date dateLimite = offre.getDateLimite();
            LocalDate localDateLimite;

            if (dateLimite instanceof java.sql.Date) {
                localDateLimite = ((java.sql.Date) dateLimite).toLocalDate();
            } else {
                localDateLimite = dateLimite.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            datelimiteField.setValue(localDateLimite);
        }
    }

    // Méthode pour appliquer les modifications
    @FXML
    private void ModifierOffre() {
        // Validate fields
        if (!validateFields()) {
            return;
        }

        try {
            // Update offer with new values
            updateOfferFromFields();
            
            // Save to database
            ServiceOffre serviceOffre = new ServiceOffre();
            serviceOffre.modifierOffre(offre);
            
            // Show success alert
            showSuccessAlert();
            
            // Refresh the list view via callback
            if (refreshCallback != null) {
                refreshCallback.refresh();
            }
            
            // Close the dialog
            closeDialog();
            
        } catch (SQLException e) {
            showErrorAlert(e.getMessage());
        }
    }
    
    private boolean validateFields() {
        // Check for empty visible fields (note: datepublicationField is hidden)
        if (salaireField.getText().isEmpty() || titreField.getText().isEmpty() || 
            typeContratField.getText().isEmpty() || lieuTravailField.getText().isEmpty() || 
            statutoffreField.getText().isEmpty() || experienceField.getText().isEmpty() ||
            descriptionField.getText().isEmpty() || datelimiteField.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return false;
        }

        // Validate salary is a number
        try {
            Integer.parseInt(salaireField.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Le champ salaire doit être un nombre valide.");
            alert.showAndWait();
            return false;
        }

        // Validate experience format
        String experienceText = experienceField.getText().trim();
        if (!experienceText.matches("^\\d+\\s*ans$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Le champ expérience doit être un nombre suivi de 'ans' (ex: 3 ans).");
            alert.showAndWait();
            return false;
        }

        // Validate dates - make sure the date limite is not before the original publication date
        if (datelimiteField.getValue().isBefore(datepublicationField.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de date");
            alert.setContentText("La date limite ne peut pas être avant la date de publication originale.");
            alert.showAndWait();
            return false;
        }
        
        return true;
    }
    
    private void updateOfferFromFields() {
        // Important: Keep the original ID and publication date
        LocalDate originalPublicationDate = datepublicationField.getValue();
        
        // Update all other fields with the new values
        offre.setTitre(titreField.getText());
        offre.setDescription(descriptionField.getText());
        offre.setTypeContrat(typeContratField.getText());
        offre.setSalaire(Integer.parseInt(salaireField.getText()));
        offre.setLieuTravail(lieuTravailField.getText());
        offre.setExperience(experienceField.getText());
        offre.setStatutOffre(statutoffreField.getText());
        
        // Keep the original publication date
        offre.setDatePublication(java.sql.Date.valueOf(originalPublicationDate));
        
        // Update the date limite
        offre.setDateLimite(java.sql.Date.valueOf(datelimiteField.getValue()));
    }
    
    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText("Offre modifiée avec succès.");
        alert.showAndWait();
    }
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText("Erreur lors de la modification de l'offre : " + message);
        alert.showAndWait();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }
}




