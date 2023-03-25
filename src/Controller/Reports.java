package Controller;

import Helper.ConvertDT;
import Helper.JDBC;
import Helper.Queries;
import Model.Appointments;
import Model.ReportAppByMonthType;
import Model.ReportLoginActivity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Class that is used to display reports.
 */
public class Reports implements Initializable {
    /**
     * Reports main label
     */
    public Label reportsLabel;
    /**
     * List of appointments by month/type
     */
    public final ObservableList<ReportAppByMonthType> reportAppByMonthTypes = FXCollections.observableArrayList();
    /**
     * List of appointments by contact
     */
    public final ObservableList<Appointments> reportContactSchedules = FXCollections.observableArrayList();
    /**
     * List of login activities
     */
    public final ObservableList<ReportLoginActivity> reportLoginActivities = FXCollections.observableArrayList();
    /**
     * List of contact in the DB
     */
    public final ObservableList<String> contacts = FXCollections.observableArrayList();
    /**
     * Appointment ID
     */
    public TableColumn appID;
    /**
     * Appointment title
     */
    public TableColumn appTitle;
    /**
     * Appointment type
     */
    public TableColumn appType;
    /**
     * Appointment description
     */
    public TableColumn appDesc;
    /**
     * Appointment start date/time
     */
    public TableColumn appStart;
    /**
     * Appointment end date/time
     */
    public TableColumn appEnd;
    /**
     * Appointment customer ID
     */
    public TableColumn appCid;
    /**
     * Appointment table for contact schedule report
     */
    public TableView appTable;
    /**
     * Table for appointments by Month/type
     */
    public TableView r1Table;
    /**
     * Appointment Month
     */
    public TableColumn r1Month;
    /**
     * Appointment Type
     */
    public TableColumn r1Type;
    /**
     * Appointment quantity
     */
    public TableColumn r1Quantity;
    /**
     * List of contacts
     */
    public ComboBox contact;
    /**
     * Login activity table
     */
    public TableView r3Table;
    /**
     * Type of activity (Login)
     */
    public TableColumn r3Type;
    /**
     * Activity status (Success / Fail)
     */
    public TableColumn r3Status;
    /**
     * Activity user name
     */
    public TableColumn r3User;
    /**
     * Date/Time of activity
     */

