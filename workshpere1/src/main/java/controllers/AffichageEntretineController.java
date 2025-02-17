package controllers;

import entities.Entretien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.EntretienService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AffichageEntretineController {
    @FXML
    private ListView<Entretien> lv_entretien;

    private EntretienService entretienService = new EntretienService();
    @FXML
    private Button btn_ajouter;


    @FXML
    public void initialize() throws SQLException {
        afficherEntretien();
    }

    private void afficherEntretien() throws SQLException {
        List<Entretien> entretiens = entretienService.afficher();
        ObservableList<Entretien> data = FXCollections.observableArrayList(entretiens);
        lv_entretien.setItems(data);

        lv_entretien.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Entretien entretien, boolean empty) {
                super.updateItem(entretien, empty);
                if (empty || entretien == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Button btnModifier = new Button("Modifier");
                    Button btnSupprimer = new Button("Supprimer");
                    Button btnAffecter = new Button("Affecter");

                    btnModifier.setOnAction(event -> ouvrirModifierEntretien(entretien));


                    HBox buttonBox = new HBox(10, btnModifier, btnSupprimer, btnAffecter);

                    setText("Titre: " + entretien.getTitre() +
                            " | Description: " + entretien.getDescription() +
                            " | Date: " + entretien.getDate_entretien() +
                            " | Heure: " + entretien.getHeure_entretien() +
                            " | Type: " + entretien.getType_entretien() +
                            " | Statut: " + (entretien.isStatus() ? "Terminé" : "En cours"));

                    setGraphic(buttonBox);
                }
            }
        });
    }

    @FXML
    public void ajouterEntretien(ActionEvent actionEvent) {


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterEntretien.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un entretien");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void ouvrirModifierEntretien(Entretien entretien) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierEntretien.fxml"));
            Parent root = loader.load();

            ModifierEntretienController controller = loader.getController();
            controller.chargerEntretien(entretien);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Entretien");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}








