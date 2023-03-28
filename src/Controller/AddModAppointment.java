package Controller;

import Helper.*;
import Model.Appointments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Class that is used to add or modify appointments.
 */
public class AddModAppointment implements Initializable {
    /**
     * Add/Modify appointment label
     */
    public Label appointmentMainLabel;
    /**
     * State time
     */
    public ComboBox<LocalTime> appStartTime;
    /**
     * End time
     */
    public ComboBox<LocalTime> appEndTime;
    /**
     * Customer name
     */
    public ComboBox cName;
    /**
     * Appointment ID
     */
    public TextField appID;
    /**
     * Appointment title
     */
    public TextField appTitle;
    /**
     * Appointment description
     */
    public TextField appDesc;
    /**
     * Appointment location
     */
    public TextField appLocation;
    /**
     * Appointment Type
     */
    public TextField appType;
    /**
     * Appointment start date
     */
    public DatePicker appStartDate;
    /**
     * Appointment End date
     */
    public DatePicker appEndDate;
    /**
     * Appointment customer ID
     */
    public TextField appCustomerID;
    /**
     * Appointment User ID
     */
    public TextField appUserID;


    /**
     * This method will add contacts from the database to the contacts combo box and add
     * appointment times in increments of 15 minutes to the start/end time combo boxes.
     */

