package esprit.tn.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import esprit.tn.entities.OffreEmploi;
import esprit.tn.services.ServiceOffre;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ModifierOffreController {

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
    private DatePicker datepublicationField;
    @FXML
    private DatePicker datelimiteField;

    private OffreEmploi offre; // L'offre à modifier
    public void remplirChamps(OffreEmploi offre) {
        System.out.println(offre);
        this.offre = offre;
        titreField.setText(offre.getTitre());
        descriptionField.setText(offre.getDescription());
        typeContratField.setText(offre.getTypeContrat());
        salaireField.setText(String.valueOf(offre.getSalaire()));
        lieuTravailField.setText(offre.getLieuTravail());
        experienceField.setText(String.valueOf(offre.getExperience()));
        statutoffreField.setText(offre.getStatutOffre());

        // Gestion de la date de publication
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

        // Gestion de la date limite
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

        System.out.println("Date Publication : " + offre.getDatePublication());
        System.out.println("Date Limite : " + offre.getDateLimite());
    }


//    public void remplirChamps(OffreEmploi offre) {
//        System.out.println(offre);
//        this.offre = offre;
//        titreField.setText(offre.getTitre());
//        descriptionField.setText(offre.getDescription());
//        typeContratField.setText(offre.getTypeContrat());
//        salaireField.setText(String.valueOf(offre.getSalaire()));
//        lieuTravailField.setText(offre.getLieuTravail());
//        experienceField.setText(String.valueOf(offre.getExperience()));
//        statutoffreField.setText(offre.getStatutOffre());
//        datepublicationField.setValue(offre.getDatePublication().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//        datelimiteField.setValue(offre.getDateLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//
//        System.out.println("Date Publication : " + offre.getDatePublication());
//        System.out.println("Date Limite : " + offre.getDateLimite());
//
//
//
//    }


    // Méthode pour appliquer les modifications
    @FXML
    private void ModifierOffre() {
        // Validation des champs
        if (salaireField.getText().isEmpty() || titreField.getText().isEmpty() || typeContratField.getText().isEmpty() ||
                lieuTravailField.getText().isEmpty() || statutoffreField.getText().isEmpty() || experienceField.getText().isEmpty() ||
                descriptionField.getText().isEmpty() || datepublicationField.getValue() == null || datelimiteField.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        // Vérification que le salaire est un nombre
        try {
            Integer.parseInt(salaireField.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Le champ salaire doit être un nombre valide.");
            alert.showAndWait();
            return;
        }

        // Vérification que l'expérience est dans le format correct "X ans" où X est un nombre valide
        String experienceText = experienceField.getText().trim();  // On retire les espaces avant et après
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
        if (datelimiteField.getValue().isBefore(datepublicationField.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de date");
            alert.setContentText("La date limite ne peut pas être avant la date de publication.");
            alert.showAndWait();
            return;
        }
        // Récupérer les nouvelles valeurs
        offre.setTitre(titreField.getText());
        offre.setDescription(descriptionField.getText());
        offre.setTypeContrat(typeContratField.getText());
        offre.setSalaire(Integer.parseInt(salaireField.getText()));
        offre.setLieuTravail(lieuTravailField.getText());
        offre.setExperience(experienceField.getText());
        offre.setStatutOffre(statutoffreField.getText());
        offre.setDatePublication(java.sql.Date.valueOf(datepublicationField.getValue()));
        offre.setDateLimite(java.sql.Date.valueOf(datelimiteField.getValue()));

        // Mettre à jour l'offre dans la base de données
        ServiceOffre serviceOffre = new ServiceOffre();
        try {
            serviceOffre.modifierOffre(offre);
            System.out.println("Offre modifiée avec succès.");

            // Redirection vers la scène précédente (ex: listView.fxml)
            // Charger la nouvelle scène
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherOffre.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer
            Stage stage = (Stage) titreField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'offre : " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

//    // Méthode pour définir l'offre à modifier
//    public void setOffre(OffreEmploi offre) {
//        this.offre = offre;
//
//        // Pré-remplir les champs du formulaire avec les valeurs actuelles de l'offre
//        titreField.setText(offre.getTitre());
//        descriptionField.setText(offre.getDescription());
//        typeContratField.setText(offre.getTypeContrat());
//        lieuTravailField.setText(offre.getLieuTravail());
//        salaireField.setText(String.valueOf(offre.getSalaire()));
//        statutoffreField.setText(offre.getStatutOffre());
//        experienceField.setText(String.valueOf(offre.getExperience()));
//        datepublicationField.setValue(LocalDate.parse(offre.getDatePublication().toString()));
//        datelimiteField.setValue(LocalDate.parse(offre.getDateLimite().toString()));
//    }

    // Méthode pour valider et enregistrer la modification de l'offre
//    @FXML
//    private void enregistrerModification() {
//        if (offre != null) {
//            // Mettre à jour les attributs de l'offre avec les nouvelles valeurs des champs
//            offre.setTitre(titreField.getText());
//            offre.setDescription(descriptionField.getText());
//            offre.setTypeContrat(typeContratField.getText());
//            offre.setLieuTravail(lieuTravailField.getText());
//            offre.setSalaire(Integer.parseInt(salaireField.getText()));
//            offre.setStatutOffre(statutoffreField.getText());
//            offre.setExperience((experienceField.getText()));
//            offre.setDatePublication(java.sql.Date.valueOf(datepublicationField.getValue()));
//            offre.setDateLimite(java.sql.Date.valueOf(datelimiteField.getValue()));
//
//            // Appeler le service pour mettre à jour l'offre dans la base de données
//            ServiceOffre serviceOffre = new ServiceOffre();
//            try {
//                serviceOffre.modifierOffre(offre);
//                System.out.println("Offre modifiée avec succès.");
//
//                // Fermer la fenêtre actuelle et revenir à la liste des offres
//                Stage stage = (Stage) titreField.getScene().getWindow();
//                stage.close();
//
//            } catch (SQLException e) {
//                System.out.println("Erreur lors de la modification de l'offre : " + e.getMessage());
//            }
//        }
//    }
//}




