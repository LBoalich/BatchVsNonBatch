/*
 * Name: Batch vs Non-Batch
 * Author: Leah Boalich
 * Date: October 6, 2024
 * Assignment: Chapter 35 Excercise 1
 * Descritpion: This program connects to a database through user input into a popup box.  It uses random numbers to add 1000 records using batch and non-batch methods.  The time in nanoseconds it took to add the records using each method is displayed.
 */

/* Imports */
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application{
    // Attributes
    private Connection connection;
    private Statement statement;
    private Stage popup = new Stage();
    private Label lblStatus = new Label("Status");
    private TextArea taPerformance = new TextArea();
    private StringBuilder taPerformanceString = new StringBuilder();
    private Label lblConnectStatus = new Label("Database Status");
    private ComboBox<String> cboDrive = new ComboBox<>();
    private ComboBox<String> cboURL = new ComboBox<>();
    private TextField tfUser = new TextField();
    private TextField tfPass = new TextField();
    // Override the start method in the Application class
    public void start(Stage primaryStage) {
        // Create connect button
        Button btConnect = new Button("Connect to Database");
        // Create batch button
        Button btBatch = new Button("Batch Update");
        // Create non-batch button
        Button btNonBatch = new Button("Non-Batch Update");
        
        // Create border pane for status and connect
        BorderPane paneTop = new BorderPane();
        // Add status to left
        paneTop.setLeft(lblStatus);
        // Add connect button to right
        paneTop.setRight(btConnect);
      

        // Create hbox for batch and non batch buttons
        HBox hBoxBottom = new HBox(5);
        // Add batch and non batch buttons
        hBoxBottom.getChildren().addAll(btBatch, btNonBatch);
        // Center align the buttons
        hBoxBottom.setAlignment(Pos.CENTER);

        // Create boarder pane to hold all hboxes
        BorderPane pane = new BorderPane();
        
        // Add top border pane
        pane.setTop(paneTop);
        // Add performance text area
        pane.setCenter(taPerformance);
        // Add bottom hbox
        pane.setBottom(hBoxBottom);
        // Add padding
        pane.setPadding(new Insets(5));

        // Add event hanlders
        btConnect.setOnAction(e -> connect());
        // Add batch button event handler
        btBatch.setOnAction(e -> batch());
        // Add non-batch event handler
        btNonBatch.setOnAction(e -> nonBatch());

        /* Create scene and place it in the stage */
        Scene scene = new Scene(pane);
        // set the stage title
        primaryStage.setTitle("Exercise 35_01");
        // Place the scene in the stage
        primaryStage.setScene(scene);
        // Display the stage
        primaryStage.show();
    }

    private void connect() {
        /* Display default data */
         // Add item to drive cbo
        cboDrive.getItems().add("com.mysql.cj.jdbc.Driver");
        // Add item to URL cbo
        cboURL.getItems().add("jdbc:mysql://localhost/Ch35Ex1");
        // Set status label
        lblConnectStatus.setText("Enter info to connect");

        /* Create nodes */
        // Create drive label
        Label lbDrive = new Label("JDBC Drive", cboDrive);
        // Create url label
        Label lbURL = new Label("Database URL", cboURL);
        // Create username label
        Label lbUser = new Label("Username", tfUser);
        // Create password label
        Label lbPass = new Label("Password", tfPass);
        // Create connect button
        Button btConnect = new Button("Connect to DB");
        // Create close button
        Button btClose = new Button("Close Dialog");

        /* Add event handlers */
        // Add connect to db event handler
        btConnect.setOnAction(e -> connectDB());
        // Add close button event handler
        btClose.setOnAction(e -> close());
      
        /* Create panes */
        // Create connect hbox
        HBox hBoxConnect = new HBox(btConnect);
        hBoxConnect.setAlignment(Pos.CENTER_RIGHT);

        // Create close hbox
        HBox hBoxClose = new HBox(btClose);
        hBoxClose.setAlignment(Pos.CENTER);

        // Create vBox to hold all elements
        VBox vBox = new VBox();
        vBox.getChildren().addAll(lblConnectStatus, lbDrive, lbURL, lbUser, lbPass, hBoxConnect, hBoxClose);
        vBox.setPadding(new Insets(5));

         /* Create scene and place it in the stage */
        Scene scenePopup = new Scene(vBox);
        // Set popup title
        popup.setTitle("Connect to DB");
        // Place the scene in the popup
        popup.setScene(scenePopup);
        // Display popup
        popup.show();
    }

    private void close() {
        popup.close();
    }

    private void connectDB() {
        /* Get user input */
        // Get drive
        String drive = cboDrive.getValue();
        // Get URL
        String url = cboURL.getValue();
        //Get username
        String name = tfUser.getText();
        // Get password
        String password = tfPass.getText();

        /* Connect to database */
        try {
            // Load the JDBC driver
            Class.forName(drive);
            System.out.println("JDBC Driver loaded");
            lblConnectStatus.setText("Driver Loaded");

            // Establish a connection
            connection = DriverManager.getConnection(url, name, password);
            System.out.println("Database connected");
            lblConnectStatus.setText("Connected to " + url);

            // Create a statement
            statement = this.connection.createStatement();

            /* 
            // Create table
            stmt.executeUpdate("create table Temp(num1 double, num2 double, num3 double)");
            */

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void batch() {
        // Determine if batchUpdatesSupported is supported
        boolean batchUpdatesSupported = false;

        try {
            if (connection.getMetaData().supportsBatchUpdates()) {
                batchUpdatesSupported = true;
                System.out.println("batch updates supported");
            }
            else {
                System.out.println("Batch updates not supported");
            }
        }
        catch (UnsupportedOperationException ex) {
            System.out.println("The operation is not supported");
        }
        catch (SQLException ex) {
            System.out.println(ex);
        } 

        try{
            // Determine if the driver is capable of batch updates
            if (batchUpdatesSupported) {
                // Get current time
                LocalDateTime start = LocalDateTime.now();

                // Add inserts to batch
                int i = 0;
                while (i < 1000) {
                    // Get random numbers
                    double num1 = Math.random();
                    double num2 = Math.random();
                    double num3 = Math.random();

                    // Add statement to batch
                    statement.addBatch("insert into Temp (num1, num2, num3) values (" + num1 + ", " + num2 + ", " + num3 + ")");

                    // Increment i
                    i += 1;
                }  
                
                // Execute batch
                statement.executeBatch();
                // Get current time
                LocalDateTime stop = LocalDateTime.now();
                // Update status label
                lblStatus.setText("Batch updates succeeded");
                // Get performance time
                long nano = ChronoUnit.NANOS.between(start, stop);
                // Add info to performace string builder
                taPerformanceString.append("\n\nBatch update completed\nThe elapsed time is " + Long.toString(nano));
                // Set performance text area 
                taPerformance.setText(taPerformanceString.toString());
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    private void nonBatch() {
        // Get current time
        LocalDateTime start = LocalDateTime.now();

        try {
            int i = 0;
            while (i < 1000) {
                // Get random numbers
                double num1 = Math.random();
                double num2 = Math.random();
                double num3 = Math.random();

                // Execute statement
                statement.executeUpdate("insert into Temp (num1, num2, num3) values (" + num1 + ", " + num2 + ", " + num3 + ")");

                // Increment i
                i += 1;
            }
            // Get current time
            LocalDateTime stop = LocalDateTime.now();
            // Update status label
            lblStatus.setText("Non-Batch updates succeeded");
            // Get performance time
            long nano = ChronoUnit.NANOS.between(start, stop);
            // Add info to performance string builder
            taPerformanceString.append("\n\nNon-Batch update completed\nThe elapsed time is " + Long.toString(nano));
            // Set performance text area 
            taPerformance.setText(taPerformanceString.toString());
        }
        catch(SQLException ex) {
            System.out.println("ex");
        }
    }
}
