package group6.java.group6.dao;

import group6.java.group6.db.DatabaseResource;
import group6.java.group6.models.Track;

import java.sql.*;
import java.util.Optional;
import java.util.Set;

public class TrackDao implements Dao<Track , Integer>{
    private final Connection sqlConnection;

    public TrackDao() {
        sqlConnection = DatabaseResource.getInstance().getSqlConnection();
    }


    @Override
    public Optional<Track> get(Integer key) {
        return Optional.empty();
    }

    @Override
    public Set<Track> getAll() {
        return Set.of();
    }


    @Override
    public void save(Track track) {
        String sql = "INSERT INTO track (title, tag, author, genre, year_of_publication, length, count_played) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = sqlConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, track.getTitle());
            stmt.setString(2, track.getTag().name());
            stmt.setString(3, track.getAuthor());
            stmt.setString(4, track.getGenre().name());
            stmt.setInt(5, track.getYear());
            stmt.setDouble(6, track.getLength());
            stmt.setInt(7, track.getCountPlayed());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                track.setId(generatedKeys.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Track track) {

    }


    @Override
    public void update(Track track) {

    }


    public void deleteAll(){
        String sql = "DELETE FROM track";
        try {
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