    public void addFormData() {
        ObservableList<String> formContactList = FXCollections.observableArrayList();
        ObservableList<LocalTime> formTimeList = FXCollections.observableArrayList();
        LocalTime timetrack = LocalTime.of(0,0);
        formTimeList.add(timetrack);
        for (int timeN = 0; timeN < 95; timeN++) {
            timetrack = timetrack.plusMinutes(15);
            formTimeList.add(timetrack);
        }
        try {
            Connection connection = JDBC.getConnection();
            PreparedStatement contacts = connection.prepareStatement(Queries.getContactNames());
            ResultSet results = contacts.executeQuery();
                while(results.next()) {
                    String cName = results.getString("Contact_Name");
                    formContactList.add(cName);
                }
        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
        }
            appStartTime.setItems(formTimeList);
            appEndTime.setItems(formTimeList);
            cName.setItems(formContactList);

    };


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Appointment appointment = new Appointment();
        if(appointment.inModify.equals(false)) { this.appointmentMainLabel.setText("Add Appointment");}
        if(appointment.inModify.equals(true)) { this.appointmentMainLabel.setText("Modify Appointment");}
    }

    /**
     * Method to close the add or modify appointment screen and go back to the appointment screen.
     * @param actionEvent Close window
     */
    public void cancelButtonClick(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) appointmentMainLabel).getScene().getWindow();
        stage.close();
    }

    /**
     * Contains one Lambda expression for this method to get appointment data from the database. To ensure this method of getting appointments is efficient, it uses lambda expressions.
     * The lambda expression creates an object that implements the ConvertDT interface that will take the date/time and convert it from UTC to the local date/time on the machine.
     * @param appointment Appointment to modify
     */
    public void getData(Appointments appointment) {
        ConvertDT dtConvert = (timestamp) -> {
            LocalDateTime localDT = timestamp.toLocalDateTime();
            ZonedDateTime zoneDT = localDT.atZone(ZoneId.of("UTC"));
            ZonedDateTime finalZDT = zoneDT.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            return finalZDT.toLocalDateTime();
        };
        try {
            Appointment app = new Appointment();
            Connection connection = JDBC.getConnection();
            PreparedStatement query = connection.prepareStatement(Queries.getDateTimeByAppID());
            query.setInt(1,appointment.getAppID());
            ResultSet result = query.executeQuery();
            result.next();
            Timestamp resultStart = result.getTimestamp("Start");
            Timestamp resultEnd = result.getTimestamp("End");
            Timestamp convertedStart = Timestamp.valueOf(dtConvert.convertDT(resultStart));
            Timestamp convertedEnd = Timestamp.valueOf(dtConvert.convertDT(resultEnd));
            appTitle.setText(appointment.getTitle());
            appDesc.setText(appointment.getDescription());
            appLocation.setText(appointment.getLocation());
            appType.setText(appointment.getType());
            appID.setText(String.valueOf(appointment.getAppID()));
            cName.getSelectionModel().select(appointment.getContactName());
            appCustomerID.setText(String.valueOf(appointment.getCustomerID()));
            appUserID.setText(String.valueOf(appointment.getUserID()));
            appStartDate.setValue(convertedStart.toLocalDateTime().toLocalDate());
            appStartTime.getSelectionModel().select(convertedStart.toLocalDateTime().toLocalTime());
            appEndDate.setValue(convertedEnd.toLocalDateTime().toLocalDate());
            appEndTime.getSelectionModel().select(convertedEnd.toLocalDateTime().toLocalTime());
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    /**
     * Method used to check that all data points are filled in.
     * @return All values to an observable list.
     */
    public ObservableList<String> checkInputs() {
        ObservableList<String> returnValue = FXCollections.observableArrayList();

        if (appTitle.getText().isEmpty()) { returnValue.add("Title"); }
        if (appDesc.getText().isEmpty()) { returnValue.add("Description"); }
        if (appLocation.getText().isEmpty()) { returnValue.add("Location"); }
        if (appType.getText().isEmpty()) { returnValue.add("Type"); }
        if (cName.getSelectionModel().getSelectedItem() == null) { returnValue.add("Contact Name"); }
        if (appCustomerID.getText().isEmpty()) { returnValue.add("Customer ID"); }
        if (appUserID.getText().isEmpty()) { returnValue.add("User ID"); }
        if (appStartDate.getValue() == null) { returnValue.add("Start Date"); }
        if (appStartTime.getSelectionModel().getSelectedItem() == null) { returnValue.add("Start Time"); }
        if (appEndDate.getValue() == null) { returnValue.add("End Date"); }
        if (appEndTime.getSelectionModel().getSelectedItem() == null) { returnValue.add("End Time"); }
        return returnValue;
    }

    /**
     * Contains two Lambda expressions for this method to save appointment data to the database. To ensure this method of saving appointments is efficient, it uses lambda expressions.
     * The first Lambda expression creates an object that implements the CheckDT interface. This object will take the date/time and convert it to EST to validate if it is within business hours.
     * The second lambda expression create an object that implements the CheckOverlap interface. This object will take the date/time and validate it does not conflict with another appointment.
     */
    public void saveNewData() {
        CheckDT checkDT = (instant) -> {
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            ZonedDateTime convertEST = zdt.withZoneSameInstant(ZoneId.of("America/New_York"));
            if(convertEST.getHour() < 8 || convertEST.getHour() > 22) {
                Alert checkDateTimeAlert =new Alert(Alert.AlertType.ERROR);
                checkDateTimeAlert.setTitle("Invalid time selection");
                checkDateTimeAlert.setContentText("Appointment is outside of business hours defined as 8:00 a.m. to 10:00 p.m. EST, including weekends");
                checkDateTimeAlert.showAndWait();
                return false;
            } else { return true; }
        };

        CheckOverlap overlap = (cID, aID, start, end) -> {
            Connection connection = JDBC.getConnection();
            boolean check = true;
            Alert olAlert = new Alert(Alert.AlertType.ERROR);
            olAlert.setTitle("Schedule Conflict");
            olAlert.setContentText("This date/time conflicts with an existing appointment for this customer.\n Please enter a different date/time");
            try {
                String query = null;
                if(Appointment.inModify == true) { query = Queries.getAppointmentsByCustomerIDForUpdate();
                } else { query = Queries.getAppointmentsByCustomerID(); }
                PreparedStatement overlapSTMNT = connection.prepareStatement(query);
                overlapSTMNT.setInt(1, cID);
                if(Appointment.inModify == true) {
                    overlapSTMNT.setInt(2, aID);
                }

                ResultSet results = overlapSTMNT.executeQuery();
                while(results.next()) {
                    LocalDateTime startCheck = results.getTimestamp("Start").toLocalDateTime();
                    LocalDateTime endCheck = results.getTimestamp("End").toLocalDateTime();
                    if((startCheck.isAfter(start) || startCheck.equals(start)) && startCheck.isBefore(end)) { check = false; }
                    else if ((startCheck.isBefore(start) || startCheck.isEqual(start)) && (endCheck.isAfter(end) || endCheck.isEqual(end))) { check = false; }
                    else if ((endCheck.isAfter(start) && endCheck.isBefore(end)) || endCheck.equals(end)) { check = false; }

                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            if (check == false) {
                olAlert.showAndWait();
            }
            return check;
        };

        try {
        Connection connection = JDBC.getConnection();
        int appointmentID = 0;
        if(Appointment.inModify == true) {
            appointmentID = Integer.parseInt(appID.getText());
        }
        int customerID = Integer.parseInt(appCustomerID.getText());

        int userID = Integer.parseInt(appUserID.getText());
        String title = appTitle.getText();
        String desc = appDesc.getText();
        String location = appLocation.getText();
        String type = appType.getText();
        String contact = (String) cName.getSelectionModel().getSelectedItem();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //Start date/time to UTC
        LocalDate startDate = appStartDate.getValue();
        LocalTime startTime = appStartTime.getSelectionModel().getSelectedItem();
        LocalDateTime formatStartDateTime = LocalDateTime.of(startDate, startTime);
        ZonedDateTime zonedStartDateTime = formatStartDateTime.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
        ZonedDateTime zonedStartDateTimeUTC = zonedStartDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime utcStart = zonedStartDateTimeUTC.toLocalDateTime();
        Instant startDateTime = Timestamp.valueOf(formatStartDateTime.format(dateTimeFormatter)).toInstant();

        //Ebd date/time to UTC
        LocalDate endDate = appEndDate.getValue();
        LocalTime endTime = appEndTime.getSelectionModel().getSelectedItem();
        LocalDateTime formatEndDateTime = LocalDateTime.of(endDate, endTime);
        ZonedDateTime zonedEndDateTime = formatEndDateTime.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
        ZonedDateTime zonedEndDateTimeUTC = zonedEndDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime utcEnd = zonedEndDateTimeUTC.toLocalDateTime();
        Instant endDateTime = Timestamp.valueOf(formatEndDateTime.format(dateTimeFormatter)).toInstant();

        //LocalDateTime utcStart = LocalDateTime.ofInstant(startDateTime, ZoneId.of("UTC"));
        //LocalDateTime utcEnd = LocalDateTime.ofInstant(endDateTime, ZoneId.of("UTC"));
        Instant formatNowDateTime = Timestamp.valueOf(LocalDateTime.now().format(dateTimeFormatter)).toInstant();
        LocalDateTime utcNow = LocalDateTime.ofInstant(formatNowDateTime, ZoneId.of("UTC"));

        if (startDateTime.equals(endDateTime)) {
            Alert olAlert = new Alert(Alert.AlertType.ERROR);
            olAlert.setTitle("Invalid Date/Time");
            olAlert.setContentText("The start Date/Time can't be the same as the end Date/Time.");
            olAlert.showAndWait();
            return;
        } else if (endDateTime.isBefore(startDateTime)) {
            Alert olAlert = new Alert(Alert.AlertType.ERROR);
            olAlert.setTitle("Invalid Date/Time");
            olAlert.setContentText("The end Date/Time can't be before the start Date/Time.");
            olAlert.showAndWait();
            return;
        }
        if (checkDT.validate(startDateTime) && checkDT.validate(endDateTime) && startDateTime.isBefore(endDateTime) && overlap.validate(customerID, appointmentID, utcStart, utcEnd)) {

                PreparedStatement getContact = connection.prepareStatement(Queries.getContactIdFromName());
                getContact.setString(1, contact);
                ResultSet result = getContact.executeQuery();
                result.next();
                int contactID = result.getInt("Contact_ID");
                PreparedStatement getUserName = connection.prepareStatement(Queries.getUserNameFromID());
                getUserName.setInt(1, Appointment.userID);
                ResultSet result2 = getUserName.executeQuery();
                result2.next();
                String username = result2.getString("User_Name");
                String appQuery = null;
                if (Appointment.inModify == false) {
                    appQuery = Queries.addAppointment();
                } else if (Appointment.inModify == true) {
                    appQuery = Queries.updateAppointment();
                }
                PreparedStatement addUpdateAppointment = connection.prepareStatement(appQuery);

                addUpdateAppointment.setString(1, title);
                addUpdateAppointment.setString(2, desc);
                addUpdateAppointment.setString(3, location);
                addUpdateAppointment.setString(4, type);
                addUpdateAppointment.setTimestamp(5, Timestamp.valueOf(utcStart));
                addUpdateAppointment.setTimestamp(6, Timestamp.valueOf(utcEnd));
                addUpdateAppointment.setTimestamp(7, Timestamp.valueOf(utcNow));
                addUpdateAppointment.setString(8, username);
                addUpdateAppointment.setInt(9, customerID);
                addUpdateAppointment.setInt(10, userID);
                addUpdateAppointment.setInt(11, contactID);

            if (Appointment.inModify == false) {
                    addUpdateAppointment.setTimestamp(12, Timestamp.valueOf(utcNow));
                    addUpdateAppointment.setString(13, username);
                } else if (Appointment.inModify == true) {
                    addUpdateAppointment.setInt(12, appointmentID);
                }

                int updateCheck = addUpdateAppointment.executeUpdate();

                if (updateCheck > 0) {
                    Stage stage = (Stage) ((Node) appointmentMainLabel).getScene().getWindow();
                    stage.close();
                    Appointment.inModify = false;
                } else {
                    System.out.println("Appointment was not added");
                }
            }
        } catch (SQLException exception) {
            Alert exceptionAlert = new Alert(Alert.AlertType.ERROR);
            exceptionAlert.setTitle("Database Error");
            exceptionAlert.setContentText(exception.getMessage());
            exceptionAlert.showAndWait();
        } catch (NumberFormatException exception) {
            Alert exceptionAlert = new Alert(Alert.AlertType.ERROR);
            exceptionAlert.setTitle("Invalid Data");
            exceptionAlert.setContentText("User ID and Customer ID must be numeric");
            exceptionAlert.showAndWait();
            System.out.println(exception.getMessage());
        } catch (NullPointerException exception) {
            Alert exceptionAlert = new Alert(Alert.AlertType.ERROR);
            exceptionAlert.setTitle("Missing Data");
            exceptionAlert.setContentText("Please verify all data is entered.");
            exceptionAlert.showAndWait();
        }
    }

    /**
     * Method to call the method to check inputs before proceeding to save the data.
     * @param actionEvent Save appointment
     */
    public void saveButtonClick(ActionEvent actionEvent) {
        ObservableList<String> validate = checkInputs();
        if(validate.size() > 0) {
            Alert olAlert = new Alert(Alert.AlertType.ERROR);
            olAlert.setTitle("Missing input data");
            String context = "The following data is missing from the form:\n";
            for (int i = 0; i < validate.size(); i++) {
                context = context + validate.get(i) + "\n";
            }
            olAlert.setContentText(context);
            olAlert.showAndWait();
        }
        else { saveNewData(); }

    }

}
