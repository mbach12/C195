package Model;

/**
 * This class is for constructing the report for appointments by Month/Type. It holds the constructor, getters, and setters for the class.
 */
public class ReportAppByMonthType {
    private int total;
    private String month, type;

    /**
     *
     * @param total Total appointments
     * @param month Appointment Month
     * @param type Appointment Type
     */
    public ReportAppByMonthType(int total, String month, String type) {
        this.total = total;
        this.month = month;
        this.type = type;
    }

    /**
     * Get the total number of appointments
     * @return Number of appointments
     */
    public int getTotal() {
        return total;
    }

    /**
     * Set the total number of appointments
     * @param total Number of appointments
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Get the appointment month
     * @return Appointment Month
     */
    public String getMonth() {
        return month;
    }

    /**
     * Set the appointment month
     * @param month Appointment month
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Get the appointment type
     * @return Appointment type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the appointment type
     * @param type Appointment type
     */
    public void setType(String type) {
        this.type = type;
    }
}
