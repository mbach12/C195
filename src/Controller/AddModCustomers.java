package Controller;

import Helper.JDBC;
import Helper.Queries;

import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Class that is used to add or modify customers.
 */
public class AddModCustomers implements Initializable {
    /**
     * Add / Modify customer label
     */
    public Label customerLabel;
    /**
     * Country list
     */
    public ComboBox cList;
    /**
     * State list
     */
    public ComboBox sList;
    /**
     * Customer ID
     */
    public TextField cID;
    /**
     * Customer Name
     */
    public TextField cName;
    /**
     * Customer Address
     */
    public TextField cAddress;
    /**
     * Customer postal code
     */
    public TextField cPostal;
    /**
     * Customer Phone Number
     */
    public TextField cPhone;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(Customers.inModify.equals(false)) { this.customerLabel.setText("Add Customer");}
        if(Customers.inModify.equals(true)) { this.customerLabel.setText("Modify Customer");}
        getCountries();
    }
    /**
     * Method used to check that all data points are filled in.
     * @return All values to an observable list.
     */
    public ObservableList<String> checkInputs() {
        ObservableList<String> returnValue = FXCollections.observableArrayList();

        if (cName.getText().isEmpty()) { returnValue.add("Name"); }
        if (cAddress.getText().isEmpty()) { returnValue.add("Address"); }
        if (cPostal.getText().isEmpty()) { returnValue.add("Postal"); }
        if (cPhone.getText().isEmpty()) { returnValue.add("Phone"); }
        return returnValue;
    }
    /**
     * Method to save customer data to the database.
     */
    public void saveNewData(){
        try {
            Connection connection = JDBC.getConnection();
            int id = 0;
            String name = cName.getText();
            String address = cAddress.getText();
            String postal = cPostal.getText();
            String phone = cPhone.getText();
            String country = cList.getSelectionModel().getSelectedItem().toString();
            String state = sList.getSelectionModel().getSelectedItem().toString();
            String query = null;
            if(Customers.inModify == false) { query = Queries.addCustomer(); }
            else if (Customers.inModify == true) { 
                query = Queries.updateCustomer();
                id = Integer.parseInt(cID.getText());
            }
            PreparedStatement getUserName = connection.prepareStatement(Queries.getUserNameFromID());
            getUserName.setInt(1,Appointment.userID);
            ResultSet resultUsername = getUserName.executeQuery();
            resultUsername.next();
            String username = resultUsername.getString("User_Name");
            PreparedStatement getDivID = connection.prepareStatement(Queries.getDivIdForCountryState());
            getDivID.setString(1,state);
            getDivID.setString(2,country);
            ResultSet resultDivID = getDivID.executeQuery();
            resultDivID.next();
            int divID = resultDivID.getInt("Division_ID");
            PreparedStatement addUpdateQuery = connection.prepareStatement(query);
            addUpdateQuery.setString(1,name);
            addUpdateQuery.setString(2,address);
            addUpdateQuery.setString(3,postal);
            addUpdateQuery.setString(4,phone);
            addUpdateQuery.setTimestamp(5,Timestamp.valueOf(LocalDateTime.now()));
            addUpdateQuery.setString(6,username);
            addUpdateQuery.setInt(7,divID);
            if(Customers.inModify == false) {
                addUpdateQuery.setTimestamp(8,Timestamp.valueOf(LocalDateTime.now()));
                addUpdateQuery.setString(9,username);
            } else if (Customers.inModify == true) {
                addUpdateQuery.setInt(8,id);
            }
            int updateCheck = addUpdateQuery.executeUpdate();
            if(updateCheck > 0) {
                Stage stage = (Stage) ((Node) cPhone).getScene().getWindow();
                stage.close();
                Customers.inModify = false;
            } else {
                System.out.println("Appointment was not added");
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }
    /**
     * Method used to get data for the specific customer ID that is being modified.
     * @param customer Customer to modify
     */
    public void getData(Customer customer) {
        try {
            Connection connection = JDBC.getConnection();
            PreparedStatement query = connection.prepareStatement(Queries.getCustomerByID());
            query.setInt(1,customer.getId());
            ResultSet result = query.executeQuery();
            result.next();
            int id = result.getInt("Customer_ID");
            String name = result.getString("Customer_Name");
            String address = result.getString("Address");
            String postal = result.getString("Postal_Code");
            String phone = result.getString("Phone");
            int dID = result.getInt("Division_ID");
            PreparedStatement queryGetDiv = connection.prepareStatement(Queries.getDivisionName());
            queryGetDiv.setInt(1, dID);
            ResultSet resultDiv = queryGetDiv.executeQuery();
            resultDiv.next();
            String dName = resultDiv.getString("Division");
            int countryID = resultDiv.getInt("COUNTRY_ID");
            PreparedStatement queryGetCon = connection.prepareStatement(Queries.getCountryNameByID());
            queryGetCon.setInt(1,countryID);
            ResultSet resultCountry = queryGetCon.executeQuery();
            resultCountry.next();
            String countryName = resultCountry.getString("Country");
            cID.setText(String.valueOf(id));
            cName.setText(name);
            cAddress.setText(address);
            cPostal.setText(postal);
            cPhone.setText(phone);
            sList.getSelectionModel().select(dName);
            cList.getSelectionModel().select(countryName);

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    /**
     * Method to get the country list from the DB and populate in the Country combo box.
     */
    public void getCountries() {
        try {
            ObservableList<String> countryStringList = FXCollections.observableArrayList();
            Connection connection = JDBC.getConnection();
            PreparedStatement query = connection.prepareStatement(Queries.getCountries());
            ResultSet results = query.executeQuery();
            while(results.next()) {
                String cName = results.getString("Country");
                countryStringList.add(cName);
            }

            cList.setItems(countryStringList);
            cList.getSelectionModel().selectFirst();
            getStates();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    /**
     * Method to get the state list from the DB and populate in the Country combo box.
     */
    public void getStates() {
        try {
            String selectedCountry = (String) cList.getSelectionModel().getSelectedItem();
            ObservableList<String> stateStringList = FXCollections.observableArrayList();
            Connection connection = JDBC.getConnection();
            PreparedStatement getIdQuery = connection.prepareStatement(Queries.getCountryIdByName());
            getIdQuery.setString(1,selectedCountry);
            ResultSet idResults = getIdQuery.executeQuery();
            idResults.next();
            int cID = idResults.getInt("Country_ID");
            PreparedStatement getDivquery = connection.prepareStatement(Queries.getDivisionNameByCountryID());
            getDivquery.setInt(1,cID);
            ResultSet results = getDivquery.executeQuery();
            while (results.next()) {
                String dName = results.getString("Division");
                stateStringList.add(dName);
            }
            sList.setItems(stateStringList);
            sList.getSelectionModel().selectFirst();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    /**
     * Method to call the method to validate the data and then save the data.
     * @param actionEvent
     */
    public void saveClick(ActionEvent actionEvent) {

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

    /**
     * Method to close the add and modify customer screen and go back to the appointment screen.
     * @param actionEvent
     */
    public void cancelClick(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) customerLabel).getScene().getWindow();
        stage.close();
    }

    /**
     * Method to re-populate the state list when a country is selected.
     * @param actionEvent
     */
    public void cListClick(ActionEvent actionEvent) {
        getStates();
    }
}
