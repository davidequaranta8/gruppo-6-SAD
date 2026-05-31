package group6.java.group6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

import group6.java.group6.db.DatabaseResource;
import group6.java.group6.models.Track;

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
    String sql = "INSERT INTO track (title, tag, author, genre, year_of_publication, length) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";
        try{
        PreparedStatement stmt = sqlConnection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setString(1, track.getTitle());
        stmt.setString(2, track.getTag().name());
        stmt.setString(3 , track.getAuthor());
        stmt.setString(4 , track.getGenre().name());
        stmt.setInt(5 , track.getYear());
        //TODO: convertire il tipo integer di length in db in double
        //stmt.setInt(6 , track.getLength());
        //PROVA: per ora setto length a 30 in attesa di capire come convertire double in int
        stmt.setInt(6 , 30);
        stmt.executeUpdate();

        // Legge l'id generato dal DB e lo assegna alla traccia in memoria
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            track.setId(rs.getInt(1));
        }

        }catch(SQLException e){
        e.printStackTrace();
    }
    }

    @Override
    public void delete(Track track) {
        String sql = "DELETE FROM track WHERE id = ?";
        try {
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            stmt.setInt(1, track.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
    }
    }

    @Override
    public void update(Track track) {
        String sql = "UPDATE track SET title = ?, tag = ?, author = ?, genre = ?, year_of_publication = ?, length = ? WHERE id = ?";
        try {
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            stmt.setString(1, track.getTitle());
            stmt.setString(2, track.getTag().name());
            stmt.setString(3 , track.getAuthor());
            stmt.setString(4 , track.getGenre().name());
            stmt.setInt(5 , track.getYear());
            // TODO: convertire il tipo integer di length in db in double
            // stmt.setInt(6 , track.getLength());
            // PROVA: per ora setto length a 30 in attesa di capire come convertire double in int
            stmt.setInt(6 , 30);
            stmt.setInt(7 , track.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
