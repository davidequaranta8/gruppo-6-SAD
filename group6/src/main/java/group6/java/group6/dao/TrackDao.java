package group6.java.group6.dao;

import group6.java.group6.db.DatabaseResource;
import group6.java.group6.models.Track;

import java.sql.Connection;
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

    }

    @Override
    public void delete(Track track) {

    }

    @Override
    public void update(Track track) {

    }
}
