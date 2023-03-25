package Model;

/**
 * This class is for constructing the report for login activity. It holds the constructor, getters, and setters for the class.
 */

public class ReportLoginActivity {
    String type,status,user,notes,dt;

    /**
     *
     * @param type Type of activity
     * @param status Status of activity
     * @param user Activity User
     * @param notes Activity notes
     * @param dt Activity date/time
     */
    public ReportLoginActivity(String type, String status, String user, String notes, String dt) {
        this.type = type;
        this.status = status;
        this.user = user;
        this.notes = notes;
        this.dt = dt;
    }
    /**
     * Get the activity type
     * @return Activity type
     */
    public String getType() {
        return type;
    }
    /**
     * Set the activity type
     * @param type Activity type
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * Get the activity status
     * @return Activity Status
     */
    public String getStatus() {
        return status;
    }
    /**
     * Set the activity status
     * @param status Activity status
     */
    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * Get the activity user
     * @return Activity User
     */
    public String getUser() {
        return user;
    }
    /**
     * Set the activity user
     * @param user Activity User
     */
    public void setUser(String user) {
        this.user = user;
    }
    /**
     * Get the activity notes
     * @return Activity Notes
     */
    public String getNotes() {
        return notes;
    }
    /**
     *  Set the activity notes
     * @param notes Activity Notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
    /**
     * Get the activity date/time
     * @return Activity Date/Time
     */
    public String getDt() {
        return dt;
    }
    /**
     * Set the activity date/time
     * @param dt Activity Date/Time
     */
    public void setDt(String dt) {
        this.dt = dt;
    }
}
