package Helper;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * This class is used for the database connection. It stores all the parameters used to create
 * the Database Connection. Also stores the username and password for database connections.
 */
public abstract class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "sqlUser";
    private static final String password = "Passw0rd!";
    public static Connection connection;

    /**
     * Getter method to return the open connection to the database.
     * @return open database connection
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Method to open the connection to the database. This will throw an  error if the driver
     * is not found or if it can not connect to the database.
     */
    public static void openConnection()
    {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        }
        catch(Exception exception)
        {
            Alert exceptionAlert = new Alert(Alert.AlertType.ERROR);
            exceptionAlert.setTitle("Database Error");
            exceptionAlert.setContentText(exception.getMessage());
            exceptionAlert.showAndWait();
        }
    }

    /**
     * Method used to close the connection to the database.
     */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception exception)
        {
            Alert exceptionAlert = new Alert(Alert.AlertType.ERROR);
            exceptionAlert.setTitle("Database Error");
            exceptionAlert.setContentText(exception.getMessage());
            exceptionAlert.showAndWait();
        }
    }


}
