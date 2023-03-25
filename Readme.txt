
- Title: C195 Scheduling Application

- Purpose: To provide a user interface for a desktop scheduling application. The UI can add/modify/delete customers and appointments from the database.
The UI is also capable of generating reports, notifying a user if there is a scheduled appointment within 15 minutes of login, and scheduling guidance
so appointments are scheduled during the appropriate times.

- Author: Michael Bachman
- Email:  mbach12@wgu.edu
- Application Version: 1.0
- Date: 3/25/2023

- IDE Version: IntelliJ IDEA 2022.3.1 (Ultimate Edition)
- JDK Version: Java(TM) SE Runtime Environment (build 17.0.5+9-LTS-191)
- JavaFX Version: JavaFX-SDK-17.0.6
- MySQL Connector Driver Version: mysql-connector-java-8.0.32

- How to use the application:
    -Pre-requisites: JDK/JavaFX/MySQL Connection versions as outlined above.
    -Setting up IDE environment: Open the project in IntelliJ
                                 Go to File -> Settings -> Appearance & Behavior -> Path Variables and insert the following record below:
                                        Name: PATH_TO_FX
                                        Value: The path to the JavaFX lib folder (eg. C:\Program Files\javafx-sdk-17.0.6\lib)
                                 Edit the run/ debug configuration
                                        VM Options: "--module-path ${PATH_TO_FX} --add-modules javafx.fxml,javafx.controls,javafx.graphics"
                                        JDK: 17.0.5
                                        Main Class: Main
                                 Ensure the JavaFX & MySQL connector libraries are in your project structure and pointed to the correct directories
                                    Go to File -> Project Structure -> Libraries
    -Running the application: Select the run (play) icon from the main bar OR select Run -> Run 'Main' from the top menu
    -Using the application:
                               1) Login using valid credentials from the database and click 'Login'. If invalid credentials are provided and error message will prompt.
                               2) A popup message will display to notify the user if an appointment is in the next 15 minutes or not
                               3) The UI will list all of the current appointments
                                    3a) To filter the list of appointments by current month, select the 'By Month' radio button.
                                    3b) To filter the list of appointments by current week, select the 'By Week' radio button.
                               4) Adding an appointment
                                    4a) Click the 'Add' button on the appointment screen.
                                    4b) Fill in all of the information for the appointment.
                                    4c) Click save and the appointment will save or prompt the user to correct the inputs (ie. Scheduling out of business hours or overlapping appointments for customers)
                               5) Updating an appointment
                                    5a) Select an appointment in the list and then click the 'Update' button on the appointment screen.
                                    5b) Change any information on the appointment as needed. The same validations will apply in steps 4c.
                               6) Delete an appointment
                                    6a) Select an appointment in the list and then click the 'Delete' button on the appointment screen.
                               7) Viewing reports
                                    7a) Click on the 'Reports' button on the appointment screen.
                                    7b) Select the report you would like to view by clicking the appropriate tab.
                                    7c) Reports are refreshed whenever the reports page is loaded from the main appointment screen.
                                        7c1) Contact schedule report is re-loaded whenever a different contact is selected.
                                    7d) Exit the report screen by clicking the 'Back to appointments' button.
                               8) Customers - To view customer click the 'Customers' button on the appointment screen
                                    8a) To add a customer click the 'Add' button.
                                        8a1) Fill in all of the information for the customer.
                                        8a2) Click save and the customer will save
                                    8b) To update a customer click the 'Update' button
                                        8b1) Change any information for the customer as needed then click save.
                                    8c) To delete a customer click the 'Delete' button after selecting an appointment in the list
                               9) To logout click 'Logout' from the appointment screen.

- Additional Report: The additional report I chose to generate is the login activity report. This report parses the login_activity.txt file to populate the data
into the table. Since the login_activity.txt file records the timezone the activity was recorded in, it will convert that date/time to the user's local date/time
if the application is opened in a different time zone.This is very useful to display in a user-friendly format. In future versions it would be better to record
login activity in the database.