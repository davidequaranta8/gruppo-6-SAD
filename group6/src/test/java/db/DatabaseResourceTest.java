package db;

import group6.java.group6.controllers.TrackDialogController;
import group6.java.group6.db.DatabaseResource;
import group6.java.group6.models.Track;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DatabaseResourceTest {
    //get the instance of the resource
    DatabaseResource resource = DatabaseResource.getInstance();

    @Test
    public void getConnectionTest()
    {
        Connection sqlConnection = resource.getSqlConnection();
        assertNotNull(sqlConnection);
    }


}
