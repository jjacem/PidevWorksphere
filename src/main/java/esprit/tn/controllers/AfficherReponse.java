package esprit.tn.controllers;

import esprit.tn.entities.Reponse;
import esprit.tn.services.ServiceReponse;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
        private Label lblReclamationId;

        private final ServiceReponse serviceReponse = new ServiceReponse();
        private int userId;
        private int reclamationId;


        public void setIds(int userId, int reclamationId) {
            this.userId = userId;
            this.reclamationId = reclamationId;
            loadReponse();
        }

        // Load response from DB
        private void loadReponse() {
            Reponse reponse = serviceReponse.checkForRepInRec(reclamationId);

            if (reponse != null) {
                lblMessage.setText(reponse.getMessage());
                lblStatus.setText(reponse.getStatus());
                lblDateDepot.setText(reponse.getDatedepot().toString());
                lblUserId.setText(String.valueOf(reponse.getId_user()));
                lblReclamationId.setText(String.valueOf(reponse.getId_reclamation()));
            } else {
                lblMessage.setText("Aucune réponse trouvée.");
                lblStatus.setText("-");
                lblDateDepot.setText("-");
                lblUserId.setText("-");
                lblReclamationId.setText("-");
            }
        }


    }


