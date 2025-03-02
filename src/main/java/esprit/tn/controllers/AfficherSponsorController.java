/*
    package esprit.tn.controllers;

    import esprit.tn.entities.Sponsor;
    import esprit.tn.services.ServiceEvenement;
    import esprit.tn.services.ServiceSponsor;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.scene.layout.HBox;
    import javafx.scene.text.Text;
    import javafx.stage.Modality;
    import javafx.stage.Stage;

    import java.io.IOException;
    import java.sql.SQLException;
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    import static esprit.tn.utils.Router.showAlert;

    public class AfficherSponsorController {
        private ServiceSponsor serviceSponsor;
        private ServiceEvenement serviceEvenement;
        public AfficherSponsorController() {
            serviceSponsor = new ServiceSponsor();
            serviceEvenement = new ServiceEvenement();
        }


        @FXML private Button deleteAssociationButton;
        @FXML
        private ListView<Sponsor> lv_sponsor;
        @FXML
        private TextField txtRechercheSponso;

        @FXML
        void initialize() {
            ServiceSponsor serviceSponsor = new ServiceSponsor();
            try {
                ObservableList<Sponsor> observableList = FXCollections.observableList(serviceSponsor.afficher());
                lv_sponsor.setItems(observableList);

                lv_sponsor.setCellFactory(param -> new ListCell<Sponsor>() {

                    @Override
                    protected void updateItem(Sponsor sponsor, boolean empty) {
                        super.updateItem(sponsor, empty);
                        if (empty || sponsor == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            try {
                                // Récupérer les événements associés au sponsor
                                List<String> eventNames = serviceSponsor.getEventNamesBySponsor(sponsor.getIdSponsor());
                                String eventsText = String.join(", ", eventNames); // Concaténer les événements en une seule chaîne

                                // Créer un texte pour afficher les informations du sponsor et ses événements
                                Text sponsorText = new Text(sponsor.getNomSponso() + " " + sponsor.getPrenomSponso() + " - " +
                                        sponsor.getEmailSponso() + " - Budget: " + sponsor.getBudgetSponso() +
                                        "\nÉvénements associés : " + (eventsText.isEmpty() ? "Aucun événement" : eventsText));
                                sponsorText.getStyleClass().add("sponsor-text");

                                // Boutons pour modifier et supprimer
                                Button btnModifier = new Button("Modifier");
                                btnModifier.getStyleClass().add("btn-modifier");
                                btnModifier.setStyle("-fx-background-color: #ffc400; -fx-text-fill: white;");

                                btnModifier.setOnAction(event -> {
                                    // Ouvrir la fenêtre de modification
                                    try {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierSponsor.fxml"));
                                        Parent root = loader.load();

                                        ModifierSponsorController controller = loader.getController();
                                        controller.setSponsor(sponsor);  // Passer le sponsor à modifier

                                        Stage popupStage = new Stage();
                                        popupStage.setTitle("Modifier Sponsor");
                                        popupStage.setScene(new Scene(root));
                                        popupStage.initModality(Modality.APPLICATION_MODAL); // Empêcher l'interaction avec la fenêtre principale
                                        popupStage.showAndWait();  // Attendre que la fenêtre popup se ferme

                                        // Recharger la liste des sponsors après modification
                                        reloadSponsorList();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });

                                Button btnSupprimer = new Button("Supprimer");
                                btnSupprimer.getStyleClass().add("btn-supprimer");
                                btnSupprimer.setStyle("-fx-background-color: red; -fx-text-fill: white;");

                                btnSupprimer.setOnAction(event -> {
                                    try {
                                        // Confirmer la suppression
                                        Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                                        alertConfirmation.setTitle("Confirmation");
                                        alertConfirmation.setHeaderText("Voulez-vous vraiment supprimer ce sponsor ?");
                                        alertConfirmation.setContentText("Cette action est irréversible.");

                                        if (alertConfirmation.showAndWait().get() == ButtonType.OK) {
                                            serviceSponsor.supprimer(sponsor.getIdSponsor());
                                            observableList.remove(sponsor); // Mettre à jour la liste des sponsors

                                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                            alert.setTitle("Succès");
                                            alert.setHeaderText("Suppression réussie");
                                            alert.setContentText("Le sponsor a été supprimé avec succès.");
                                            alert.showAndWait();

                                            reloadSponsorList();

                                        }
                                    } catch (SQLException e) {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Erreur");
                                        alert.setHeaderText("Erreur lors de la suppression du sponsor");
                                        alert.setContentText("Une erreur est survenue : " + e.getMessage());
                                        alert.showAndWait();
                                    }
                                });

                                // Bouton Supprimer Association
                                Button btnSupprimerAssociation = new Button("Supprimer Association");
                                btnSupprimerAssociation.getStyleClass().add("btn-supprimer-association");
                                btnSupprimerAssociation.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;");

                                btnSupprimerAssociation.setOnAction(event -> {
                                    try {
                                        removeSponsorFromEvent(sponsor);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                });

                                // Bouton Associer Événement
                                Button btnAssocierEvenement = new Button("Associer Événement");
                                btnAssocierEvenement.getStyleClass().add("btn-associer-evenement");
                                btnAssocierEvenement.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

                                btnAssocierEvenement.setOnAction(event -> {
                                    try {
                                        associerEvenement(sponsor);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                });

                                // Ajouter les éléments dans un HBox
                                HBox hbox = new HBox(10, sponsorText, btnModifier, btnSupprimer, btnSupprimerAssociation, btnAssocierEvenement);
                                hbox.getStyleClass().add("hbox-item");
                                setGraphic(hbox);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @FXML
        public void Onajoutersponso(ActionEvent actionEvent) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterSponsor.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Ajouter un Sponsor");
                stage.setScene(new Scene(root));
                stage.showAndWait();  // Attendre que la fenêtre popup se ferme

                reloadSponsorList();  // Recharger la liste des sponsors après l'ajout
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        private void reloadSponsorList() {
            ServiceSponsor serviceSponsor = new ServiceSponsor();
            try {
                List<Sponsor> sponsors = serviceSponsor.afficher();
                ObservableList<Sponsor> observableList = FXCollections.observableList(sponsors);
                lv_sponsor.setItems(observableList);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @FXML
        public void OnchercherSponsor(ActionEvent actionEvent) {
            ServiceSponsor serviceSponsor = new ServiceSponsor();
            try {
                List<Sponsor> sponsors = serviceSponsor.afficher();
                String searchQuery = txtRechercheSponso.getText().toLowerCase();
                List<Sponsor> filteredSponsors = sponsors.stream()
                        .filter(sponsor -> sponsor.getNomSponso().toLowerCase().contains(searchQuery) ||
                                sponsor.getPrenomSponso().toLowerCase().contains(searchQuery) ||
                                sponsor.getEmailSponso().toLowerCase().contains(searchQuery))
                        .collect(Collectors.toList());

                ObservableList<Sponsor> observableList = FXCollections.observableList(filteredSponsors);
                lv_sponsor.setItems(observableList);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        private void removeSponsorFromEvent(Sponsor sponsor) throws SQLException {
            // Récupérer les événements associés au sponsor
            List<String> eventNames = serviceSponsor.getEventNamesBySponsor(sponsor.getIdSponsor());

            if (eventNames.isEmpty()) {
                showAlert("Aucun événement associé", "Ce sponsor n'est associé à aucun événement.");
                return;
            }

            // Afficher une boîte de dialogue pour choisir l'événement à dissocier
            ChoiceDialog<String> dialog = new ChoiceDialog<>(eventNames.get(0), eventNames);
            dialog.setTitle("Suppression d'association");
            dialog.setHeaderText("Choisissez l'événement à dissocier du sponsor :");
            dialog.setContentText("Événement :");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                String selectedEventName = result.get();
                int eventId = serviceEvenement.getEventIdByName(selectedEventName);

                // Supprimer l'association
                serviceSponsor.removeEventFromSponsor(sponsor.getIdSponsor(), eventId);

                // Recharger la liste des sponsors
                reloadSponsorList();
            }
        }

        private void associerEvenement(Sponsor sponsor) throws SQLException {
            // Récupérer la liste des événements disponibles
            List<String> eventNames = serviceEvenement.getEventNames();

            if (eventNames.isEmpty()) {
                showAlert("Aucun événement disponible", "Il n'y a aucun événement à associer.");
                return;
            }

            // Afficher une boîte de dialogue pour choisir l'événement à associer
            ChoiceDialog<String> dialog = new ChoiceDialog<>(eventNames.get(0), eventNames);
            dialog.setTitle("Association d'événement");
            dialog.setHeaderText("Choisissez l'événement à associer au sponsor :");
            dialog.setContentText("Événement :");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                String selectedEventName = result.get();
                int eventId = serviceEvenement.getEventIdByName(selectedEventName);

                // Associer l'événement au sponsor
                serviceSponsor.ajouterEvenementASponsor(sponsor.getIdSponsor(), eventId);

                // Recharger la liste des sponsors
                reloadSponsorList();
            }
        }


    }*/
