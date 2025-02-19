package esprit.tn.controllers;

import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import javax.swing.text.html.ImageView;

public class DashboardCandidat {

    @FXML
    private AnchorPane contentPane;
@FXML
    ImageView img;
    public void modifierprofil(ActionEvent actionEvent) {
     try{
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCompte.fxml"));
         Stage stage = new Stage();
         stage.setScene(new Scene(loader.load()));

         ModifierCompteController  controller = loader.getController();

         int userId = SessionManager.extractuserfromsession().getIdUser();
         controller.initData(userId);

         stage.setTitle("Modifier Utilisateur");
         stage.show();
        } catch (IOException e) {
            e.printStackTrace();
     } catch (SQLException e) {
         throw new RuntimeException(e);
     }
    }

    public void consulterreclamation(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamations.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
