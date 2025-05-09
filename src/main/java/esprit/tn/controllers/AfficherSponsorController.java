package esprit.tn.controllers;

import esprit.tn.entities.Classement;
import esprit.tn.entities.Sponsor;
import esprit.tn.services.ServiceEvenement;
import esprit.tn.services.ServiceSponsor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static esprit.tn.entities.Classement.*;
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
    private ComboBox<String> cbBudgetFilter;
    @FXML
    private Button btnResetFilter;

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
                        String eventsTextString = String.join(", ", eventNames); // Renamed to eventsTextString

                        // Créer un texte pour afficher les informations du sponsor
                        Text sponsorText = new Text("\uD83D\uDCB0 " + sponsor.getNomSponso() + " " + sponsor.getPrenomSponso() );

                        // Créer un Label pour la ligne "Type: " avec une couleur de fond spécifique
                        Label typeLabel = new Label("Type: " + sponsor.getClassement());

                        // Définir la couleur de fond en fonction du classement
                        switch (sponsor.getClassement()) {
                            case Or:
                                typeLabel.setStyle("-fx-background-color: gold; -fx-padding: 5px; -fx-background-radius: 15"); // Or = gold
                                break;
                            case Argent:
                                typeLabel.setStyle("-fx-background-color: silver; -fx-padding: 5px; -fx-background-radius: 10"); // Argent = silver
                                break;
                            case Bronze:
                                typeLabel.setStyle("-fx-background-color: #cd7f32; -fx-padding: 5px; -fx-background-radius: 10"); // Bronze = bronze
                                break;
                            default:
                                typeLabel.setStyle("-fx-background-color: silver; -fx-padding: 5px; -fx-background-radius: 10;"); // Par défaut = gris clair
                                break;
                        }

                        // Continuer avec le reste du texte
                        Text emailText = new Text("   Email: " + sponsor.getEmailSponso() );
                        Text secteurText = new Text("   Secteur: " + sponsor.getSecteurSponsor());

                        Text budgetInitialText = new Text("   Budget initial: " + sponsor.getBudgetSponso() );
                        Text budgetApresReductionText = new Text("   Budget après réduction: " + sponsor.getBudgetApresReduction() + "\n");
                        Text eventsText = new Text("\n \uD83D\uDCC5 Événements associés : \n" + (eventsTextString.isEmpty() ? "Aucun événement" : eventsTextString)); // Use eventsTextString here
                        eventsText.setWrappingWidth(500);
                        // Ajouter tous les éléments dans un VBox
                        VBox textVBox = new VBox(5,
                                sponsorText,
                                typeLabel,
                                emailText,
                                secteurText, // ajouté ici
                                budgetInitialText,
                                budgetApresReductionText,
                                eventsText
                        );
                        textVBox.getStyleClass().add("sponsor-text");
                        textVBox.setStyle("-fx-font-weight: normal;");

                        // Boutons pour modifier et supprimer
                        Button btnModifier = new Button("\uD83D\uDCDD");
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

                        Button btnSupprimer = new Button("❌");
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

                        // Ajouter les boutons dans un HBox
                        HBox hboxButtons = new HBox(10, btnModifier, btnSupprimer, btnSupprimerAssociation, btnAssocierEvenement);
                        hboxButtons.getStyleClass().add("hbox-buttons");

                        // Ajouter le texte et les boutons dans un VBox
                        VBox vbox = new VBox(10, textVBox, hboxButtons);
                        vbox.getStyleClass().add("vbox-item");
                        setGraphic(vbox);
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

   /*private void reloadSponsorList() {
        try {
            List<Sponsor> sponsors = serviceSponsor.afficher();
            ObservableList<Sponsor> observableList = FXCollections.observableList(sponsors);
            lv_sponsor.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
   private void reloadSponsorList() {
       try {
           List<Sponsor> sponsors = serviceSponsor.afficher();

           // Trier les sponsors par budget en ordre décroissant
           sponsors.sort((s1, s2) -> Double.compare(s2.getBudgetSponso(), s1.getBudgetSponso()));

           // Déterminer le classement des sponsors
           determinerClassement(sponsors);

           // Appliquer les réductions sur le budget
           for (Sponsor sponsor : sponsors) {
               double budgetInitial = sponsor.getBudgetSponso();
               double budgetApresReduction = budgetInitial;

               if (sponsor.getClassement() == Classement.Or) {
                   budgetApresReduction = budgetInitial * 0.90; // Réduction de 10%
               } else if (sponsor.getClassement() == Classement.Argent) {
                   budgetApresReduction = budgetInitial * 0.95; // Réduction de 5%
               }

               sponsor.setBudgetApresReduction(budgetApresReduction);
               serviceSponsor.mettreAJourBudgetApresReduction(sponsor.getIdSponsor(), budgetApresReduction);
           }

           ObservableList<Sponsor> observableList = FXCollections.observableList(sponsors);
           lv_sponsor.setItems(observableList);
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }


    private void determinerClassement(List<Sponsor> sponsors) throws SQLException {
        sponsors.sort((s1, s2) -> Double.compare(s2.getBudgetSponso(), s1.getBudgetSponso()));

        int totalSponsors = sponsors.size();
        int nbOr = Math.max(1, (int) Math.ceil(totalSponsors * 0.01)); // Au moins 1 sponsor en Or
        int nbArgent = Math.max(1, (int) Math.ceil(totalSponsors * 0.02)); // Au moins 1 sponsor en Argent

        for (int i = 0; i < totalSponsors; i++) {
            Sponsor sponsor = sponsors.get(i);
            Classement classement = Classement.Bronze; // Par défaut

            if (i < nbOr) {
                classement = Classement.Or;
            } else if (i < nbOr + nbArgent) { // Les suivants sont en Argent
                classement = Classement.Argent;
            }

            sponsor.setClassement(classement);
            serviceSponsor.mettreAJourClassement(sponsor.getIdSponsor(), classement);
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
        System.out.println("Début de la suppression d'association pour le sponsor : " + sponsor.getIdSponsor());

        // Récupérer la liste des événements associés au sponsor
        List<String> eventNames = serviceSponsor.getEventNamesBySponsor(sponsor.getIdSponsor());
        System.out.println("Événements associés trouvés : " + eventNames);

        // Vérifier s'il y a des événements associés
        if (eventNames.isEmpty()) {
            System.out.println("Aucun événement trouvé pour ce sponsor.");
            showAlert("Aucun événement associé", "Ce sponsor n'est associé à aucun événement.");
            return;
        }

        // Créer une boîte de dialogue pour choisir l'événement à dissocier
        ChoiceDialog<String> dialog = new ChoiceDialog<>(eventNames.get(0), eventNames);
        dialog.setTitle("Suppression d'association");
        dialog.setHeaderText("Choisissez l'événement à dissocier du sponsor :");
        dialog.setContentText("Événement :");

        // Afficher la boîte de dialogue et attendre la réponse de l'utilisateur
        Optional<String> result = dialog.showAndWait();

        // Si l'utilisateur a choisi un événement
        if (result.isPresent()) {
            String selectedEventName = result.get();
            System.out.println("Événement sélectionné : " + selectedEventName);

            // Extraire uniquement le nom de l'événement (sans les informations supplémentaires)
            String eventNameOnly = selectedEventName.split(" \\(")[0]; // Extrait "Université de Tunis El Manar" de "Université de Tunis El Manar (Début: 2025-03-03, Durée: unAns)"
            System.out.println("Nom de l'événement extrait : " + eventNameOnly);

            // Récupérer l'ID de l'événement à partir de son nom
            Integer eventId = serviceEvenement.getEventIdByName(eventNameOnly);
            System.out.println("ID de l'événement récupéré : " + eventId);

            // Vérifier si l'ID de l'événement a été trouvé
            if (eventId == null) {
                System.out.println("Erreur : L'ID de l'événement est null !");
                showAlert("Erreur", "Impossible de trouver l'événement sélectionné.");
                return;
            }

            // Supprimer l'association entre le sponsor et l'événement
            serviceSponsor.removeEventFromSponsor(sponsor.getIdSponsor(), eventId);
            System.out.println("Association supprimée avec succès !");

            // Recharger la liste des sponsors pour afficher les modifications
            reloadSponsorList();
        } else {
            // L'utilisateur a annulé la suppression
            System.out.println("Suppression annulée par l'utilisateur.");
        }
    }
    // Méthode pour associer un sponsor à un événement

    private void associerEvenement(Sponsor sponsor) throws SQLException {
        // Récupérer la liste des événements disponibles
        List<String> eventNames = serviceEvenement.getEventNames();

        if (eventNames.isEmpty()) {
            showAlert("Aucun événement disponible", "Il n'y a aucun événement à associer.");
            return;
        }

        // Créer une nouvelle fenêtre (popup)
        Stage popupStage = new Stage();
        popupStage.setTitle("Associer un événement");

        // Créer un conteneur pour la popup
        VBox popupLayout = new VBox(10);
        popupLayout.setPadding(new Insets(10));

        // ComboBox pour sélectionner l'événement
        ComboBox<String> cbEvent = new ComboBox<>(FXCollections.observableArrayList(eventNames));
        cbEvent.setPromptText("Sélectionnez un événement");

        // DatePicker pour choisir la date de début du contrat
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Date de début du contrat");

        // ComboBox pour choisir la durée du contrat
        ComboBox<String> cbDuree = new ComboBox<>(FXCollections.observableArrayList("troisMois", "sixMois", "unAns"));
        cbDuree.setPromptText("Choisissez la durée");

        // Label pour afficher les erreurs
        Label errorLabel = new Label();
        errorLabel.setTextFill(javafx.scene.paint.Color.RED);

        // Bouton pour valider l'association
        Button btnValider = new Button("Valider");
        btnValider.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        // Gérer le clic sur le bouton Valider
        btnValider.setOnAction(event -> {
            // Réinitialiser le message d'erreur
            errorLabel.setText("");

            // Vérifier que tous les champs sont remplis
            if (cbEvent.getValue() == null || datePicker.getValue() == null || cbDuree.getValue() == null) {
                errorLabel.setText("Veuillez remplir tous les champs.");
                return;
            }

            // Vérifier que la date de début est supérieure à la date d'aujourd'hui
            if (datePicker.getValue().isBefore(java.time.LocalDate.now())) {
                errorLabel.setText("La date de début doit être supérieure à la date d'aujourd'hui.");
                return;
            }

            try {
                // Récupérer les valeurs saisies
                String selectedEventName = cbEvent.getValue();
                Date datedebutContrat = Date.valueOf(datePicker.getValue()); // Convertir LocalDate en java.sql.Date
                String duree = cbDuree.getValue();

                // Récupérer l'ID de l'événement sélectionné
                int eventId = serviceEvenement.getEventIdByName(selectedEventName);

                // Vérifier si l'événement est déjà associé au sponsor
                boolean isAlreadyAssociated = serviceSponsor.isEventAssociatedWithSponsor(sponsor.getIdSponsor(), eventId);

                if (isAlreadyAssociated) {
                    // Mettre à jour l'association existante
                    serviceSponsor.updateAssociation(sponsor.getIdSponsor(), eventId, datedebutContrat, duree);
                    showAlert("Mise à jour réussie", "L'association a été mise à jour avec succès.");
                } else {
                    // Ajouter une nouvelle association
                    serviceSponsor.ajouterEvenementASponsor(sponsor.getIdSponsor(), eventId, datedebutContrat, duree);
                    showAlert("Association réussie", "L'événement a été associé au sponsor avec succès.");
                }

                // Recharger la liste des sponsors
                reloadSponsorList();

                // Fermer la popup
                popupStage.close();
            } catch (SQLException e) {
                showAlert("Erreur", "Une erreur est survenue : " + e.getMessage());
            }
        });

        // Ajouter les éléments à la popup
        popupLayout.getChildren().addAll(
                new Label("Événement :"), cbEvent,
                new Label("Date de début du contrat :"), datePicker,
                new Label("Durée du contrat :"), cbDuree,
                btnValider,
                errorLabel
        );

        // Configurer la scène et afficher la popup
        Scene popupScene = new Scene(popupLayout, 300, 250);
        popupStage.setScene(popupScene);
        popupStage.initModality(Modality.APPLICATION_MODAL); // Bloquer l'interaction avec la fenêtre principale
        popupStage.showAndWait(); // Attendre que la popup se ferme
    }

}