package esprit.tn.controllers;

import esprit.tn.entities.Sponsor;
import esprit.tn.services.ServiceEvenement;
import esprit.tn.services.ServiceSponsor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static esprit.tn.utils.Router.showAlert;

public class AfficherSponsorController {
    private ServiceSponsor serviceSponsor;
    private ServiceEvenement serviceEvenement;

    public AfficherSponsorController() {
        serviceSponsor = new ServiceSponsor();
        serviceEvenement = new ServiceEvenement();
    }

    @FXML
    private Button deleteAssociationButton;
    @FXML
    private ListView<Sponsor> lv_sponsor;
    @FXML
    private TextField txtRechercheSponso;
    @FXML
    private ComboBox<String> cbBudgetFilter; // ComboBox pour le filtrage par budget
    @FXML
    private Button btnResetFilter; // Bouton pour réinitialiser le filtre

    @FXML
    void initialize() {
        // Initialiser le ComboBox avec les options de filtrage
        ObservableList<String> budgetFilters = FXCollections.observableArrayList(
                "Tous", "Budget < 5000", "Budget 5000-30000", "Budget > 30000"
        );
        cbBudgetFilter.setItems(budgetFilters);
        cbBudgetFilter.setValue("Tous"); // Valeur par défaut

        // Gérer la sélection dans le ComboBox
        cbBudgetFilter.setOnAction(this::onBudgetFilterSelected);

        // Gérer le clic sur le bouton de réinitialisation
        btnResetFilter.setOnAction(this::onResetFilter);

        // Charger la liste des sponsors
        reloadSponsorList();

        // Configurer la ListView
        lv_sponsor.setCellFactory(param -> new ListCell<Sponsor>() {
            @Override
            protected void updateItem(Sponsor sponsor, boolean empty) {
                super.updateItem(sponsor, empty);
                if (empty || sponsor == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        // Récupérer les événements associés au sponsor
                        List<String> eventNames = serviceSponsor.getEventNamesBySponsor(sponsor.getIdSponsor());
                        String eventsText = String.join(", ", eventNames);

                        // Créer un texte pour afficher les informations du sponsor
                        Text sponsorText = new Text(sponsor.getNomSponso() + " " + sponsor.getPrenomSponso() + " - " +
                                sponsor.getEmailSponso() + " - Budget: " + sponsor.getBudgetSponso() +
                                "\nÉvénements associés : " + (eventsText.isEmpty() ? "Aucun événement" : eventsText));
                        sponsorText.getStyleClass().add("sponsor-text");

                        // Boutons pour modifier et supprimer
                        Button btnModifier = new Button("Modifier");
                        btnModifier.getStyleClass().add("btn-modifier");
                        btnModifier.setStyle("-fx-background-color: #ffc400; -fx-text-fill: white;");

                        btnModifier.setOnAction(event -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierSponsor.fxml"));
                                Parent root = loader.load();

                                ModifierSponsorController controller = loader.getController();
                                controller.setSponsor(sponsor);

                                Stage popupStage = new Stage();
                                popupStage.setTitle("Modifier Sponsor");
                                popupStage.setScene(new Scene(root));
                                popupStage.initModality(Modality.APPLICATION_MODAL);
                                popupStage.showAndWait();

                                reloadSponsorList();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        Button btnSupprimer = new Button("Supprimer");
                        btnSupprimer.getStyleClass().add("btn-supprimer");
                        btnSupprimer.setStyle("-fx-background-color: red; -fx-text-fill: white;");

                        btnSupprimer.setOnAction(event -> {
                            try {
                                Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                                alertConfirmation.setTitle("Confirmation");
                                alertConfirmation.setHeaderText("Voulez-vous vraiment supprimer ce sponsor ?");
                                alertConfirmation.setContentText("Cette action est irréversible.");

                                if (alertConfirmation.showAndWait().get() == ButtonType.OK) {
                                    serviceSponsor.supprimer(sponsor.getIdSponsor());
                                    reloadSponsorList();

                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Succès");
                                    alert.setHeaderText("Suppression réussie");
                                    alert.setContentText("Le sponsor a été supprimé avec succès.");
                                    alert.showAndWait();
                                }
                            } catch (SQLException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Erreur");
                                alert.setHeaderText("Erreur lors de la suppression du sponsor");
                                alert.setContentText("Une erreur est survenue : " + e.getMessage());
                                alert.showAndWait();
                            }
                        });

                        // Bouton Supprimer Association
                        Button btnSupprimerAssociation = new Button("Supprimer Association");
                        btnSupprimerAssociation.getStyleClass().add("btn-supprimer-association");
                        btnSupprimerAssociation.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;");

                        btnSupprimerAssociation.setOnAction(event -> {
                            try {
                                removeSponsorFromEvent(sponsor);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });

                        // Bouton Associer Événement
                        Button btnAssocierEvenement = new Button("Associer Événement");
                        btnAssocierEvenement.getStyleClass().add("btn-associer-evenement");
                        btnAssocierEvenement.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

                        btnAssocierEvenement.setOnAction(event -> {
                            try {
                                associerEvenement(sponsor);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });

                        // Ajouter les éléments dans un HBox
                        HBox hbox = new HBox(10, sponsorText, btnModifier, btnSupprimer, btnSupprimerAssociation, btnAssocierEvenement);
                        hbox.getStyleClass().add("hbox-item");
                        setGraphic(hbox);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Méthode pour filtrer les sponsors par budget
    @FXML
    private void onBudgetFilterSelected(ActionEvent event) {
        String selectedFilter = cbBudgetFilter.getValue();
        if (selectedFilter != null) {
            switch (selectedFilter) {
                case "Budget < 5000":
                    filterSponsorsByBudget(0, 5000);
                    break;
                case "Budget 5000-30000":
                    filterSponsorsByBudget(5000, 30000);
                    break;
                case "Budget > 30000":
                    filterSponsorsByBudget(30000, Integer.MAX_VALUE);
                    break;
                default:
                    reloadSponsorList(); // Afficher tous les sponsors
                    break;
            }
        }
    }

    // Méthode pour réinitialiser le filtre
    @FXML
    private void onResetFilter(ActionEvent event) {
        cbBudgetFilter.setValue("Tous"); // Réinitialiser la sélection du ComboBox
        reloadSponsorList(); // Recharger la liste complète des sponsors
    }

    // Méthode pour filtrer les sponsors par budget
    private void filterSponsorsByBudget(int minBudget, int maxBudget) {
        try {
            List<Sponsor> sponsors = serviceSponsor.afficher();
            List<Sponsor> filteredSponsors = sponsors.stream()
                    .filter(sponsor -> sponsor.getBudgetSponso() >= minBudget && sponsor.getBudgetSponso() <= maxBudget)
                    .collect(Collectors.toList());

            ObservableList<Sponsor> observableList = FXCollections.observableList(filteredSponsors);
            lv_sponsor.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour recharger la liste des sponsors
    private void reloadSponsorList() {
        try {
            List<Sponsor> sponsors = serviceSponsor.afficher();
            ObservableList<Sponsor> observableList = FXCollections.observableList(sponsors);
            lv_sponsor.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour ajouter un sponsor
    @FXML
    public void Onajoutersponso(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterSponsor.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Sponsor");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Attendre que la fenêtre popup se ferme

            reloadSponsorList(); // Recharger la liste des sponsors après l'ajout
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour rechercher un sponsor
    @FXML
    public void OnchercherSponsor(ActionEvent actionEvent) {
        try {
            List<Sponsor> sponsors = serviceSponsor.afficher();
            String searchQuery = txtRechercheSponso.getText().toLowerCase();
            List<Sponsor> filteredSponsors = sponsors.stream()
                    .filter(sponsor -> sponsor.getNomSponso().toLowerCase().contains(searchQuery) ||
                            sponsor.getPrenomSponso().toLowerCase().contains(searchQuery) ||
                            sponsor.getEmailSponso().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());

            ObservableList<Sponsor> observableList = FXCollections.observableList(filteredSponsors);
            lv_sponsor.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour supprimer l'association d'un sponsor à un événement
    private void removeSponsorFromEvent(Sponsor sponsor) throws SQLException {
        List<String> eventNames = serviceSponsor.getEventNamesBySponsor(sponsor.getIdSponsor());

        if (eventNames.isEmpty()) {
            showAlert("Aucun événement associé", "Ce sponsor n'est associé à aucun événement.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(eventNames.get(0), eventNames);
        dialog.setTitle("Suppression d'association");
        dialog.setHeaderText("Choisissez l'événement à dissocier du sponsor :");
        dialog.setContentText("Événement :");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String selectedEventName = result.get();
            int eventId = serviceEvenement.getEventIdByName(selectedEventName);

            serviceSponsor.removeEventFromSponsor(sponsor.getIdSponsor(), eventId);
            reloadSponsorList();
        }
    }

    // Méthode pour associer un sponsor à un événement
    private void associerEvenement(Sponsor sponsor) throws SQLException {
        List<String> eventNames = serviceEvenement.getEventNames();

        if (eventNames.isEmpty()) {
            showAlert("Aucun événement disponible", "Il n'y a aucun événement à associer.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(eventNames.get(0), eventNames);
        dialog.setTitle("Association d'événement");
        dialog.setHeaderText("Choisissez l'événement à associer au sponsor :");
        dialog.setContentText("Événement :");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String selectedEventName = result.get();
            int eventId = serviceEvenement.getEventIdByName(selectedEventName);

            serviceSponsor.ajouterEvenementASponsor(sponsor.getIdSponsor(), eventId);
            reloadSponsorList();
        }
    }
}