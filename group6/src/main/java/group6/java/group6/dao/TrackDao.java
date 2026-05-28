package group6.java.group6.dao;

import group6.java.group6.db.DatabaseResource;
import group6.java.group6.models.Track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    String sql  = "INSERT INTO track VALUES (? , ? , ? , ? , ? , ?)";
    try{
        PreparedStatement stmt = sqlConnection.prepareStatement(sql);
        stmt.setString(1, track.getTitle());
        stmt.setString(2, track.getTag().name());
        stmt.setString(3 , track.getAuthor());
        stmt.setString(4 , track.getGenre().name());
        stmt.setInt(5 , track.getYear());
        //TODO: convertire il tipo integer di length in db in double
        //stmt.setInt(6 , track.getLength());

    }catch(SQLException e){
        e.printStackTrace();
    }
    }

    @Override
    public void delete(Track track) {

    }

    @Override
    public void update(Track track) {

    }
}
