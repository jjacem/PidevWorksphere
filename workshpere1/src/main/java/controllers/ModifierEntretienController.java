package controllers;

import entities.Entretien;
import entities.TypeEntretien;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.EntretienService;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.time.LocalDate;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class ModifierEntretienController implements Initializable {
    @FXML
    private ComboBox cb_type_entretien;
    @FXML
    private TextField tf_titre;
    @FXML
    private DatePicker dp_date_entretien;
    @FXML
    private CheckBox cb_status;
    @FXML
    private Button btnModifier;
    @FXML
    private TextField tf_description;
    @FXML
    private Spinner sp_heure_entretien;

    private EntretienService entretienService;
    private Entretien entretienActuel;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        entretienService = new EntretienService();
        cb_type_entretien.getItems().addAll(TypeEntretien.values()); // FIXED

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        sp_heure_entretien.setValueFactory(valueFactory);
    }


//    public void chargerEntretien(Entretien entretien) {
//        this.entretienActuel = entretien;
//        tf_titre.setText(entretien.getTitre());
//        tf_description.setText(entretien.getDescription());
////        dp_date_entretien.setValue(entretien.getDate_entretien().);
//        sp_heure_entretien.getValueFactory().setValue(entretien.getHeure_entretien());
//        cb_type_entretien.setValue(entretien.getType_entretien());
//        cb_status.setSelected(entretien.isStatus());
//    }

    public void chargerEntretien(Entretien entretien) {
        this.entretienActuel = entretien;
        tf_titre.setText(entretien.getTitre());
        tf_description.setText(entretien.getDescription());

        if (entretien.getDate_entretien() != null) {
            Date date = entretien.getDate_entretien();

            LocalDate localDate;
            if (date instanceof java.sql.Date) {
                localDate = ((java.sql.Date) date).toLocalDate();
            } else {
                localDate = date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            dp_date_entretien.setValue(localDate) ;
        }

        if (entretien.getHeure_entretien() != null) {
            LocalTime localTime = entretien.getHeure_entretien().toLocalTime();
            sp_heure_entretien.getValueFactory().setValue(localTime.getHour());
        }

        cb_type_entretien.setValue(entretien.getType_entretien());
        cb_status.setSelected(entretien.isStatus());
    }

//    @javafx.fxml.FXML
//    public void modifierEntretien(ActionEvent actionEvent) {
//
//        try {
//
//            LocalDate date = dp_date_entretien.getValue();
//            TypeEntretien type = TypeEntretien.valueOf((String) cb_type_entretien.getValue());
//
//            java.util.Date dateEntretien = java.util.Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
//            entretienActuel.setTitre(tf_titre.getText());
//            entretienActuel.setDescription(tf_description.getText());
//            entretienActuel.setDate_entretien(dateEntretien);
//            entretienActuel.setType_entretien(type);
//            entretienActuel.setStatus(cb_status.isSelected());
//
//            entretienService.modifier(entretienActuel);
//            fermerFenetre();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//
//
//
//    }


    @javafx.fxml.FXML
    public void modifierEntretien(ActionEvent actionEvent) {
        try {
            LocalDate date = dp_date_entretien.getValue();
            TypeEntretien type = TypeEntretien.valueOf(cb_type_entretien.getValue().toString());

            Date dateEntretien = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

            int hour = (int) sp_heure_entretien.getValue();
            Time heureEntretien = Time.valueOf(LocalTime.of(hour, 0));

            entretienActuel.setTitre(tf_titre.getText());
            entretienActuel.setDescription(tf_description.getText());
            entretienActuel.setDate_entretien(dateEntretien);
            entretienActuel.setHeure_entretien(heureEntretien);
            entretienActuel.setType_entretien(type);
            entretienActuel.setStatus(cb_status.isSelected());

            entretienService.modifier(entretienActuel);
            fermerFenetre();

            ouvrirAffichageEntretien();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @javafx.fxml.FXML
    public void fermerFenetre() {

        Stage stage = (Stage) btnModifier.getScene().getWindow();
        stage.close();
    }

    private void ouvrirAffichageEntretien() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
            Parent root = loader.load();

            AffichageEntretineController controller = loader.getController();
            controller.initialize();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Entretiens");
            stage.show();

        } catch (IOException | SQLException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'affichage : " + e.getMessage());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }






}
