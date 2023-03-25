package Helper;

/**
 * Class used to hold all queries used by the application
 */
public class Queries {
    /**
     * Method to return a query that selects all users.
     * @return Query to get usernames
     */
    public static String getUsernames() { return "SELECT User_Name FROM users"; }
    /**
     * Method to return a query that selects the password for the provided user name.
     * @return Query to get password for username
     */
    public static String getPassword() { return "SELECT Password FROM users WHERE User_Name = ?"; }
    /**
     * Method to return a query that selects appointments for the specific contact id
     * @return Query to get appointments for specified contact id.
     */
    public static String getAppointmentsByContact() { return "Select * FROM appointments WHERE Contact_ID = ?"; }
    /**
     * Method to return a query that selects all appointments
     * @return Query to get all appointments
     */
    public static String getAllAppointments() { return "SELECT * FROM appointments"; }
    /**
     * Method to return a query that counts the number of appointments for a customer id
     * @return Query to count number of appointments for a customer id.
     */
    public static String getCountAppointsmentsByCustomerID() { return "SELECT COUNT(*) as count FROM appointments WHERE Customer_ID = ?"; }
    /**
     * Method to return a query that selects the user id for the specified user name.
     * @return Query to get the user id for the specified user name.
     */
    public static String getUserId() { return "SELECT User_ID FROM users WHERE User_Name = ?"; }
    /**
     * Method to return a query that selects the user name for the specified user id.
     * @return Query to get the user name for the specified user id.
     */
    public static String getUserNameFromID() { return "SELECT User_Name FROM users WHERE User_ID = ?"; }
    /**
     * Method to return a query that selects the contact name for the specified contact id.
     * @return Query to get the contact name for the specified contact id
     */
    public static String getContactNameById() { return "SELECT Contact_Name FROM contacts WHERE Contact_ID = ?"; }
    /**
     * Method to return a query that selects the contact name for all contacts
     * @return Query to get the contact name for all contacts.
     */
    public static String getContactNames() { return "SELECT Contact_Name FROM contacts"; }
    /**
     * Method to return a query that selects the contact id for a specific contact name.
     * @return Query to get the contact id for a specific contact name.
     */
    public static String getContactIdFromName() { return "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?"; }
    /**
     * Method to return a query that inserts a new appointment into the appointment table.
     * @return Query to insert a new appointment into the appointment table.
     */
    public static String addAppointment() {
        return " INSERT INTO appointments (Title, Description, Location, Type, Start, End, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID, Create_Date, Created_By)" +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";}
    /**
     * Method to return a query that updates an appointment in the appointment table.
     * @return Query to updates an appointment in the appointment table.
     */
    public static String updateAppointment() {
        return "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, " +
                "Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
    }
    /**
     * Method to return a query that inserts a new customer into the customers table.
     * @return Query to insert a new customer into the customers table.
     */
    public static String addCustomer() { return "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Last_Update, Last_Updated_By, Division_ID, Create_Date, Created_By)" +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";}
    /**
     * Method to return a query that updates a customer in the customers table.
     * @return Query to update a customer in the customers table.
     */
    public static String updateCustomer() { return "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ?" +
            " WHERE Customer_ID = ?";}
    /**
     * Method to return a query to get the appointment id, start and end date/time by customer id.
     * @return Query to get the appointment id, start and end date/time by customer id.
     */
    public static String getAppointmentsByCustomerID() { return "SELECT Appointment_ID, Start, End FROM appointments WHERE Customer_ID = ?"; }
    /**
     * Method to return a query to get the start and end date/time for appointments by appointment id.
     * @return Query to get the start and end date/time for appointments by appointment id.
     */
    public static String getDateTimeByAppID() { return "SELECT Start, End FROM appointments WHERE Appointment_ID = ?"; }
    /**
     * Method to return a query to delete an appointment by appointment id.
     * @return Query to delete an appointment by appointment id.
     */
    public static String delAppointment() { return "DELETE FROM appointments WHERE Appointment_ID = ?"; }
    /**
     * Method to return a query to delete a customer by customer id.
     * @return Query to delete a customer by customer id.
     */
    public static String delCustomer() { return "DELETE FROM customers WHERE Customer_ID = ?"; }
    /**
     * Method to return a query to get all customers
     * @return Query to get all customers
     */
    public static String getCustomers(){ return "SELECT C.Customer_ID as Customer_ID, C.Customer_Name as Customer_Name, C.Address as Address, C.Postal_Code as Postal_Code,\n" +
            "C.Phone as Phone, C.Division_ID as Division_ID, CO.Country as Country FROM customers C\n" +
            "LEFT JOIN first_level_divisions FLD\n" +
            "on FLD.Division_ID = C.Division_ID\n" +
            "LEFT JOIN countries CO\n" +
            "ON CO.Country_ID = FLD.COUNTRY_ID\n" +
            "\n"; }
    /**
     * Method to return a query to get the country id and division for a specific division id.
     * @return Query to get the country id and division for a specific division id.
     */
    public static String getDivisionName() { return "SELECT COUNTRY_ID, Division FROM first_level_divisions where Division_ID = ?"; }
    /**
     * Method to return a query to get all countries
     * @return Query to get all countries
     */
    public static String getCountries() { return "SELECT Country FROM countries";}
    /**
     * Method to return a query to get the country id for a specific country.
     * @return Query to get the country id for a specific country.
     */
    public static String getCountryIdByName() { return "SELECT Country_ID from countries WHERE Country = ?"; }
    /**
     * Method to return a query to get the country name from a specific country id.
     * @return Query to get the country name from a specific country id.
     */
    public static String getCountryNameByID() { return "SELECT Country FROM countries WHERE Country_ID = ?"; }
    /**
     * Method to return a query to get the division(s) for a specific country id.
     * @return Query to get the division(s) for a specific country id.
     */
    public static String getDivisionNameByCountryID() { return "SELECT Division FROM first_level_divisions where COUNTRY_ID = ?"; }
    /**
     * Method to return a query to get all customer data for a specific customer id
     * @return Query to get all customer data for a specific customer id
     */
    public static String getCustomerByID() { return "SELECT * FROM customers WHERE Customer_ID = ?"; }
    /**
     * Method to return a query to get the division id for a specific division and country name.
     * @return Query to get the division id for a specific division and country name.
     */
    public static String getDivIdForCountryState() {
        return "SELECT FLD.Division_ID FROM first_level_divisions FLD " +
                "INNER JOIN countries C " +
                "ON FLD.Country_ID = C.COUNTRY_ID " +
                "WHERE FLD.DIVISION = ? AND Country = ?";
    }
    /**
     * Method to return a query to get number of appointments by month and type.
     * @return Query to get number of appointments by month and type.
     */
    public static String reportAppByMonthType() { return "SELECT monthname(Start) as month, Type as type, count(Appointment_ID) as total FROM appointments\n" +
            "GROUP BY monthname(Start), Type\n" +
            "ORDER BY FIELD(month,'January','February','March','April','May','June','July','August','September','October','November','December')";}

}