    public TableColumn r3DT;
    /**
     * Login activity notes
     */
    public TableColumn r3Notes;
    /**
     * Load login activity file
     */
    private final File loginLogFile = new File("login_activity.txt");
    /**
     * System local time zone
     */
    private String zoneName = ZoneId.systemDefault().getDisplayName(TextStyle.FULL, Locale.getDefault());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getReportAppByMonthType();
        getContacts();
        reportContactSchedules("All");
        reportLoginActivity();
        r3DT.setText("Date/Time (" + zoneName + ")");
    }

    /**
     * Method to display the login activity report. This will read the login_activity.txt file, convert the reported date/time in the file to the users local date/time,
     * and display the data in the table.
     */
    public void reportLoginActivity() {
        BufferedReader read;
        DateTimeFormatter format = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy hh':'mm a");
        try {
            read = new BufferedReader(new FileReader(loginLogFile));
            String line = read.readLine();
            while (line != null) {
                String[] lineSplit = line.split("~");
                String type = null,status = null,user = null,notes = null, dt = null, dtRecord = null;
                for(int i=0;i<lineSplit.length;i++) {
                    if(i == 0) { type = lineSplit[i]; }
                    else if(i == 1) { status = lineSplit[i]; }
                    else if(i == 2) { user = lineSplit[i]; }
                    else if(i == 3) { dt = lineSplit[i]; }
                    else if(i == 4) { notes = lineSplit[i]; }
                }
                dtRecord = dt;
                dtRecord = dtRecord.substring(dtRecord.indexOf("[") + 1);
                dtRecord = dtRecord.substring(0, dtRecord.indexOf("]"));
                LocalDateTime localdateTime = LocalDateTime.parse(dt, format);
                ZonedDateTime zoneDT = localdateTime.atZone(ZoneId.of(dtRecord));
                ZonedDateTime finalZDT = zoneDT.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
                String convertedDT = dtFormat.format(finalZDT);
                ReportLoginActivity reportLoginActivity = new ReportLoginActivity(type,status,user,notes,convertedDT);
                reportLoginActivities.add(reportLoginActivity);
                line = read.readLine();
            }
            r3Type.setCellValueFactory(new PropertyValueFactory<>("type"));
            r3Status.setCellValueFactory(new PropertyValueFactory<>("status"));
            r3User.setCellValueFactory(new PropertyValueFactory<>("user"));
            r3DT.setCellValueFactory(new PropertyValueFactory<>("dt"));
            r3Notes.setCellValueFactory(new PropertyValueFactory<>("notes"));
            r3Table.setItems(reportLoginActivities);
            read.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to get all the contacts in the database.
     */
    public void getContacts() {
        try {
            Connection connection = JDBC.getConnection();
            PreparedStatement getReport = connection.prepareStatement(Queries.getContactNames());
            ResultSet results = getReport.executeQuery();
            contacts.add("** ALL Contacts **");
            while(results.next()) {
                String name = results.getString("Contact_Name");
                contacts.add(name);
            }
            contact.setItems(contacts);
            contact.getSelectionModel().select("** ALL Contacts **");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    /**
     * Method to retrieve all the appointments for the provided contact name.
     * @param name The name of the contact to retrieve appointments for.
     */
    public void reportContactSchedules(String name) {
        ConvertDT dtConvert = (timestamp) -> {
            LocalDateTime localDT = timestamp.toLocalDateTime();
            ZonedDateTime zoneDT = localDT.atZone(ZoneId.of("UTC"));
            ZonedDateTime finalZDT = zoneDT.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            return finalZDT.toLocalDateTime();
        };
        String cID = null;
        System.out.println(name);
        try {
            Connection connection = JDBC.getConnection();
            String query = null;
            if (name == "All") { query = Queries.getAllAppointments();
            } else {
                query = Queries.getAppointmentsByContact();
                PreparedStatement getCustomerID = connection.prepareStatement(Queries.getContactIdFromName());
                getCustomerID.setString(1,name);
                ResultSet result = getCustomerID.executeQuery();
                result.next();
                cID = result.getString("Contact_ID");
            }

            PreparedStatement getAppStatement = connection.prepareStatement(query);
            if (name != "All") {
                getAppStatement.setInt(1, Integer.parseInt(cID));
            }
            ResultSet results = getAppStatement.executeQuery();
            while(results.next()) {
                Appointment appointment = new Appointment();
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
                Appointments setAppointment = new Appointments(appID, appCustomerID, appUserID, appContactID, appTitle, appDescription, appLocation, appContactName, appType, convertedStart, convertedEnd);
                reportContactSchedules.add(setAppointment);
            }
            appID.setCellValueFactory(new PropertyValueFactory<>("appID"));
            appTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            appDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
            appType.setCellValueFactory(new PropertyValueFactory<>("type"));
            appStart.setCellValueFactory(new PropertyValueFactory<>("start"));
            appEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
            appCid.setCellValueFactory(new PropertyValueFactory<>("customerID"));

            appTable.setItems(reportContactSchedules);

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    /**
     * Method to get the data of all appointments by Month and type to populate in the table.
     */

    public void getReportAppByMonthType(){
        try {
            Connection connection = JDBC.getConnection();
            PreparedStatement getReport = connection.prepareStatement(Queries.reportAppByMonthType());
            ResultSet results = getReport.executeQuery();
            while(results.next()) {
                String month = results.getString("month");
                String type = results.getString("type");
                int total = results.getInt("total");
                ReportAppByMonthType reportAppByMonthType = new ReportAppByMonthType(total, month, type);
                reportAppByMonthTypes.add(reportAppByMonthType);
            }
            r1Month.setCellValueFactory(new PropertyValueFactory<>("month"));
            r1Type.setCellValueFactory(new PropertyValueFactory<>("type"));
            r1Quantity.setCellValueFactory(new PropertyValueFactory<>("total"));
            r1Table.setItems(reportAppByMonthTypes);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    /**
     * Method to close the reports screen and go back to the appointments screen.
     * @param actionEvent Close report screen.
     */
    public void backClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/Appointment.fxml")));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) reportsLabel).getScene().getWindow();
        stage.setTitle("Appointments");
        stage.setScene(new Scene(root, 1200, 800));
        stage.show();
    }

    /**
     * Method to call the method to generate the appointment report by contact based on the selected contact.
     * @param actionEvent Open appointment report for contacts.
     */

    public void contactsClick(ActionEvent actionEvent) {
        if((String) contact.getSelectionModel().getSelectedItem() == "** ALL Contacts **") {
            appTable.refresh();
            appTable.getItems().clear();
            reportContactSchedules("All");
        } else {
            appTable.refresh();
            appTable.getItems().clear();
            reportContactSchedules((String) contact.getSelectionModel().getSelectedItem());
        }

    }
}
