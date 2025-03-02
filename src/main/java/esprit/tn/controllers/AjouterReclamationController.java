package esprit.tn.controllers;

import esprit.tn.entities.Reclamation;
import esprit.tn.services.ServiceReclamation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class AjouterReclamationController {

    @FXML
    private TextField titre;

    @FXML
    private TextArea description;

    @FXML
    private ChoiceBox<String> type;

    @FXML
    private Button ajouterButton;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();

    @FXML
<<<<<<< Updated upstream
    public void initialize() {
        System.out.println("AjouterReclamationController.initialize");
=======
    public void initialize() throws SQLException {
        ServiceUser u = new ServiceUser();
        Employy = u.getUsersByRoleEmployee();

        List<String> emailList = new ArrayList<>();
        for (User employee : Employy) {
            emailList.add(employee.getEmail());
        }
        Employees.getItems().addAll(emailList);

>>>>>>> Stashed changes
        List<String> types = Arrays.asList("Technique", "Administratif", "Autre");
        type.getItems().addAll(types);
    }

    @FXML
<<<<<<< Updated upstream
    private void ajouter(ActionEvent event) {

        if (titre.getText().isEmpty() || description.getText().isEmpty() || type.getValue() == null) {
=======
    private void ajouter(ActionEvent event) throws SQLException {
        if (titre.getText().isEmpty() || description.getText().isEmpty() || type.getValue() == null || Employees.getValue() == null) {
>>>>>>> Stashed changes
            showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

<<<<<<< Updated upstream

        Reclamation reclamation = new Reclamation(
                "En attente",
                titre.getText(),
=======
        String selectedEmail = Employees.getValue();
        int selectedEmployeeId = -1;
        for (User employee : Employy) {
            if (employee.getEmail().equals(selectedEmail)) {
                selectedEmployeeId = employee.getIdUser();
                break;
            }
        }

        if (selectedEmployeeId == -1) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Employé non trouvé.");
            return;
        }

        Reclamation reclamation = new Reclamation(
                titre.getText(),

>>>>>>> Stashed changes
                description.getText(),
                type.getValue(),3
                ,
                13
        );
        System.out.println(reclamation);
        try {

            serviceReclamation.ajouter(reclamation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation ajoutée avec succès !");
            clearFields();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter la réclamation.");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        titre.clear();
        description.clear();
        type.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
