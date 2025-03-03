package esprit.tn.utils;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import esprit.tn.entities.*;
import esprit.tn.services.ServiceProjet;
import java.io.*;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PDFGenerator {

    public static byte[] generateEquipePDF(Equipe equipe) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Ajouter la date d'aujourd'hui
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        document.add(new Paragraph("Date: " + date).setTextAlignment(TextAlignment.RIGHT));

        // Ajouter le nom de l'équipe
        document.add(new Paragraph("Équipe: " + equipe.getNomEquipe())
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Ajouter les membres de l'équipe
        document.add(new Paragraph("Membres de l'équipe:")
                .setBold()
                .setFontSize(14)
                .setMarginBottom(10));

        Table membresTable = new Table(UnitValue.createPercentArray(new float[]{3, 3})); // 2 colonnes : Nom, Email
        membresTable.setWidth(UnitValue.createPercentValue(100));

        for (User user : equipe.getEmployes()) {
            // Ajouter le nom et prénom
            membresTable.addCell(user.getNom() + " " + user.getPrenom());

            // Ajouter l'email
            membresTable.addCell(user.getEmail());
        }
        document.add(membresTable);

        // Ajouter les projets assignés
        document.add(new Paragraph("Projets assignés:")
                .setBold()
                .setFontSize(14)
                .setMarginTop(20)
                .setMarginBottom(10));

        Table projetsTable = new Table(UnitValue.createPercentArray(new float[]{3, 7}));
        projetsTable.setWidth(UnitValue.createPercentValue(100));

        for (Projet projet : equipe.getProjets()) {
            projetsTable.addCell(projet.getNom());
            projetsTable.addCell(projet.getDescription());
        }
        document.add(projetsTable);

        // Fermer le document
        document.close();

        return outputStream.toByteArray();
    }


    public static byte[] generateProjetPDF(Projet projet) throws IOException {
        ServiceProjet serviceProjet = new ServiceProjet();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // Charger l'image depuis les ressources
            InputStream logoStream = PDFGenerator.class.getResourceAsStream("/images/worksphere.png");
            if (logoStream == null) {
                throw new FileNotFoundException("Logo non trouvé dans les ressources : /images/worksphere.png");
            }
            ImageData logoData = ImageDataFactory.create(logoStream.readAllBytes());
            com.itextpdf.layout.element.Image logo = new com.itextpdf.layout.element.Image(logoData).setWidth(100).setHeight(80);

            // Créer un paragraphe pour contenir le logo et l'aligner à gauche
            Paragraph paragraph = new Paragraph()
                    .add(logo)
                    .setTextAlignment(TextAlignment.LEFT); // Aligner le paragraphe à gauche

            // Ajouter le paragraphe au document
            document.add(paragraph);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du logo : " + e.getMessage());
        }

        // Ajouter la date et l'heure
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        document.add(new Paragraph("Date : " + date)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10)
                .setFontColor(ColorConstants.GRAY));

        // Titre du document
        document.add(new Paragraph("Détails du Projet")
                .setBold()
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Ajouter l'image du projet
        if (projet.getImageProjet() != null && !projet.getImageProjet().isEmpty()) {
            try {
                String imagePath = "C:/xampp/htdocs/img/" + new File(projet.getImageProjet()).getName();
                ImageData imageData = ImageDataFactory.create(imagePath);
                com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image(imageData).setWidth(100).setHeight(80);
                document.add(image.setHorizontalAlignment(HorizontalAlignment.CENTER));
            } catch (MalformedURLException e) {
                System.err.println("Image du projet non trouvée : " + e.getMessage());
            }
        }

        // Informations du projet
        document.add(new Paragraph("Nom du projet : " + projet.getNom())
                .setBold()
                .setFontSize(16)
                .setMarginTop(20));

        document.add(new Paragraph("Description : " + projet.getDescription())
                .setFontSize(12)
                .setMarginBottom(10));

        document.add(new Paragraph("Date de création : " + projet.getDatecréation())
                .setFontSize(12));

        document.add(new Paragraph("Deadline : " + projet.getDeadline())
                .setFontSize(12));

        document.add(new Paragraph("État : " + projet.getEtat().name())
                .setFontSize(12)
                .setMarginBottom(20));

        // Ajouter les membres de l'équipe
        document.add(new Paragraph("Membres de l'équipe :")
                .setBold()
                .setFontSize(16)
                .setMarginTop(20));

        Table membresTable = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3})); // 3 colonnes : Image, Nom, Email
        membresTable.setWidth(UnitValue.createPercentValue(100));

        // Récupérer l'équipe et les employés associés au projet
        try {
            Equipe equipe = serviceProjet.getEquipeAvecEmployesParProjet(projet.getId());
            if (equipe != null) {
                // Ajouter le nom de l'équipe
                document.add(new Paragraph("Équipe : " + equipe.getNomEquipe())
                        .setBold()
                        .setFontSize(14)
                        .setMarginBottom(10));

                // Vérifier si la liste des employés est null ou vide
                List<User> employes = equipe.getEmployes();
                if (employes != null && !employes.isEmpty()) {
                    for (User membre : employes) {
                        // Ajouter l'image du membre
                        if (membre.getImageProfil() != null && !membre.getImageProfil().isEmpty()) {
                            try {
                                String imagePath = "C:/xampp/htdocs/img/" + new File(membre.getImageProfil()).getName();
                                ImageData imageData = ImageDataFactory.create(imagePath);
                                com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image(imageData)
                                        .setWidth(30) // Largeur fixe pour les images des membres
                                        .setAutoScaleHeight(true); // Ajuster la hauteur automatiquement
                                membresTable.addCell(image);
                            } catch (MalformedURLException e) {
                                membresTable.addCell(new Paragraph("Pas d'image"));
                            }
                        } else {
                            membresTable.addCell(new Paragraph("Pas d'image"));
                        }

                        // Ajouter le nom et prénom
                        membresTable.addCell(new Paragraph(membre.getNom() + " " + membre.getPrenom()));

                        // Ajouter l'email
                        membresTable.addCell(new Paragraph(membre.getEmail()));
                    }
                } else {
                    // Ajouter un message si la liste des employés est vide ou null
                    membresTable.addCell(new Paragraph("Aucun membre dans cette équipe."));
                }
            } else {
                // Ajouter un message si l'équipe est null
                membresTable.addCell(new Paragraph("Aucune équipe assignée."));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Ajouter un message d'erreur
            membresTable.addCell(new Paragraph("Erreur lors de la récupération de l'équipe."));
        }

        document.add(membresTable);

        // Fermer le document
        document.close();

        return outputStream.toByteArray();
    }
}