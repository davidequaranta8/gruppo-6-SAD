package group6.java.group6.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Singleton pattern applied here

public class DatabaseResource {

    private static DatabaseResource instance; //Shared instance
    private Connection sqlConnection; //connection object got from the driver


    private final String url = "jdbc:postgresql://localhost:5432/";
    private final String user = "user";
    private final String password = "password";


    //Private constructor to respect the singleton pattern
    private DatabaseResource() {
        try {
            this.sqlConnection = DriverManager.getConnection(url, user, password);
            initSchema(); //initialize the schema if not present
        } catch (SQLException e) {
            System.err.println("Errore di connessione: " + e.getMessage());
        }
    }

    //Static method provided to provide the instance of this class
    public static synchronized DatabaseResource getInstance() {
        if (instance == null) {
            instance = new DatabaseResource();
        }
        return instance;
    }

    //Useful method to obtain the real sql connection provided by the jdbc driver.
    //Example call will be: Connection realConnection = DatabaseConnection.getInstance().getSqlConnection()
    public Connection getSqlConnection() {
        return sqlConnection;
    }

    /*Method to create the schema automatically of the db if it does not exist.
    *The source file is "db_init.sql" in db folder
    */
    private void initSchema(){
        /*open  file .sql*/
        try (InputStream is = getClass().getResourceAsStream("db_init.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder sql = new StringBuilder();
            String line;
            /*loop through the lines of the opened file*/
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }

            try (Statement stmt = sqlConnection.createStatement()) {
                /*Execute the entire sql file read*/
                stmt.execute(sql.toString());
            }
        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
}