package Model;

/**
 * This class is for constructing customers. It holds the constructor, getters, and setters for the class.
 */
public class Customer {
    private int id;
    private String name, address, postal, phone, div, country;

    /**
     *
     * @param id Customer ID
     * @param div Customer Division
     * @param name Customer Name
     * @param address Customer Address
     * @param postal Customer Postal Code
     * @param phone Customer Phone Number
     */
    public Customer(int id, String div, String name, String address, String postal, String phone, String country) {
        this.id = id;
        this.div = div;
        this.name = name;
        this.address = address;
        this.postal = postal;
        this.phone = phone;
        this.country = country;
    }

    /**
     * Method to get the customer country
     * @return Country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Method to set the customer country
     * @param country Country
     */
    public void setCountry(String country) {
        this.country = country;
    }
    /**
     * Method to get the customer ID
     * @return Customer ID
     */

    public int getId() {
        return id;
    }

    /**
     * Method to set the customer ID
     * @param id Customer ID
     */

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Method to get the customer division
     * @return Customer Division
     */
    public String getDiv() {
        return div;
    }

    /**
     * Method to set the customer division
     * @param div Customer Division
     */
    public void setDiv(String div) {
        this.div = div;
    }

    /**
     * Method to get the customer name
     * @return Customer Name
     */
    public String getName() {
        return name;
    }

    /**
     * Method to set the customer name
     * @param name Customer Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method to get the customer address
     * @return Customer Address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Method to set the customer address
     * @param address Customer address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Method to get the customer postal code
     * @return Customer Postal Code
     */
    public String getPostal() {
        return postal;
    }

    /**
     * Method to set the customer postal code
     * @param postal Customer Postal Code
     */
    public void setPostal(String postal) {
        this.postal = postal;
    }

    /**
     * Method to get the customer phone number
     * @return Customer Phone Number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Method to set the custmer phone number
     * @param phone Customer Phone Number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
