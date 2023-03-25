package Model;

/**
 * This class is for constructing appointments. It holds the constructor, getters, and setters for the class.
 */
public class Appointments {
    private int appID, customerID, userID, contactID;
    private String title, description, location, contactName, type, start, end;

    /**
     *
     * @param appID Appointment ID
     * @param customerID Customer ID for the appointment
     * @param userID User ID for the appointment
     * @param contactID Contact ID for the appointment
     * @param title Appointment Title
     * @param description Appointment Description
     * @param location Appointment Location
     * @param contactName Appointment's contact name
     * @param type Appointment type
     * @param start Appointment Start date/time
     * @param end Appointment End date/time
     */
    public Appointments(int appID, int customerID, int userID, int contactID, String title, String description, String location, String contactName, String type, String start, String end) {
        this.appID = appID;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactName = contactName;
        this.type = type;
        this.start = start;
        this.end = end;
    }

    /**
     * Method to get the appointment ID
     * @return Appointment ID
     */
    public int getAppID() {
        return appID;
    }

    /**
     * Method to set the appointment ID
     * @param appID Appointment ID
     */
    public void setAppID(int appID) {
        this.appID = appID;
    }
    /**
     * Method to get the customer ID
     * @return Customer ID
     */
    public int getCustomerID() {
        return customerID;
    }
    /**
     * Method to set the customer ID
     * @param customerID Customer ID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    /**
     * Method to get the User ID
     * @return User ID
     */
    public int getUserID() {
        return userID;
    }
    /**
     * Method to set the user ID
     * @param userID User ID
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }
    /**
     * Method to get the Contact ID
     * @return Contact ID
     */
    public int getContactID() {
        return contactID;
    }
    /**
     * Method to set the contact ID
     * @param contactID Contact ID
     */
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }
    /**
     * Method to get the appointment title
     * @return Appointment Title
     */
    public String getTitle() {
        return title;
    }
    /**
     * Method to set the appointment title
     * @param title Appointment Title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * Method to get the appointment description
     * @return Appointment Description
     */
    public String getDescription() {
        return description;
    }
    /**
     * Method to set the appointment description
     * @param description Appointment Description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Method to get the appointment location
     * @return Appointment Location
     */
    public String getLocation() {
        return location;
    }
    /**
     * Method to set the appointment location
     * @param location Appointment Location
     */
    public void setLocation(String location) {
        this.location = location;
    }
    /**
     * Method to get the appointment contact name
     * @return Contact name
     */
    public String getContactName() {
        return contactName;
    }
    /**
     * Method to set the appointment contact name
     * @param contactName Appointment Contact Name
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    /**
     * Method to get the appointment type
     * @return Appointment Type
     */

    public String getType() {
        return type;
    }
    /**
     * Method to set the appointment type
     * @param type Appointment Type
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * Method to get the appointment start date/time
     * @return Appointment start date/time
     */
    public String getStart() {
        return start;
    }
    /**
     * Method to set the appointment start date/time
     * @param start Appointment start date/time
     */
    public void setStart(String start) {
        this.start = start;
    }
    /**
     * Method to get the appointment end date/time
     * @return Appointment End Date/Time
     */
    public String getEnd() {
        return end;
    }
    /**
     * Method to set the appointment end date/time
     * @param end Appointment End Date/Time
     */
    public void setEnd(String end) {
        this.end = end;
    }




}
