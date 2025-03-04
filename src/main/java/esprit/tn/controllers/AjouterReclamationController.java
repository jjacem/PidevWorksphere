package esprit.tn.controllers;

import esprit.tn.entities.Reclamation;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceReclamation;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.Router;
import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.ArrayList;
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
    private ChoiceBox<String> Employees;

    @FXML
    private Button ajouterButton;

    private List<User> Employy;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();

    @FXML
    public void initialize() throws SQLException {
        ServiceUser u = new ServiceUser();
        Employy = u.getUsersByRoleEmployee();

        List<String> emailList = new ArrayList<>();
        for (User employee : Employy) {
            emailList.add(employee.getEmail());
        }
        Employees.getItems().addAll(emailList);

        List<String> types = Arrays.asList("Technique", "Administratif", "Autre");
        type.getItems().addAll(types);
    }

    @FXML
    private void ajouter(ActionEvent event) throws SQLException {
        if (titre.getText().isEmpty() || description.getText().isEmpty() || type.getValue() == null || Employees.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

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

                description.getText(),
                type.getValue(),
                SessionManager.extractuserfromsession().getIdUser(),
                selectedEmployeeId
        );
        System.out.println(reclamation);
        try {
            serviceReclamation.ajouter(reclamation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation ajoutée avec succès !");
            clearFields();
            Router r = new Router();
            r.navigate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter la réclamation.");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        titre.clear();
        description.clear();
        type.getSelectionModel().clearSelection();
        Employees.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}