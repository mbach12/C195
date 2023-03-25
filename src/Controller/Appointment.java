package Controller;

import Helper.ConvertDT;
import Helper.JDBC;
import Helper.Queries;
import Model.Appointments;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * Class that is used to display all the appointments, filter appointments, and load other controllers.
 */

public class Appointment implements Initializable {
    /**
     * User ID that is logged in
     */
    public static int userID;
    /**
     * Appointment table
     */
    public TableView<Appointments> appTable;
    /**
     * Appointment ID
     */

    public TableColumn<Appointments, Integer> appID;
    /**
     * Appointment title
     */

    public TableColumn<Appointments, String> appTitle;
    /**
     * Appointment description
     */

    public TableColumn<Appointments, String> appDescription;
    /**
     * Appointment location
     */

    public TableColumn<Appointments, String> appLocation;
    /**
     * Appointment contact
     */

    public TableColumn<Appointments, String> appContact;
    /**
     * Appointment type
     */

    public TableColumn<Appointments, String> appType;
    /**
     * Appointment start date/time
     */

    public TableColumn<Appointments, String> appStart;
    /**
     * Appointment end date/time
     */
    public TableColumn<Appointments, String> appEnd;
    /**
     * Appointment customer id
     */
    public TableColumn<Appointments, Integer> appCustId;
    /**
     * Appointment user id
     */
    public TableColumn<Appointments, Integer> appUserId;
    /**
     * List of appointments
     */
    public final ObservableList<Appointments> appointmentList = FXCollections.observableArrayList();
    /**
     * Check if an appointment is being modified
     */

    public static Boolean inModify = false;
    /**
     * Appointment screen main label
     */
    public Label mainLabel;
    /**
     * Check if the listener is already enabled
     */
    public boolean listenerEnabled = false;
    /**
     * Filter by none
     */
    public RadioButton noneRadio;
    /**
     * Filter by month
     */
    public RadioButton monthRadio;
    /**
     * Filter by week
     */
    public RadioButton weekRadio;


