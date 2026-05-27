package group6.java.group6.dao;

import group6.java.group6.db.DatabaseResource;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.Track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/*Responsible to interact with db for everything concerns playlist*/
public class PlaylistDao implements Dao<Playlist , Integer>{

    private final Connection sqlConnection;

    /*When creating get the connection to db*/
    public PlaylistDao() {
        this.sqlConnection = DatabaseResource.getInstance().getSqlConnection();
    }


    /*Get the playlist object with id passed.Here as below , the object return is only the playlist without songs
    that belong to it since this last thing is made through TrackDao
    * */
    @Override
    public Optional<Playlist> get(Integer id) {
        String sql = "SELECT * FROM Playlist WHERE id = ?";
        try {
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            /*If any results: */
            if (rs.next()) {
                /*Save the object playlist passing only title since when creating count_played defaults to 0*/
                Playlist playlist = new Playlist(
                        rs.getString("title")
                );
                /*set countPlayed attribute*/
                playlist.setCountPlayed(rs.getInt("count_played"));
                /*set Id attribute*/
                playlist.setId(rs.getInt("id"));
                return Optional.of(playlist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*return Optional empy if no results found*/
        return Optional.empty();
    }

    /*Get all the playlists saved*/
    @Override
    public Set<Playlist> getAll() {
        String sql = "SELECT * FROM Playlist";
        Set<Playlist> playlists = new HashSet<>();
        try {
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Playlist playlist = new Playlist(rs.getString("title"));
                /*set count_played attribute*/
                playlist.setCountPlayed(rs.getInt("count_played"));
                /*set id attribute for the model in memory*/
                playlist.setId(rs.getInt("id"));

                /*finally add the playlist to set to return*/
                playlists.add(playlist);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    /*Not add ID because serial so it increments automatically. After insertion in db insert the id in playlist model*/
    @Override
    public void save(Playlist playlist) {
        String sql = "INSERT INTO Playlist (title, count_played) VALUES (?, ?)";
        try {
            PreparedStatement stmt = sqlConnection.prepareStatement(sql,
                    PreparedStatement.RETURN_GENERATED_KEYS); /*returns the id*/
            stmt.setString(1, playlist.getTitle());
            stmt.setInt(2, playlist.getCountPlayed());
            stmt.executeUpdate();

            /*get the id generated and set it in playlist model*/
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                playlist.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Playlist playlist) {
        String sql = "DELETE FROM Playlist WHERE id = ?";

        try{
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            stmt.setInt(1, playlist.getId());
            stmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    /*Playlist passed is already the one updated for this reason we need only to update it in db*/
    @Override
    public void update(Playlist playlist) {
        String sql = "UPDATE Playlist SET title = ?, count_played = ? WHERE id = ?";
        try {
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            stmt.setString(1, playlist.getTitle());
            stmt.setInt(2, playlist.getCountPlayed());
            stmt.setInt(3, playlist.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    * Increments the count played of a playlist both
    * in the db record and the model to keep coherent both sides
    * */
    public void incrementCountPlayed(Playlist playlist) {
        String sql = "UPDATE Playlist SET count_played = count_played + 1 WHERE id = ?";
        try (PreparedStatement stmt = sqlConnection.prepareStatement(sql)) {
            stmt.setInt(1, playlist.getId());
            stmt.executeUpdate();
            playlist.incrementCountPlayed(); /*update also the counter of model*/
        } catch (SQLException e) {
            //TODO: define a proper exception to handle here
            e.printStackTrace();
        }


    }


    /*Method for adding a track in a playlist*/
    public void addTrack(Playlist playlist ,  Track track) {
        String sql = "INSERT INTO playlist_track  VALUES (?, ?)";
        try (PreparedStatement stmt = sqlConnection.prepareStatement(sql)) {
            stmt.setInt(1, playlist.getId());
            stmt.setInt(2, track.getId());
            stmt.executeUpdate();
            playlist.addTrack(track); /*add the track also inside the playlist model*/
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }



    /*Method for removing a track from a playlist*/
    public void removeTrack(Playlist playlist ,  Track track) {
        String sql = "DELETE FROM playlist_track WHERE playlist_id = ? AND track_id = ?";
        try(PreparedStatement stmt = sqlConnection.prepareStatement(sql)){
            stmt.setInt(1, playlist.getId());
            stmt.setInt(2, track.getId());
            stmt.executeUpdate();
            playlist.removeTrack(track); /*remove the track also from the playlist model*/
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }


    /*TODO: Implement method to load all tracks of a playlist[playlistDao]*/










}
