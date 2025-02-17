package controllers;


import entities.Entretien;
import entities.TypeEntretien;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import services.EntretienService;

import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class AjouterEntretineController {


    @FXML
    private ComboBox cb_type_entretien;
    @FXML
    private TextField tf_titre;
    @FXML
    private DatePicker dp_date_entretien;
    @FXML
    private CheckBox cb_status;
    @FXML
    private TextField tf_description;
    @FXML
    private Button btnAjouter;
    @FXML
    private Spinner sp_heure_entretien;

    private  EntretienService entretienService = new EntretienService();


    @FXML
    public void initialize() {
        cb_type_entretien.getItems().addAll("EN_PRESENTIEL", "EN_VISIO");
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        sp_heure_entretien.setValueFactory(valueFactory);
    }





    @FXML
    public void ajouterEntretien(ActionEvent actionEvent) throws SQLException {

        try{

        String titre = tf_titre.getText();
        String description = tf_description.getText();
        LocalDate date = dp_date_entretien.getValue();
        int heure = (int) sp_heure_entretien.getValue();
//        TypeEntretien type = (TypeEntretien) cb_type_entretien.getValue();

            TypeEntretien type = TypeEntretien.valueOf((String) cb_type_entretien.getValue());

            boolean status = cb_status.isSelected();



        if (titre.isEmpty() || description.isEmpty() || date == null || type == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        Date dateEntretien = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Time heureEntretien = Time.valueOf(LocalTime.of(heure, 0));

        Entretien entretien = new Entretien(titre, description, dateEntretien, heureEntretien, type, status);

        entretienService.ajouter(entretien);


        clearFields();  }

        catch (SQLException e) {
            showAlert( "Erreur SQL", "Erreur lors de l'ajout : " + e.getMessage());
        }



    }

    private void clearFields() {
            tf_titre.clear();
            tf_description.clear();
            dp_date_entretien.setValue(null);
            cb_type_entretien.setValue(null);
            cb_status.setSelected(false);
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }


    @FXML
    private void fermerFenetre() {
        btnAjouter.getScene().getWindow().hide();
    }


}
