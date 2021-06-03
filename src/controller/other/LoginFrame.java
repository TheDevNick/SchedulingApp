package controller.other;

import controller.BaseController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * negotiates the login flow
 */
public class LoginFrame extends BaseController implements Initializable {
    @FXML
    private TextField tfUsername;
    @FXML
    private TextField tfPassword;
    @FXML
    private Label lbZone;

    /**
     * validates the password matches for the return row (if any). also hard-coded to allow for a test account
     *
     * @param ex a SQL exception generated by the query
     * @param rs a result set containing 1 or 0 rows
     * @return whether the password from the form matches the password in the database
     */
    private long validateInput(SQLException ex, ResultSet rs) {
        if (ex == null) {
            String username = tfUsername.getText();
            String password = tfPassword.getText();
            try {
                if (rs.next() && (rs.getString("Password").trim().equals(hashPassword().trim()) || (username.equals("test") && password.equals("test"))))
                    return rs.getLong("User_ID");
            } catch (SQLException exc) {
                printSQLException(exc);
            }
        }

        return -1;
    }

    /**
     * validates that the required fields aren't empty and then checks
     *
     */
    @FXML
    private void login() {
        String username = tfUsername.getText();
        String password = tfPassword.getText();
        if (username.length() != 0 && password.length() != 0) {
            List<Object> arguments = new ArrayList<>();
            arguments.add(username);
            long userId = executeQuery("SELECT User_ID, Password " +
                    "FROM users " +
                    "WHERE User_Name = ? " +
                    "LIMIT 1", arguments, this::validateInput);
            saveLoginAction(userId != -1);
            if (userId != -1) {
                BaseController.userID = userId;
                viewController.showMainView();
            } else {
                showMessageBox(resourceBundle.getString("error.defaultTitle"), resourceBundle.getString("error.invalidCredentials"), Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Enter key pressed
     * @param event JavaFX keyevent
     */
    @FXML
    private void enterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            login();
    }

    /**
     * hash password
     * @return the hashed password
     */
    private String hashPassword() {
        try {
            byte[] messageDigest = MessageDigest.getInstance("SHA-512").digest(tfPassword.getText().getBytes());
            return Base64.getEncoder().encodeToString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * save login status to the login_activity.txt file
     * @param status whether the login attempt was successful
     */
    private void saveLoginAction(boolean status) {
        String time = DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        String username = tfUsername.getText();
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("login_activity.txt", true));
            pw.print("login time: " + time + "\t");
            pw.print("username: " + username + "\t");
            pw.print("status: " + status + "\t");
            pw.println();
            pw.close();
        } catch (IOException ex) {
            System.out.println("Failed to log invalid login attempt:");
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbZone.setText(getLocale().toString());
    }
}