package esprit.tn.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import esprit.tn.entities.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
}