    /**
     * Method to load a CONFIRMATION alert box
     * @param windowTitle The title of the alert box
     * @param message The message to display in the alert box
     * @return Confirmation alert box
     */
    static boolean confirmBox(String header, String windowTitle, String message ){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(windowTitle);
        alert.setHeaderText(header);
        alert.setContentText(message);
        Optional<ButtonType> action = alert.showAndWait();
        if (action.get() == ButtonType.OK){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Method for deleting appointments. When an appointment is selected in the list and the delete button is clicked. If an item is not selected when the delete button is clicked
     * an error alert will prompt.
     */
    public void deleteAppointment() {
        Appointments appointmentSelected = (Appointments) appTable.getSelectionModel().getSelectedItem();
        if(appointmentSelected == null) {
            Alert olAlert = new Alert(Alert.AlertType.ERROR);
            olAlert.setTitle("Appointment not selected");
            olAlert.setContentText("Please select an appointment to delete it.");
            olAlert.showAndWait();
        } else {
            int appID = appointmentSelected.getAppID();
            String appType = appointmentSelected.getType();
            try {
                String message = "Would you like to delete the selected appointment?\n" +
                        "Appointment ID: " + appID + "\n" +
                        "Type: " + appType;
                if (confirmBox("Delete Appointment","Please Confirm", message)) {
                    Connection connection = JDBC.getConnection();
                    PreparedStatement delApp = connection.prepareStatement(Queries.delAppointment());
                    delApp.setInt(1,appID);
                    int delRows = delApp.executeUpdate();
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    if(delRows > 0) {
                        info.setTitle("Appointment Deleted");
                        info.setContentText("Appointment ID: " + appID + " has been deleted successfully.");
                    } else {
                        info.setTitle("Appointment Not Deleted");
                        info.setContentText("Appointment ID: " + appID + " was not deleted successfully.");
                    }
                    info.showAndWait();
                }
                refreshTable();

            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

    /**
     * Contains one Lambda expressions for this method to get all the appointments from the database to display in the UI. To ensure this method of loading appointments is efficient, it uses lambda
     * expressions.
     * The lambda expression creates an object that implements the ConvertDT interface. This object will take the date/time that is passed and convert it from UTC to the users default time zone.
     */
    public void getAppointments() {
        ConvertDT dtConvert = (timestamp) -> {
            LocalDateTime localDT = timestamp.toLocalDateTime();
            ZonedDateTime zoneDT = localDT.atZone(ZoneId.of("UTC"));
            ZonedDateTime finalZDT = zoneDT.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            return finalZDT.toLocalDateTime();
        };
        try {
            Connection connection = JDBC.getConnection();
            PreparedStatement getAppStatement = connection.prepareStatement(Queries.getAllAppointments());
            ResultSet results =getAppStatement.executeQuery();
            while(results.next()) {
                int appID = results.getInt("Appointment_ID");
                String appTitle = results.getString("Title");
                String appDescription = results.getString("Description");
                String appLocation = results.getString("Location");
                String appType = results.getString("Type");
                DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy hh':'mm a");
                Timestamp appStart = results.getTimestamp("Start");
                String convertedStart = dtFormat.format(dtConvert.convertDT(appStart));
                Timestamp appEnd = results.getTimestamp("End");
                String convertedEnd = dtFormat.format(dtConvert.convertDT(appEnd));
                int appCustomerID = results.getInt("Customer_ID");
                int appUserID = results.getInt("User_ID");
                int appContactID = results.getInt("Contact_ID");
                PreparedStatement contactStatement = connection.prepareStatement(Queries.getContactNameById());
                contactStatement.setInt(1,appContactID);
                ResultSet resultContact = contactStatement.executeQuery();
                resultContact.next();
                String appContactName = resultContact.getString("Contact_Name");
                Appointments appointment = new Appointments(appID, appCustomerID, appUserID, appContactID, appTitle, appDescription, appLocation, appContactName, appType, convertedStart, convertedEnd);
                appointmentList.add(appointment);
            }
                appID.setCellValueFactory(new PropertyValueFactory<>("appID"));
                appTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
                appDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
                appLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
                appContact.setCellValueFactory(new PropertyValueFactory<>("contactName"));
                appType.setCellValueFactory(new PropertyValueFactory<>("type"));
                appStart.setCellValueFactory(new PropertyValueFactory<>("start"));
                appEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
                appCustId.setCellValueFactory(new PropertyValueFactory<>("customerID"));
                appUserId.setCellValueFactory(new PropertyValueFactory<>("userID"));
                appTable.setItems(appointmentList);

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getAppointments();
    }

    /**
     * This method adds a listener to the appointment screen. The listener validates whenever the focus changes on the appointment screen and refreshes the data in the tableview.
     */
    public void addListener() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/Appointment.fxml"));
        Stage stage = (Stage) ((Node) mainLabel).getScene().getWindow();
        stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            @FXML
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean onHidden, Boolean onShown) {
                refreshTable();
                if(monthRadio.isSelected()) { filterByMonth(); }
                if(weekRadio.isSelected()) { filterByWeek(); }
            }
        });
    }

    /**
     * This method loads the Add appointment screen. It also attaches a listener to the appointment screen that will refresh the data
     * whenever the focus changes back to the appointment screen.
     */
    public void addButtonClick(ActionEvent actionEvent) throws IOException {
        if(listenerEnabled == false) {
            addListener();
            listenerEnabled = true;
        }
        this.inModify = false;
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/AddModAppointment.fxml")));
            Parent root = loader.load();
            AddModAppointment addModAppointment = loader.getController();
            addModAppointment.addFormData();
            Stage stage = new Stage();
            stage.setTitle("Add Appointment");
            stage.setScene(new Scene(root));
            stage.show();
    }

    /**
     * This method refreshes the data in the appointment tableview.
     */
    public void refreshTable() {
        appTable.refresh();
        appTable.getItems().clear();
        getAppointments();

    }

    /**
     * Method to load the add or modify appointment screen
     */
    public void updateButtonClick(ActionEvent actionEvent) throws IOException {
        try {
            Appointments appointments = (Appointments) appTable.getSelectionModel().getSelectedItem();
            if (appointments == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Modify Appointment");
                alert.setContentText("No Appointment Selected");
                alert.showAndWait();
                return;
            }
            if(listenerEnabled == false) {
                addListener();
                listenerEnabled = true;
            }
            this.inModify = true;
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/AddModAppointment.fxml")));
            Parent root = loader.load();
            AddModAppointment addModAppointment = loader.getController();
            addModAppointment.addFormData();
            addModAppointment.getData(appTable.getSelectionModel().getSelectedItem());
            Stage stage = new Stage();
            stage.setTitle("Add Appointment");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {

        }


    }

    /**
     * Method to call the delete appointment method.
     * @param actionEvent Delete selected appointment
     */
    public void deleteClick(ActionEvent actionEvent) {
        deleteAppointment();
    }

    /**
     * Method to load the customer screen
     * @param actionEvent Load customer screen
     */
    public void customersButtonClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/Customers.fxml")));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Customers");
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Method to filter appointments by the current month and load into the appointments table.
     */
    public void filterByMonth(){
        ObservableList<Appointments> appointmentsByMonth = FXCollections.observableArrayList();
        weekRadio.setSelected(false);
        noneRadio.setSelected(false);
        int monthCheck = LocalDateTime.now().getMonth().getValue();
        int yearCheck = LocalDateTime.now().getYear();
        DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy hh':'mm a");
        int i=0;
        for(Appointments appointment: appointmentList) {
            LocalDateTime currentAppDT = LocalDateTime.parse(appointmentList.get(i).getStart(), dtFormat);
            int currentAppMonth = currentAppDT.getMonth().getValue();
            int currentAppYear = currentAppDT.getYear();
            if(yearCheck == currentAppYear && monthCheck == currentAppMonth) {
                appointmentsByMonth.add(appointment);
            }
            i += 1;
        }
        appTable.refresh();
        appTable.getItems().clear();
        appTable.setItems(appointmentsByMonth);
    }
    /**
     * Method to filter appointments by the current week and load into the appointments table.
     */
    public void filterByWeek(){
        ObservableList<Appointments> appointmentsByWeek = FXCollections.observableArrayList();
        monthRadio.setSelected(false);
        noneRadio.setSelected(false);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekCheck = LocalDateTime.now().get(weekFields.weekOfWeekBasedYear());
        DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy hh':'mm a");
        int z=0;
        for(Appointments appointment: appointmentList) {
            LocalDateTime currentAppDT = LocalDateTime.parse(appointmentList.get(z).getStart(), dtFormat);
            int currentWeek= currentAppDT.get(weekFields.weekOfWeekBasedYear());
            if(weekCheck == currentWeek) {
                appointmentsByWeek.add(appointment);
            }
            z += 1;
        }
        appTable.refresh();
        appTable.getItems().clear();
        appTable.setItems(appointmentsByWeek);
    }

    /**
     * Method to filter by current month
     * @param actionEvent Filter by current month
     */
    public void monthClick(ActionEvent actionEvent) {
        refreshTable();
        filterByMonth();
    }

    /**
     * Method to filter by current week
     * @param actionEvent Filter by current week
     */
    public void weekClick(ActionEvent actionEvent) {
        refreshTable();
        filterByWeek();
    }
    /**
     * Method to clear any filters
     * @param actionEvent Clear filters
     */
    public void noneClick(ActionEvent actionEvent) {
        weekRadio.setSelected(false);
        monthRadio.setSelected(false);
        refreshTable();

    }

    /**
     * Method to check if there are any upcoming appointments within the next 15 minutes.
     */
    public void checkAppointments() {
        ConvertDT dtConvert = (timestamp) -> {
            LocalDateTime localDT = timestamp.toLocalDateTime();
            ZonedDateTime zoneDT = localDT.atZone(ZoneId.of("UTC"));
            ZonedDateTime finalZDT = zoneDT.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            return finalZDT.toLocalDateTime();
        };
        ObservableList<Appointments> appointmentsIn15 = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
        LocalDateTime currentDT = LocalDateTime.now();
        Alert appAlert = new Alert(Alert.AlertType.INFORMATION);
        appAlert.setTitle("Appointments");
        Connection connection = JDBC.getConnection();
        PreparedStatement checkApp = connection.prepareStatement(Queries.getAllAppointments());
        ResultSet results = checkApp.executeQuery();
        while(results.next()) {
            Timestamp appStart = results.getTimestamp("Start");
            String convertedStart = formatter.format(dtConvert.convertDT(appStart));
            LocalDateTime start = LocalDateTime.parse(convertedStart, formatter);
            long minutes = ChronoUnit.MINUTES.between(currentDT, start);
            if(minutes <= 15 && minutes > 0) {
                int id = results.getInt("Appointment_ID");
                int cID = results.getInt("Customer_ID");
                int uID = results.getInt("User_ID");
                int contactID = results.getInt("Contact_ID");
                String title = results.getString("Title");
                String desc = results.getString("Description");
                String location = results.getString("Location");
                String cName = "";
                String type = results.getString("Type");
                Timestamp appStart1 = results.getTimestamp("Start");
                String convertedStart1 = formatter.format(dtConvert.convertDT(appStart1));
                Timestamp appEnd = results.getTimestamp("End");
                String convertedEnd = formatter.format(dtConvert.convertDT(appEnd));
                Appointments appointment = new Appointments(id, cID, uID, contactID, title, desc, location, cName, type, convertedStart1, convertedEnd);
                appointmentsIn15.add(appointment);
            }
        }
        if(appointmentsIn15.size() > 0) {
            String context = "The following appointments are within 15 minutes:\n";
            for (int i=0; i < appointmentsIn15.size(); i++) {
                LocalDateTime dtStart = LocalDateTime.parse(appointmentsIn15.get(i).getStart(), formatter);
                //ZonedDateTime zonedAppTime = dtStart.atZone(ZoneId.systemDefault());
                //LocalDateTime convertedAppTime = zonedAppTime.toLocalDateTime();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                String timePattern = "hh:mm:ss a";
                LocalDate convertedDate = dtStart.toLocalDate();
                String formattedDate = convertedDate.format(dateTimeFormatter);
                LocalTime convertedTime = dtStart.toLocalTime();
                String formattedTime = convertedTime.format(DateTimeFormatter.ofPattern(timePattern));
                context = context + "Appointment ID: " + appointmentsIn15.get(i).getAppID() + " Date: " + formattedDate + " Time: " + formattedTime + "\n";
            }
            appAlert.setContentText(context);
            appAlert.showAndWait();
        } else {
            String context = "There are no appointments within the next 15 minutes\n";
            appAlert.setContentText(context);
            appAlert.showAndWait();
        }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    /**
     * Method to logout of current session and return to the login screen
     * @param actionEvent Logout
     */
    public void logoutClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/Login.fxml")));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) noneRadio).getScene().getWindow();
        stage.setTitle("Login Screen");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
    /**
     * Method to load the reports screen
     * @param actionEvent Load reports screen
     */
    public void reportsClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/Reports.fxml")));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) mainLabel).getScene().getWindow();
        stage.setTitle("Reports");
        stage.setScene(new Scene(root, 1200, 800));
        stage.show();
    }
}
