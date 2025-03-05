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
    private DatePicker datepublication;
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
        // Make this field read-only to prevent changes
        datepublication.setEditable(false);
        datelimite.setValue(LocalDate.now().plusDays(30));
    }

    public void AjouterOffre(ActionEvent actionEvent) {
        // Création du service d'ajout d'offre
        ServiceOffre serviceOffreEmploi = new ServiceOffre();

        // Validation des champs
        if (salaireoffre.getText().isEmpty() || titreoffre.getText().isEmpty() || typeoffre.getText().isEmpty() ||
                lieuoffre.getText().isEmpty() || statutoffre.getText().isEmpty() || experienceoffre.getText().isEmpty() ||
                descriptionoffre.getText().isEmpty() || datepublication.getValue() == null || datelimite.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }
        // Vérification que le salaire est un nombre
        try {
            Integer.parseInt(salaireoffre.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Le champ salaire doit être un nombre valide.");
            alert.showAndWait();
            return;
        }
        // Vérification que l'expérience est dans le format correct "X ans" où X est un nombre valide
        String experienceText = experienceoffre.getText().trim();  // On retire les espaces avant et après
        if (!experienceText.matches("^\\d+\\s*ans$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Le champ expérience doit être un nombre suivi de 'ans' (ex: 3 ans).");
            alert.showAndWait();
            return;
        }

        // Récupération du nombre d'années à partir de la chaîne
        int experienceYears = Integer.parseInt(experienceText.replaceAll("\\D", ""));  // On retire le texte "ans" et on garde le nombre

        // Vérification que la date limite est après la date de publication
        if (datelimite.getValue().isBefore(datepublication.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de date");
            alert.setContentText("La date limite ne peut pas être avant la date de publication.");
            alert.showAndWait();
            return;
        }

        // Récupération des données saisies dans les champs
        OffreEmploi o1 = new OffreEmploi(Integer.parseInt(salaireoffre.getText()),titreoffre.getText(),descriptionoffre.getText() ,typeoffre.getText(),lieuoffre.getText(),statutoffre.getText(),experienceoffre.getText(),java.sql.Date.valueOf(datepublication.getValue()),java.sql.Date.valueOf(datelimite.getValue()));

        try {
            System.out.println(o1);
            // Ajouter l'offre dans la base de données
            serviceOffreEmploi.ajouter(o1);

            // Stocker la dernière offre ajoutée
            derniereOffreAjoutee = o1;

            // Alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Offre d'emploi ajoutée avec succès !");
            alert.showAndWait();

            // Call refresh callback if available
            if (refreshCallback != null) {
                refreshCallback.refresh();
            }
            
            // Close the window
            Stage stage = (Stage) titreoffre.getScene().getWindow();
            stage.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());

            // Alerte d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Échec de l'ajout de l'offre d'emploi.");
            alert.showAndWait();
        }
    }
}
