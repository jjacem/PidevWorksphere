package esprit.tn.controllers;

import esprit.tn.entities.Classement;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import esprit.tn.entities.Evenement;
import esprit.tn.entities.EvenementSponsor;
import esprit.tn.entities.Sponsor;
import esprit.tn.services.EventSponsorService;
import esprit.tn.services.ServiceEvenement;
import esprit.tn.services.ServiceSponsor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class EvenementSponsorController {

    @FXML
    private ListView<EvenementSponsor> lv_evenementSponsor;

    @FXML
    private Button btnRefresh;

    private EventSponsorService eventSponsorService = new EventSponsorService();
    private ServiceSponsor serviceSponsor = new ServiceSponsor();
    private ServiceEvenement serviceEvenement = new ServiceEvenement();

    @FXML
    private ComboBox<String> cbDurationFilter; // Added ComboBox
   @FXML
   public void initialize() {
       // Initialize ComboBox
       cbDurationFilter.getItems().addAll("Tous", "troisMois", "sixMois", "unAns");
       cbDurationFilter.setValue("Tous"); // Default value
       cbDurationFilter.setOnAction(event -> filterByDuration());

       loadEvenementSponsorList();
       lv_evenementSponsor.setCellFactory(new Callback<ListView<EvenementSponsor>, ListCell<EvenementSponsor>>() {
           @Override
           public ListCell<EvenementSponsor> call(ListView<EvenementSponsor> param) {
               return new ListCell<EvenementSponsor>() {
                   private final Button btnGeneratePdf = new Button("Générer PDF");
                   private final VBox vbox = new VBox();
                   private final Label label = new Label();

                   {
                       vbox.setSpacing(10);
                       btnGeneratePdf.setOnAction(event -> {
                           EvenementSponsor evenementSponsor = getItem();
                           if (evenementSponsor != null) {
                               try {
                                   generatePdf(evenementSponsor);
                               } catch (SQLException | IOException | DocumentException e) {
                                   e.printStackTrace();
                                   System.out.println("Erreur lors de la génération du PDF: " + e.getMessage());
                               }
                           }
                       });
                       btnGeneratePdf.setPrefWidth(130);
                       btnGeneratePdf.setPrefHeight(10);
                       vbox.getChildren().addAll(label, btnGeneratePdf);
                       btnGeneratePdf.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                   }

                   @Override
                   protected void updateItem(EvenementSponsor evenementSponsor, boolean empty) {
                       super.updateItem(evenementSponsor, empty);
                       if (empty || evenementSponsor == null) {
                           setText(null);
                           setGraphic(null);
                       } else {
                           try {
                               // Récupérer le sponsor et l'événement
                               Sponsor sponsor = serviceSponsor.afficher().stream()
                                       .filter(s -> s.getIdSponsor() == evenementSponsor.getSponsorId())
                                       .findFirst()
                                       .orElse(null);

                               Evenement evenementRecherche = serviceEvenement.afficher().stream()
                                       .filter(e -> e.getIdEvent() == evenementSponsor.getEvenementId())
                                       .findFirst()
                                       .orElse(null);

                               if (sponsor != null && evenementRecherche != null) {
                                   // Afficher le nom de l'événement et le nom complet du sponsor
                                   String displayText = "\uD83D\uDCC5 Événement: " + evenementRecherche.getNomEvent() + "\n"+ "\n"+
                                           "\uD83E\uDD1D Sponsor: " + sponsor.getNomSponso() + " " + sponsor.getPrenomSponso() + "\n"+
                                            "\n"+  "⏳ Durée: " + evenementSponsor.getDuree() + "\n"+ "\n"+
                                           "\uD83D\uDE80 Début du contrat: " + evenementSponsor.getDatedebutContrat();
                                   label.setText(displayText);

                                   // Calculer la date de fin de contrat
                                   LocalDate dateDebut = evenementSponsor.getDatedebutContrat().toLocalDate();
                                   LocalDate dateFin = calculateEndDate(dateDebut, evenementSponsor.getDuree());
                                   LocalDate today = LocalDate.now();

                                   // Vérifier si la date de fin est antérieure à aujourd'hui
                                   if (dateFin.isBefore(today)) {
                                       label.setStyle("-fx-text-fill: red;");
                                   } else {
                                       label.setStyle("");
                                   }
                               } else {
                                   label.setText("Information manquante");
                               }
                           } catch (SQLException e) {
                               e.printStackTrace();
                               label.setText("Erreur lors de la récupération des données");
                           }
                           setGraphic(vbox);
                       }
                   }
               };
           }
       });
   }
    private void filterByDuration() {
        String selectedDuration = cbDurationFilter.getValue();
        loadEvenementSponsorList(selectedDuration);
    }
    private void loadEvenementSponsorList() {
        loadEvenementSponsorList("Tous");
    }

    private void loadEvenementSponsorList(String durationFilter) {
        try {
            List<EvenementSponsor> evenementSponsors = eventSponsorService.afficher();

            if (!durationFilter.equals("Tous")) {
                evenementSponsors = evenementSponsors.stream()
                        .filter(es -> es.getDuree().equals(durationFilter))
                        .collect(Collectors.toList());
            }

            ObservableList<EvenementSponsor> observableList = FXCollections.observableArrayList(evenementSponsors);
            lv_evenementSponsor.setItems(observableList);

            for (EvenementSponsor es : evenementSponsors) {
                Sponsor sponsor = serviceSponsor.afficher().stream()
                        .filter(s -> s.getIdSponsor() == es.getSponsorId())
                        .findFirst()
                        .orElse(null);

                Evenement evenementRecherche = serviceEvenement.afficher().stream()
                        .filter(e -> e.getIdEvent() == es.getEvenementId())
                        .findFirst()
                        .orElse(null);

                if (sponsor != null && evenementRecherche != null) {
                    System.out.println("Evenement: " + evenementRecherche.getNomEvent() +
                            ", Sponsor: " + sponsor.getNomSponso() + " " + sponsor.getPrenomSponso());
                } else {
                    System.out.println("EvenementSponsor: " + es.getEvenementId() + ", Sponsor ID: " + es.getSponsorId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        lv_evenementSponsor.setStyle(
                "-fx-padding: 30px; " +
                        "-fx-border-insets: 15px; " +
                        "-fx-background-insets: 15px;"
        );
    }

    private LocalDate calculateEndDate(LocalDate startDate, String duree) {
        switch (duree) {
            case "troisMois":
                return startDate.plusMonths(3);
            case "sixMois":
                return startDate.plusMonths(6);
            case "unAns":
                return startDate.plusYears(1);
            default:
                return startDate; // Retourner la date de début si la durée n'est pas reconnue
        }
    }



    private void generatePdf(EvenementSponsor evenementSponsor) throws SQLException, IOException, DocumentException {
        // Récupérer le sponsor et l'événement
        Sponsor sponsor = serviceSponsor.afficher().stream()
                .filter(s -> s.getIdSponsor() == evenementSponsor.getSponsorId())
                .findFirst()
                .orElse(null);

        Evenement evenementRecherche = serviceEvenement.afficher().stream()
                .filter(e -> e.getIdEvent() == evenementSponsor.getEvenementId())
                .findFirst()
                .orElse(null);

        if (sponsor == null || evenementRecherche == null) {
            System.out.println("Sponsor ou événement non trouvé.");
            return;
        }

        // Créer le document PDF
        Document document = new Document();
        String filePath = "Contrat_Sponsoring_" + sponsor.getNomSponso() + "_" + evenementRecherche.getNomEvent() + ".pdf";
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Définir des polices avec des couleurs
        Font redFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.NORMAL, BaseColor.RED);
        Font blueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.NORMAL, BaseColor.BLUE);
        Font blackFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);

        // Ajouter le contenu du PDF avec des couleurs
        document.add(new Paragraph("CONTRAT DE SPONSORING\n\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Font.NORMAL, BaseColor.BLUE)));

        document.add(new Paragraph("Entre les soussignés :\n\n", blackFont));

        document.add(new Paragraph("Le Sponsor", blueFont));
        document.add(new Paragraph("Nom : " + sponsor.getNomSponso(), blackFont));
        document.add(new Paragraph("Prénom : " + sponsor.getPrenomSponso(), blackFont));
        document.add(new Paragraph("Identifiant Sponsor : " + sponsor.getIdSponsor(), blackFont));
        document.add(new Paragraph("Budget Alloué : " + sponsor.getBudgetSponso() , redFont));
        double budgetInitial = sponsor.getBudgetSponso();
        double budgetApresReduction = budgetInitial;

        if (sponsor.getClassement() == Classement.Or) {
            budgetApresReduction = budgetInitial * 0.90; // Réduction de 10%
        } else if (sponsor.getClassement() == Classement.Argent) {
            budgetApresReduction = budgetInitial * 0.95; // Réduction de 5%
        }
        document.add(new Paragraph("Budget Apres reduction : " +budgetApresReduction + "\n\n", redFont));
        document.add(new Paragraph("L'Organisateur de l'Événement", blueFont));
        document.add(new Paragraph("Nom de l'Événement : " + evenementRecherche.getNomEvent(), blackFont));
        document.add(new Paragraph("Description de l'Événement : " + evenementRecherche.getDescEvent(), blackFont));
        document.add(new Paragraph("Lieu de l'Événement : " + evenementRecherche.getLieuEvent(), blackFont));
        document.add(new Paragraph("Date de l'Événement : " + evenementRecherche.getDateEvent(), blackFont));
        document.add(new Paragraph("Capacité de l'Événement : " + evenementRecherche.getCapaciteEvent() + "\n\n", blackFont));

        document.add(new Paragraph("Article 1 : Objet du Contrat", blueFont));
        document.add(new Paragraph("Le présent contrat a pour objet de définir les modalités de financement et de partenariat entre le Sponsor et l'Organisateur de l'Événement " + evenementRecherche.getNomEvent() + ", prévu le " + evenementRecherche.getDateEvent() + " au " + evenementRecherche.getLieuEvent() + ". Le Sponsor s'engage à fournir un soutien financier selon les conditions définies ci-après pour l'événement.\n\n", blackFont));

        document.add(new Paragraph("Article 2 : Montant du Sponsoring", blueFont));
        document.add(new Paragraph("Le Sponsor s'engage à fournir une somme de " + sponsor.getBudgetSponso() + " pour la réalisation de l'événement. Cette somme sera utilisée conformément aux besoins définis par l'Organisateur.\n\n", blackFont));

        document.add(new Paragraph("Article 3 : Durée du Contrat", blueFont));
        document.add(new Paragraph("Le présent contrat prend effet à compter du " + evenementSponsor.getDatedebutContrat() + " et se termine le " + evenementSponsor.getDuree() + ". La durée du contrat est choisie par le Sponsor parmi les options suivantes :\n", blackFont));
        document.add(new Paragraph("- Trois Mois\n- Six Mois\n- Un An\n\n", blackFont));

        document.add(new Paragraph("Article 4 : Obligations de l'Organisateur", blueFont));
        document.add(new Paragraph("L'Organisateur s'engage à :\n", blackFont));
        document.add(new Paragraph("- Organiser l'événement " + evenementRecherche.getNomEvent() + " conformément à la description fournie dans le présent contrat.\n", blackFont));
        document.add(new Paragraph("- Assurer la visibilité du Sponsor à travers des supports de communication (affiches, programmes, réseaux sociaux, etc.), comme convenu entre les parties.\n", blackFont));
        document.add(new Paragraph("- Garantir la capacité d'accueil de l'événement de " + evenementRecherche.getCapaciteEvent() + " personnes.\n\n", blackFont));

        document.add(new Paragraph("Article 5 : Obligations du Sponsor", blueFont));
        document.add(new Paragraph("Le Sponsor s'engage à :\n", blackFont));
        document.add(new Paragraph("- Fournir la somme définie dans l'Article 2 avant la date du [date limite de paiement].\n", blackFont));
        document.add(new Paragraph("- Respecter les conditions de partenariat et de visibilité définies par l'Organisateur.\n", blackFont));
        document.add(new Paragraph("- Participer à des activités promotionnelles avant, pendant, ou après l'événement, selon les accords établis.\n\n", blackFont));

        document.add(new Paragraph("Article 6 : Résiliation du Contrat", blueFont));
        document.add(new Paragraph("Le contrat pourra être résilié de plein droit par l'une ou l'autre des parties en cas de manquement grave aux obligations prévues. La résiliation devra être notifiée par écrit et prendra effet immédiatement.\n\n", blackFont));

        document.add(new Paragraph("Article 7 : Force Majeure", blueFont));
        document.add(new Paragraph("Aucune des parties ne pourra être tenue responsable en cas de non-exécution des obligations en raison d'un événement de force majeure, tel que défini par la loi.\n\n", blackFont));

        document.add(new Paragraph("Article 8 : Litiges", blueFont));
        document.add(new Paragraph("En cas de litige, les parties s'engagent à résoudre le différend à l'amiable. En cas d'échec, le tribunal compétent sera celui du lieu de l'événement.\n\n", blackFont));
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = today.format(formatter);
        document.add(new Paragraph("Fait à Worksphere,Tunis Tunisie , le " + formattedDate, blackFont));

        // Ajouter l'image de signature
        try {
            String imagePath = "src\\main\\icons\\signatureContrat.png";
            Image signature = Image.getInstance(imagePath);
            signature.scaleToFit(100, 50); // Ajuster la taille de l'image
            signature.setAbsolutePosition(400, 50); // Positionner l'image en bas à droite
            document.add(signature);
        } catch (IOException e) {
            System.out.println("Erreur lors de l'ajout de l'image de signature : " + e.getMessage());
        }

        // Fermer le document
        document.close();

        System.out.println("PDF généré avec succès !");

        // Ouvrir le fichier PDF
        openPdfFile(filePath);
    }

    private void openPdfFile(String filePath) {
        try {
            // Ouvrir le fichier PDF
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}