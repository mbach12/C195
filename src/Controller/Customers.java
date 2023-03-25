package Controller;

import Helper.JDBC;
import Helper.Queries;
import Helper.SetData;
import Model.Customer;
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
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Class that is used to display all the customers, and load other controllers.
 */
public class Customers implements Initializable {
    /**
     * Customer table
     */
    public TableView<Customer> customersTable;
    /**
     * Customer ID
     */
    public TableColumn<Customer, Integer> customersID;
    /**
     * Customer Name
     */
    public TableColumn<Customer, String> customersName;
    /**
     * Customer address
     */
    public TableColumn<Customer, String> customersAddress;
    /**
     * Customer Division
     */
    public TableColumn<Customer, String> customersDiv;
    /**
     * Customer postal code
     */
    public TableColumn<Customer, String> customersPostal;
    /**
     * Customer phone number
     */
    public TableColumn<Customer, String> customersPhone;
    /**
     * List of customers
     */
    public final ObservableList<Customer> customersList = FXCollections.observableArrayList();
    /**
     * Check if a customer is currently being modified
     */
    public static Boolean inModify = false;
    /**
     * Check if the listener is already enabled
     */
    public boolean listenerEnabled = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {  getCustomer.setData();  }

    /**
     * This method is used to check how many appointments a customer has and return that number.
     * @return Number of appointments the customer has.
     */
    public int checkCustomerAppointments() {
        int count = 0;
        try {
            Connection connection = JDBC.getConnection();
            Customer customer = customersTable.getSelectionModel().getSelectedItem();
            int id = customer.getId();
            PreparedStatement getAppCount = connection.prepareStatement(Queries.getCountAppointsmentsByCustomerID());
            getAppCount.setInt(1,id);
            ResultSet resultUsername = getAppCount.executeQuery();
            resultUsername.next();
            count = resultUsername.getInt("count");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return count;
    }

    /**
     * This method adds a listener to the appointment screen. The listener validates whenever the focus changes on the appointment screen and refreshes the data in the tableview.
     */
    public void addListener() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/Customers.fxml"));
        Stage stage = (Stage) ((Node) customersTable).getScene().getWindow();
        stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            @FXML
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean onHidden, Boolean onShown) {
                refreshTable();
            }
        });
    }

    /**
     * Method used to refresh/repopulate the customer table.
     */
    public void refreshTable() {
        customersTable.refresh();
        customersTable.getItems().clear();
        getCustomer.setData();

    }
    /**
     * This is a Lambda expression that creates an object that implements the SetData interface. This method will get the customer data from the DB and populate it in the customer table.
     */
        SetData getCustomer = () -> {
            try {
                Connection connection = JDBC.getConnection();
                PreparedStatement customerQuery = connection.prepareStatement(Queries.getCustomers());
                ResultSet results = customerQuery.executeQuery();
                while(results.next()) {
                    int cID = results.getInt("Customer_ID");
                    String cName = results.getString("Customer_Name");
                    String cAddress = results.getString("Address");
                    String cPostal = results.getString("Postal_Code");
                    String cPhone = results.getString("Phone");
                    int cDivID = results.getInt("Division_ID");
                    PreparedStatement dNameQuery = connection.prepareStatement(Queries.getDivisionName());
                    dNameQuery.setInt(1,cDivID);
                    ResultSet resultsDName = dNameQuery.executeQuery();
                    resultsDName.next();
                    String cDName =  resultsDName.getString("Division");
                    Customer Customers = new Customer(cID, cDName, cName, cAddress, cPostal, cPhone);
                    customersList.add(Customers);
                }
                customersID.setCellValueFactory(new PropertyValueFactory<>("id"));
                customersName.setCellValueFactory(new PropertyValueFactory<>("name"));
                customersPostal.setCellValueFactory(new PropertyValueFactory<>("postal"));
                customersAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
                customersPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
                customersDiv.setCellValueFactory(new PropertyValueFactory<>("div"));
                customersTable.setItems(customersList);

            } catch(SQLException sqlException) {
                sqlException.printStackTrace();
            }

        };

    /**
     * This is a method to open the Add customer screen.
     * @param actionEvent Show add customer screen.
     */
    public void addClick(ActionEvent actionEvent) throws IOException {
        if(listenerEnabled == false) {
            addListener();
            listenerEnabled = true;
        }
        Customers.inModify = false;
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/AddModCustomers.fxml")));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Customers");
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * This is a method to open the modify customer screen. It also validates that a customer is selected before proceeding.
     * @param actionEvent Show modify customer screen.
     */
    public void updateClick(ActionEvent actionEvent) throws IOException {
        try {
            if(listenerEnabled == false) {
                addListener();
                listenerEnabled = true;
            }
            Customer customer = (Customer) customersTable.getSelectionModel().getSelectedItem();
            if (customer == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Modify Customer");
                alert.setContentText("No Customer Selected");
                alert.showAndWait();
                return;
            }
            Customers.inModify = true;
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/AddModCustomers.fxml")));
            Parent root = loader.load();
            AddModCustomers addModCustomers = loader.getController();
            addModCustomers.getData(customersTable.getSelectionModel().getSelectedItem());
            Stage stage = new Stage();
            stage.setTitle("Customers");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {

        }
    }

    /**
     * Method to delete a selected customer. This method validates that a customer is selected before deleting the customer.
     */
    public void deleteCustomer() {
        Customer customerSelected = (Customer) customersTable.getSelectionModel().getSelectedItem();
        if(customerSelected == null) {
            Alert olAlert = new Alert(Alert.AlertType.ERROR);
            olAlert.setTitle("Customer not selected");
            olAlert.setContentText("Please select a customer to delete it.");
            olAlert.showAndWait();
        } else {
            int cID = customerSelected.getId();
            String cName = customerSelected.getName();
            try {
                String message = "Would you like to delete the selected customer?\n" +
                        "Customer ID: " + cID + "\n" +
                        "Customer Name: " + cName;
                if (Appointment.confirmBox("Delete Customer","Please Confirm", message)) {
                    Connection connection = JDBC.getConnection();
                    PreparedStatement delCustomer = connection.prepareStatement(Queries.delCustomer());
                    delCustomer.setInt(1,cID);
                    int delRows = delCustomer.executeUpdate();
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    if(delRows > 0) {
                        info.setTitle("Customer Deleted");
                        info.setContentText("Customer " + cName + " has been deleted successfully.");
                    } else {
                        info.setTitle("Customer Not Deleted");
                        info.setContentText("Customer " + cName + " was not deleted successfully.");
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
     * Method that calls other methods to: Validate the selected customer to delete has no appointments and then delete the customer.
     * @param actionEvent
     */
    public void deleteClick(ActionEvent actionEvent) {
        int appCount = checkCustomerAppointments();
        if (appCount > 0) {
            Alert olAlert = new Alert(Alert.AlertType.ERROR);
            olAlert.setTitle("Customer has appointments");
            olAlert.setContentText("Customer has " + appCount + " appointment(s) and can't be deleted until the appointments are deleted.");
            olAlert.showAndWait();
        } else {
            deleteCustomer();
        }
    }

    /**
     * Method to close the customer screen and return to the appointment screen.
     * @param actionEvent Close customer screen.
     */
    public void closeClick(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) customersTable).getScene().getWindow();
        stage.close();
    }
}
