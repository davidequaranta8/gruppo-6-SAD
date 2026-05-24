package group6.java.group6.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Singleton pattern applied here

public class DatabaseConnection {

    private static DatabaseConnection instance; //Shared instance
    private Connection sqlConnection; //connection object got from the driver


    private final String url = "jdbc:postgresql://localhost:5432/";
    private final String user = "user";
    private final String password = "password";


    //Private constructor to respect the singleton pattern
    private DatabaseConnection() {
        try {
            this.sqlConnection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established");
        } catch (SQLException e) {
            System.err.println("Errore di connessione: " + e.getMessage());
        }
    }

    //Static method provided to provide the instance of this class
    public static synchronized Connection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance.getSqlConnection();
    }

    //Useful method to obtain the real sql connection provided by the jdbc driver.
    //Example call will be: Connection realConnection = DatabaseConnection.getInstance().getSqlConnection()
    private Connection getSqlConnection() {
        return sqlConnection;
    }
}