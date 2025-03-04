package esprit.tn.controllers;

import esprit.tn.entities.Reclamation;
import esprit.tn.entities.Reponse;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceReclamation;
import esprit.tn.services.ServiceReponse;
import esprit.tn.services.ServiceUser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AfficherReponse {

    @FXML
    private Label lblMessage;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblDateDepot;
    @FXML
    private Label lblUserId;

    @FXML
    private Label lblReclamationname;

    private final ServiceReponse serviceReponse = new ServiceReponse();
    private int userId;
    private int reclamationId;

    public void setIds(int userId, int reclamationId) throws SQLException {
        this.userId = userId;
        this.reclamationId = reclamationId;
        loadReponse();
    }

    private void loadReponse() throws SQLException {
        Reponse reponse = serviceReponse.checkForRepInRec(reclamationId);

        if (reponse != null) {
            ServiceUser us = new ServiceUser();
            User user = us.findbyid(reponse.getId_user());
            System.out.println(user);
            String nom=user.getNom() + " " + user.getPrenom();
            ServiceReclamation rec = new ServiceReclamation();
            Reclamation r = rec.getReclamationById(reponse.getId_reclamation());

            if (lblMessage != null) lblMessage.setText(safeText(reponse.getMessage()));
            if (lblStatus != null) lblStatus.setText(safeText(reponse.getStatus()));
            if (lblDateDepot != null) lblDateDepot.setText(formatTimestamp(reponse.getDatedepot()));
            if (lblUserId != null) lblUserId.setText(safeText(nom));

            if (lblReclamationname != null) lblReclamationname.setText(safeText(r.getTitre()));
        } else {
            showNoResponseMessage();
        }
    }

    private String safeText(String text) {
        return (text != null && !text.isEmpty()) ? text : "N/A";
    }

    private String formatTimestamp(Timestamp timestamp) {
        return (timestamp != null) ? timestamp.toString() : "N/A";
    }

    private void showNoResponseMessage() {
        if (lblMessage != null) lblMessage.setText("Aucune réponse trouvée.");
        if (lblStatus != null) lblStatus.setText("-");
        if (lblDateDepot != null) lblDateDepot.setText("-");
        if (lblUserId != null) lblUserId.setText("-");

        if (lblReclamationname != null) lblReclamationname.setText("-");
    }
}