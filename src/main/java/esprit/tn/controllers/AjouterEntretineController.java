package esprit.tn.controllers;

import esprit.tn.entities.Entretien;
import esprit.tn.entities.OffreEmploi;
import esprit.tn.entities.TypeEntretien;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceOffre;
import esprit.tn.services.ServiceUser;
import esprit.tn.services.WhatsAppService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import esprit.tn.services.EntretienService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class AjouterEntretineController {

    @FXML
    private ComboBox<String> cb_type_entretien;
    @FXML
    private TextField tf_titre;
    @FXML
    private DatePicker dp_date_entretien;
    @FXML
    private TextField tf_description;
    @FXML
    private Button btnAjouter;
    @FXML
    private Spinner<Integer> sp_heure_entretien;

    @FXML
    private ComboBox cb_candidat;
    @FXML
    private ComboBox cb_employe;
    @FXML
    private ComboBox cb_offre;

    private EntretienService entretienService = new EntretienService();
    private ServiceUser userservice = new ServiceUser();
    private ServiceOffre offreService = new ServiceOffre();
    private WhatsAppService whatsappService = new WhatsAppService();






    @FXML
    public void initialize() {
        cb_type_entretien.getItems().addAll("EN_PRESENTIEL", "EN_VISIO");
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        sp_heure_entretien.setValueFactory(valueFactory);
        cb_candidat.setDisable(true);
        loadEmployes();
        loadOffres();

        cb_offre.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cb_candidat.setDisable(false); // Enable the candidat ComboBox
                loadCandidatsByOffre((Integer) extractIdFromComboBox(newValue.toString()));
            } else {
                cb_candidat.setDisable(true);
            }
        });
    }


    private void loadEmployes() {
        try {
            List<User> employes = userservice.getUsersByRoleEmployee();
            for (User employe : employes) {
                cb_employe.getItems().add( employe.getIdUser() + " - " + employe.getNom());
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des employés.");
        }
    }

    private void loadCandidats() {
        try {
            List<User> candidats = userservice.getUsersByRoleCandidat();
            for (User candidat : candidats) {
                cb_candidat.getItems().add(candidat.getIdUser() + " - " + candidat.getNom());
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des candidatures.");
        }
    }

    private void loadOffres() {
        try {
            List<OffreEmploi> offres = offreService.afficher();
            for (OffreEmploi offre : offres) {
                cb_offre.getItems().add( offre.getIdOffre() + " - " + offre.getTitre());
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des offres.");
        }  }


//hh
    @FXML
    public void ajouterEntretien(ActionEvent actionEvent) throws SQLException {
        try {
            String titre = tf_titre.getText();
            String description = tf_description.getText();
            LocalDate date = dp_date_entretien.getValue();
            int heure = sp_heure_entretien.getValue();
            String typeString = cb_type_entretien.getValue();

            if (titre.isEmpty() || description.isEmpty() || date == null || typeString == null || cb_employe.getValue() == null || cb_candidat.getValue() == null || cb_offre.getValue() == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            if (titre.length() < 5) {
                showAlert("Erreur", "Le titre doit contenir au moins 5 caractères.");
                return;
            }

            if (description.length() < 10) {
                showAlert("Erreur", "La description doit contenir au moins 10 caractères.");
                return;
            }

            if (date.isBefore(LocalDate.now())) {
                showAlert("Erreur", "La date doit être supérieure ou égale à aujourd'hui.");
                return;
            }


            int employeId = extractIdFromComboBox((String) cb_employe.getValue());
            int candidatId = extractIdFromComboBox((String) cb_candidat.getValue());
            int offreId = extractIdFromComboBox((String) cb_offre.getValue());


            TypeEntretien type = TypeEntretien.valueOf(typeString);
            Date dateEntretien = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Time heureEntretien = Time.valueOf(LocalTime.of(heure, 0));

            Entretien entretien = new Entretien(titre, description, dateEntretien, heureEntretien, type, false, candidatId, employeId, offreId , 0);
            entretienService.ajouterEntretienAvecCandidature(entretien , offreId);


            User candidat = userservice.findbyid(candidatId);
            User employe = userservice.findbyid(employeId);

            String messageCandidat = "📢 Cher(e) " + candidat.getNom() + ",\n\n"
                    + "📅 Nous avons le plaisir de vous informer que votre candidature pour le poste de **" + entretien.getTitre() + "** chez **[WorkSphere]** a été retenue. 🎉\n\n"
                    + "🗓 **Date de l’entretien :** " + entretien.getDate_entretien() + "\n"
                    + "🕒 **Heure :** " + entretien.getHeure_entretien() + "\n"
                    + "👨‍💼 **Interviewer :** " + employe.getNom() + "\n\n"
                    + "✅ Nous vous recommandons d’être ponctuel et bien préparé pour maximiser vos chances de succès.\n\n"
                    + "📩 Pour toute question, n’hésitez pas à nous contacter.\n\n"
                    + "Cordialement,\n"
                    + "📍 **L’équipe WorkSphere**";

            String messageEmploye = "📢 Bonjour " + employe.getNom() + ",\n\n"
                    + "👥 Un entretien a été programmé avec **" + candidat.getNom() + "** pour le poste de **" + entretien.getTitre() + "**.\n\n"
                    + "🗓 **Date :** " + entretien.getDate_entretien() + "\n"
                    + "🕒 **Heure :** " + entretien.getHeure_entretien() + "\n"
                    + "⚡ Veuillez préparer les questions et documents nécessaires avant l’entretien.\n\n"
                    + "🔔 Un rappel vous sera envoyé avant l’entretien.\n\n"
                    + "Cordialement,\n"
                    + "📍 **L’équipe WorkSphere**";


            String numeroCandidat = "+216" + candidat.getTelephone().trim();
            String numeroEmployee = "+216" + employe.getTelephone().trim();





            WhatsAppService.sendWhatsAppMessage(numeroCandidat, messageCandidat);
            WhatsAppService.sendWhatsAppMessage(numeroEmployee, messageEmploye);







            clearFields();

            refreshAffichageEntretien();


        } catch (SQLException e) {
            showAlert("Erreur SQL", "Erreur lors de l'ajout : " + e.getMessage());
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur s'est produite : " + e.getMessage());
        }
    }

    private void clearFields() {
        tf_titre.clear();
        tf_description.clear();
        dp_date_entretien.setValue(null);
        cb_type_entretien.setValue(null);
        cb_candidat.setValue(null);
        cb_employe.setValue(null);
        cb_offre.setValue(null);
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

//    @FXML
//    private void fermerFenetre(ActionEvent actionEvent) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
//            Parent root = loader.load();
//            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (Exception e) {
//            showAlert("Erreur", "Impossible de fermer la fenêtre.");
//        }
//    }


    @FXML
    private void fermerFenetre(ActionEvent actionEvent) {
        try {


            refreshAffichageEntretien();

        } catch (Exception e) {
            showAlert("Erreur", "Impossible de fermer la fenêtre.");
        }
    }

    private void ouvrirAffichageEntretien(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
            Parent root1 = loader.load();
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            stage.getScene().setRoot(root1);
            stage.setTitle("Liste des Entretiens");
            stage.show();
        } catch (IOException e) {
        }
    }


    public void Onback(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
        }
    }



    private int extractIdFromComboBox(String value) {
        try {
            if (value != null && value.contains(" - ")) {
                return Integer.parseInt(value.split(" - ")[0].trim());
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format incorrect pour : " + value);
        }
        return -1;
    }



    private void loadCandidatsByOffre(int offreId) {
        List<User> candidats = entretienService.getAllCandidatsSansEntretien(offreId);

        cb_candidat.getItems().clear();

        for (User candidat : candidats) {
            cb_candidat.getItems().add(candidat.getIdUser() + " - " + candidat.getNom());
        }
    }


    private void refreshAffichageEntretien() {
        try {
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
            Parent root = loader.load();
            AffichageEntretineController controller = loader.getController();
            controller.initialize();
//            Stage stage = (Stage) cb_type_entretien.getScene().getWindow();
//            stage.getScene().setRoot(root);
//            stage.setTitle("Liste des Entretiens");
//            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de rafraîchir l'affichage : " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }





























//    @FXML
//    public void ajouterEntretien(ActionEvent actionEvent) throws SQLException {
//        try {
//            String titre = tf_titre.getText();
//            String description = tf_description.getText();
//            LocalDate date = dp_date_entretien.getValue();
//            int heure = (int) sp_heure_entretien.getValue();
//            String typeString = (String) cb_type_entretien.getValue();
//            boolean status = cb_status.isSelected();
//            String titrev = tf_titre.getText().trim();
//            String descriptionv = tf_description.getText().trim();
//
//            if (titre.isEmpty() || description.isEmpty() || date == null || typeString == null || typeString.isEmpty()) {
//                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
//                return;
//            }
//
//            if (titre.length() < 3) {
//                showAlert("Erreur", "Le titre doit contenir au moins 3 caractères.");
//                return;
//            }
//
//            if (description.length() < 5) {
//                showAlert("Erreur", "La description doit contenir au moins 5 caractères.");
//                return;
//            }
//
//            TypeEntretien type;
//            try {
//                type = TypeEntretien.valueOf(typeString);
//            } catch (IllegalArgumentException e) {
//                showAlert("Erreur", "Type d'entretien invalide.");
//                return;
//            }
//
//            Date dateEntretien = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
//            Time heureEntretien = Time.valueOf(LocalTime.of(heure, 0));
//
//            Entretien entretien = new Entretien(titre, description, dateEntretien, heureEntretien, type, status);
//            entretienService.ajouter(entretien);
//
//            clearFields();
//            fermerFenetre(actionEvent);
//            ouvrirAffichageEntretien();
//
//        } catch (SQLException e) {
//            System.err.println("SQL Error: " + e.getMessage());
//            showAlert("Erreur SQL", "Erreur lors de l'ajout : " + e.getMessage());
//        } catch (Exception e) {
//            System.err.println("Unexpected Error: " + e.getMessage());
//            showAlert("Erreur Inattendue", "Une erreur inattendue s'est produite : " + e.getMessage());
//        }
//    }
//
//    private void clearFields() {
//        tf_titre.clear();
//        tf_description.clear();
//        dp_date_entretien.setValue(null);
//        cb_type_entretien.setValue(null);
//        cb_status.setSelected(false);
//    }
//
//    private void showAlert(String title, String message) {
//        Platform.runLater(() -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle(title);
//            alert.setContentText(message);
//            alert.showAndWait();
//        });
//    }
//
//    @FXML
//    private void fermerFenetre(ActionEvent actionEvent) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
//            Parent root = loader.load();
//            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (Exception e) {
//            showAlert("Erreur", "Impossible de fermer la fenêtre.");
//        }
//    }
//
//    private void ouvrirAffichageEntretien() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
//            Parent root = loader.load();
//
//            AffichageEntretineController controller = loader.getController();
//            controller.initialize();
//
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Liste des Entretiens");
//            stage.show();
//
//        } catch (IOException | SQLException e) {
//            showAlert("Erreur", "Impossible d'ouvrir l'affichage : " + e.getMessage());
//        }
//    }



}
