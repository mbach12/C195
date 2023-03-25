package Controller;

import Helper.JDBC;
import Helper.Queries;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Class that is used to authenticate the login attempt, and load other controllers.
 */
public class Login implements Initializable {
    /**
     * Load default locale
     */
    public Locale locale = Locale.getDefault();
    /**
     * Load login activity file
     */
    private final File loginLogFile = new File("login_activity.txt");
    /**
     * Invalid user already
     */
    public Alert invalidUserAlert = new Alert(Alert.AlertType.ERROR);
    /**
     * User name
     */
    public TextField usernameText;
    /**
     * Password
     */
    public PasswordField passwordText;
    /**
     * System zone ID
     */
    private final ZoneId zoneId = ZoneId.systemDefault();
    /**
     * System zone
     */
    public Label zoneLabel;
    /**
     * Login text
     */
    public Label loginLabel;
    /**
     * Username text
     */
    public Label usernameLabel;
    /**
     * Password text
     */
    public Label passwordLabel;
    /**
     * Login button
     */
    public Button login;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialized login screen");
        zoneLabel.setText(zoneId.getId());
        locale(locale);
    }

    /**
     * Method to validate the username against the database. If the user exists it will call the method to check the password. If the username does not exist
     * it will display an error message. If the systems Locale is French, the error message will be displayed in French. This method records all login attempts
     * to the login_activity.txt file.
     */
    public void userName() {
        String providedUsername = usernameText.getText();
        String providedPassword = passwordText.getText();
        String resultCheck;
            try {
                Connection connection = JDBC.getConnection();
                boolean usernameFound = false;
                PreparedStatement userStatement = connection.prepareStatement(Queries.getUsernames());
                ResultSet usernameResult = userStatement.executeQuery();
                    while(usernameResult.next()) {
                        resultCheck = usernameResult.getString("User_Name");
                        PreparedStatement userIdStatement = connection.prepareStatement(Queries.getUserId());
                        userIdStatement.setString(1,resultCheck);
                        ResultSet userIdResult = userIdStatement.executeQuery();
                        userIdResult.next();
                        if(providedUsername.equals(resultCheck)) {
                            usernameFound = true;
                            password(providedUsername, providedPassword, userIdResult.getInt("User_ID"));
                        }

                    }
                    if(!usernameFound) {
                        if (locale.getLanguage().equals("fr")) {
                            ResourceBundle labels = ResourceBundle.getBundle("Language/Lang", locale);
                            invalidUserAlert.setTitle(labels.getString("Invalid") + " " + labels.getString("Username"));
                            invalidUserAlert.setContentText(labels.getString("The") + " " + labels.getString("Username") + " " + labels.getString("DoesNotExist"));
                        }
                    else {
                        invalidUserAlert.setTitle("Invalid Username");
                        invalidUserAlert.setContentText("The username does not exist");
                        }
                    invalidUserAlert.showAndWait();
                    try {
                        FileWriter write = new FileWriter(loginLogFile, true);
                        write.write("Login~Failed~" + providedUsername + "~" + LocalDateTime.now().atZone(ZoneId.systemDefault()) + "~" + "Invalid username\n");
                        write.close();
                    }
                    catch(IOException ioException) { ioException.printStackTrace(); }

                    }

            }
            catch(SQLException sqlException) {
                sqlException.printStackTrace();
        }

    }
    /**
     * Login Button Click Event. Calls the Username Validation method to check the username/password.
     */
    public void loginButtonHandler(ActionEvent actionEvent) {
        userName();
    }

    /**
     * Method to validate the password against the database. If the password is valid it will load the appointment screen. If the password is invalid
     * it will display an error message. If the systems Locale is French, the error message will be displayed in French. This method records all login attempts
     * to the login_activity.txt file.
     * @param username Username used to login.
     * @param providedPassword Password used to login.
     * @param userID User id associated with the username.
     */

    public void password(String username, String providedPassword, int userID) {
        try {
            Connection connection = JDBC.getConnection();
            boolean passwordFound = false;
            PreparedStatement passwordStatement = connection.prepareStatement(Queries.getPassword());
            passwordStatement.setString(1,username);
            ResultSet passwordResult = passwordStatement.executeQuery();
                while(passwordResult.next()) {
                    String resultCheck = passwordResult.getString("Password");
                    if(providedPassword.equals(resultCheck)) {
                        passwordFound = true;
                        try {
                            Appointment.userID = userID;
                            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/Appointment.fxml")));
                            Parent root = loader.load();
                            Appointment appointment = loader.getController();
                            appointment.checkAppointments();

                            Stage stage = (Stage) ((Node) usernameText).getScene().getWindow();
                            Scene scene = new Scene(root, 1200, 800);
                            stage.setTitle("Appointments");
                            stage.setScene(scene);
                            stage.show();

                            FileWriter write = new FileWriter(loginLogFile, true);
                            write.write("Login~Success~" + username + "~" + LocalDateTime.now().atZone(ZoneId.systemDefault()) + "~" + "None\n");
                            write.close();

                        }
                        catch (IOException ioe){
                            ioe.printStackTrace();
                        }
                    }
                }
                    if (!passwordFound) {
                        if (locale.getLanguage().equals("fr")) {
                            ResourceBundle labels = ResourceBundle.getBundle("Language/Lang", locale);
                            invalidUserAlert.setTitle(labels.getString("Invalid") + " " + labels.getString("Password"));
                            invalidUserAlert.setContentText(labels.getString("The") + " " + labels.getString("Password") + " " + labels.getString("IsInvalid"));
                        }
                        else {
                            invalidUserAlert.setTitle("Invalid Password");
                            invalidUserAlert.setContentText("The password is invalid");
                        }
                        invalidUserAlert.showAndWait();
                        try {
                            FileWriter write = new FileWriter(loginLogFile, true);
                            write.write("Login~Failed~" + username + "~" + LocalDateTime.now().atZone(ZoneId.systemDefault()) + "~" + "Invalid password\n");
                            write.close();
                        }
                        catch(IOException ioException) { ioException.printStackTrace(); }
                    }
        }
        catch(SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    /**
     * Method to load the Language resource bundle and change the displayed text to french if the system's locale is french.
     * @param locale
     */
    public void locale(Locale locale) {
        if(locale.getLanguage().equals("fr")) {
            ResourceBundle resourceLogin = ResourceBundle.getBundle("Language/Lang", locale);
            passwordLabel.setText(resourceLogin.getString("Password"));
            usernameLabel.setText(resourceLogin.getString("Username"));
            loginLabel.setText(resourceLogin.getString("Login"));
            login.setText(resourceLogin.getString("Login"));
            String america = zoneLabel.getText();
            america = america.replace("America", resourceLogin.getString("America"));
            zoneLabel.setText(america);
        }
    }

    /**
     * Method to exit the program
     * @param actionEvent Exit the program
     */
    public void exitCLick(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }
}
