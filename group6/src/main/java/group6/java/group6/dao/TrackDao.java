package group6.java.group6.dao;

import group6.java.group6.db.DatabaseResource;
import group6.java.group6.enumerations.Genre;
import group6.java.group6.enumerations.TagEnum;
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
        String sql = "SELECT * FROM track where id = ?";
        try{
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            stmt.setInt(1 , key);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                Track track  = new Track(rs.getString("title") , rs.getString("author") , rs.getDouble("length"), Genre.valueOf(rs.getString("genre")) , rs.getInt("year_of_publication") , TagEnum.valueOf(rs.getString("tag")));
                track.setCountPlayed(rs.getInt("count_played"));
                track.setId(rs.getInt("id"));
                return Optional.of(track);
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
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
        String sql = "DELETE FROM track where id = ?";
        try{
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            stmt.setInt(1, track.getId());
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }



    @Override
    public void update(Track track) {
        String sql = "UPDATE track  "
                + "SET title = ?, "
                + "    tag = ?, "
                + "    author = ?, "
                + "    genre = ?, "
                + "    year_of_publication = ?, "
                + "    length = ?, "
                + "    count_played = ? "
                + "WHERE id = ?";

        try (PreparedStatement ps = sqlConnection.prepareStatement(sql)) {
            ps.setString(1, track.getTitle());
            ps.setString(2, track.getTag().name());
            ps.setString(3, track.getAuthor());
            ps.setString(4, track.getGenre().name());
            ps.setInt(5, track.getYear());
            ps.setDouble(6, track.getLength());
            ps.setInt(7, track.getCountPlayed());
            ps.setInt(8, track.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating track with id: " + track.getId(), e);
        }
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
