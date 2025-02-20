package esprit.tn.controllers;

import esprit.tn.services.ServiceUser;
import esprit.tn.utils.Emailsend;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.Random;
import java.util.regex.Pattern;

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
    private int userId = -1;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    @FXML
    private void onSendMail(ActionEvent event) throws SQLException {
        userEmail = mail.getText().trim();

        if (!isValidEmail(userEmail)) {
            showAlert("Invalid Email", "Please enter a valid email address.", Alert.AlertType.ERROR);
            return;
        }

        userId = serviceUser.findidbyemail(userEmail);

        if (userId != -1) {
            generatedCode = generateVerificationCode(6);
            Emailsend.sendEmail(userEmail, "Password Reset Code", "Your verification code is: " + generatedCode);

            verificationBox.setVisible(true);
            showAlert("Success", "A verification code has been sent to your email.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Email not found. Please enter a registered email.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onVerifyCode(ActionEvent event) {
        String enteredCode = verificationCodeField.getText().trim();

        if (!isNumeric(enteredCode)) {
            showAlert("Invalid Code", "Verification code must be numeric.", Alert.AlertType.ERROR);
            return;
        }

        if (enteredCode.equals(generatedCode)) {
            verificationBox.setVisible(false);
            resetPasswordBox.setVisible(true);
        } else {
            showAlert("Error", "Incorrect verification code. Please try again.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onResetPassword(ActionEvent event) throws SQLException {
        String newPassword = newPasswordField.getText().trim();
        String repeatPassword = repeatPasswordField.getText().trim();

        if (!isValidPassword(newPassword)) {
            showAlert("Weak Password", "Password must be at least 8 characters long and include:\n- One uppercase letter\n- One lowercase letter\n- One number\n- One special character (@$!%*?&)", Alert.AlertType.ERROR);
            return;
        }

        if (!newPassword.equals(repeatPassword)) {
            showAlert("Error", "Passwords do not match. Please try again.", Alert.AlertType.ERROR);
            return;
        }

        if (userId == -1) {
            showAlert("Error", "Invalid session. Please restart the process.", Alert.AlertType.ERROR);
            return;
        }

        serviceUser.changermdp(newPassword, userId);
        showAlert("Success", "Your password has been reset successfully.", Alert.AlertType.INFORMATION);

        resetPasswordBox.setVisible(false);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String generateVerificationCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    private boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
