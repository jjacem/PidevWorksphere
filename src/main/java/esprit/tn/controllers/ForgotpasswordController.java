package esprit.tn.controllers;

import esprit.tn.services.ServiceUser;
import esprit.tn.utils.Emailsend;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.Random;

public class ForgotpasswordController {
    @FXML
    private TextField mail;
    @FXML
    private TextField verificationCodeField;
    @FXML
    private VBox verificationBox;
    @FXML
    private VBox resetPasswordBox;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private Button verifyCodeButton;
    @FXML
    private Button resetPasswordButton;

    private final ServiceUser serviceUser = new ServiceUser();
    private String generatedCode;
    private String userEmail;
private int id;
    public void onSendMail() throws SQLException {
        userEmail = mail.getText().trim();

        if (serviceUser.findidbyemail(userEmail) != -1) {
            Random random = new Random();
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                sb.append(random.nextInt(10));
            }
            generatedCode = sb.toString();


            Emailsend.sendEmail(userEmail, "Verification Code", "Your verification code is: " + generatedCode);

            // Show verification input field
            verificationBox.setVisible(true);
            showAlert("Success", "A verification code has been sent to your email.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Email not found.", Alert.AlertType.ERROR);
        }
    }

    public void onVerifyCode() {
        String enteredCode = verificationCodeField.getText().trim();
        if (enteredCode.equals(generatedCode)) {
            verificationBox.setVisible(false);
            resetPasswordBox.setVisible(true);
        } else {
            showAlert("Error", "Incorrect verification code. Try again.", Alert.AlertType.ERROR);
        }
    }

    public void onResetPassword() throws SQLException {
        String newPassword = newPasswordField.getText().trim();
        String repeatPassword = repeatPasswordField.getText().trim();

        if (newPassword.isEmpty() || repeatPassword.isEmpty()) {
            showAlert("Error", "Please enter your new password in both fields.", Alert.AlertType.ERROR);
            return;
        }

        if (!newPassword.equals(repeatPassword)) {
            showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
            return;
        }

        // Update password in database
        serviceUser.changermdp( newPassword,serviceUser.findidbyemail(userEmail));
        showAlert("Success", "Your password has been updated successfully.", Alert.AlertType.INFORMATION);

        // Hide the password reset box
        resetPasswordBox.setVisible(false);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
