package group6.java.group6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import group6.java.group6.db.DatabaseResource;
import group6.java.group6.exceptions.DuplicateTitleTrackException;
import group6.java.group6.models.Track;
import group6.java.group6.utils.TrackMapper;

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
                return Optional.of(TrackMapper.extractTrackFromResultSet(rs));
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Set<Track> getAll() {
        String sql = "SELECT * FROM track ORDER BY position ASC";
        try{
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            Set<Track> tracks = new HashSet<>();
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                tracks.add(TrackMapper.extractTrackFromResultSet(rs));
            }
            return tracks;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        //if nothing was found
        return null;
    }


    @Override
    public void save(Track track) throws DuplicateTitleTrackException {
        //check first whether exists a track with same author and title or not
        if(existsByAuthorAndTitle(track.getAuthor(), track.getTitle())) throw new DuplicateTitleTrackException("Esiste già una traccia con titolo "+track.getTitle()+ " e autore "+ track.getAuthor());

        int nextPosition = getMaxPosition() + 1;
        //if we got till here it means we can proceed with insertion hence has not been found any duplicate record
        try{
            String sql = "INSERT INTO track (title, tag, author, genre, year_of_publication, length, count_played, file_path, position) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = sqlConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, track.getTitle());
            if(track.getTag() !=  null) {
                stmt.setString(2, track.getTag().name());
            }
            else {
                stmt.setString(2, "");
            }
            stmt.setString(3, track.getAuthor());
            stmt.setString(4, track.getGenre().name());
            stmt.setInt(5, track.getYear());
            stmt.setDouble(6, track.getLength());
            stmt.setInt(7, track.getCountPlayed());
            stmt.setString(8, "placeholder");
            stmt.setInt(9, nextPosition);
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                track.setId(generatedId);

                String filePath = "music/" + generatedId + ".mp3";
                track.setFilePath(filePath);

                String updateSql = "UPDATE track SET file_path = ? WHERE id = ?";
                PreparedStatement updateStmt = sqlConnection.prepareStatement(updateSql);
                updateStmt.setString(1, filePath);
                updateStmt.setInt(2, generatedId);
                updateStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void delete(Track track) {
        String sql = "DELETE FROM track where id = ?";
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
            if(track.getTag() !=  null) {
                ps.setString(2, track.getTag().name());
                ps.addBatch();
            }
            else {
                ps.setString(2, "");
            }
            ps.setString(3, track.getAuthor());
            ps.setString(4, track.getGenre().name());
            ps.setInt(5, track.getYear());
            ps.setDouble(6, track.getLength());
            ps.setInt(7, track.getCountPlayed());
            ps.setInt(8, track.getId());

            ps.executeUpdate();

        } catch (SQLException e) {

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


    //Method to check if exists already a record with that author and that title. Check is consistent to upper and lower case words
    public boolean existsByAuthorAndTitle(String author, String title){
        String sql = "SELECT COUNT(*) FROM track WHERE LOWER(author) = ? AND LOWER(title) = ?";
        try{
            PreparedStatement checkStmt = sqlConnection.prepareStatement(sql);
            checkStmt.setString(1, author.toLowerCase());
            checkStmt.setString(2, title.toLowerCase());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean existsByAuthorAndTitleAndId(String author, String title,int id){
        String sql = "SELECT COUNT(*) FROM track WHERE LOWER(author) = ? AND LOWER(title) = ? AND id <> ?";
        try{
            PreparedStatement checkStmt = sqlConnection.prepareStatement(sql);
            checkStmt.setString(1, author.toLowerCase());
            checkStmt.setString(2, title.toLowerCase());
            checkStmt.setInt(3,id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }



    /*This method provides the top k songs played*/
    public List<Track> getTopPlayedTracks(int k) {
        List<Track> topTracks = new ArrayList<>();
        String sql = "SELECT * FROM track  WHERE count_played <> 0 ORDER BY count_played DESC LIMIT ?";

        try{
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            stmt.setInt(1, k);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                topTracks.add(TrackMapper.extractTrackFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topTracks;
    }



    public Set<Integer> getAllYears(){
        Set<Integer> years = new HashSet<>();
        String sql = "SELECT DISTINCT year_of_publication FROM track";
        try{
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                years.add(rs.getInt("year_of_publication"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return years;
    }

    public void updateTrackPositionsInLibrary(List<Integer> orderedTrackIds) {
        String sql = "UPDATE track SET position = ? WHERE id = ?";
        try {
            sqlConnection.setAutoCommit(false);
            try (PreparedStatement ps = sqlConnection.prepareStatement(sql)) {
                for (int i = 0; i < orderedTrackIds.size(); i++) {
                    ps.setInt(1, i + 1);
                    ps.setInt(2, orderedTrackIds.get(i));
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            sqlConnection.commit();
            sqlConnection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            try { sqlConnection.rollback(); sqlConnection.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

     private void compactLibraryPositions() {
        String select = "SELECT id FROM track ORDER BY position ASC";
        try (PreparedStatement sel = sqlConnection.prepareStatement(select)) {
            ResultSet rs = sel.executeQuery();
            List<Integer> ids = new ArrayList<>();
            while (rs.next()) ids.add(rs.getInt("id"));
            updateTrackPositionsInLibrary(ids);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getMaxPosition() {
        String sql = "SELECT COALESCE(MAX(position), 0) FROM track";
        try (PreparedStatement ps = sqlConnection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
