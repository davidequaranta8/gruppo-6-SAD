package db;

import group6.java.group6.controllers.TrackDialogController;
import group6.java.group6.db.DatabaseResource;
import group6.java.group6.models.Track;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseResourceTest {
    //get the instance of the resource
    DatabaseResource resource = DatabaseResource.getInstance();

    @Test
    public void getConnectionTest()
    {
        Connection sqlConnection = resource.getSqlConnection();
        assertNotNull(sqlConnection);
    }

/*Verify that requesting another instance means getting the same one was requested before*/
    @Test
    public void singletonTest() {
        DatabaseResource resource2 = DatabaseResource.getInstance();
        assertSame(resource, resource2);
    }


/*Assert connection is stable within 2 seconds from calling the method getSqlConnection*/
    @Test
    public void connectionIsValidTest() throws SQLException {
        Connection sqlConnection = resource.getSqlConnection();
        try {
            assertTrue(sqlConnection.isValid(2));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

/*Assert schema is correctly initialized after requesting the connection to the db through getSqlConnection on resource db */
    @Test
    public void schemaInitializedTest() throws SQLException {
        Connection conn = resource.getSqlConnection();
        var meta = conn.getMetaData();

        var playlists = meta.getTables(null, null, "playlist", null);
        assertTrue(playlists.next(), "Playlists table not found");

        var playlistTrack = meta.getTables(null, null, "playlist_track", null);
        assertTrue(playlistTrack.next(), "Playlist_track table not found");

        var track = meta.getTables(null, null, "track", null);
        assertTrue(track.next(), "Track table not found");
    }


}
