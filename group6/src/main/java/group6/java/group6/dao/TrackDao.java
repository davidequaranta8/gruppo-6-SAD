package group6.java.group6.dao;

import group6.java.group6.db.DatabaseResource;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.exceptions.DuplicateTitleTrackException;
import group6.java.group6.models.Track;

import java.io.File;
import java.sql.*;
import java.util.HashSet;
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
                Track track;
                //it there is the tag then use constructor with the tag
                if (!rs.getString("tag").isEmpty()) {
                    track  = new Track(rs.getString("title"),rs.getString("author") , GenreEnum.valueOf(rs.getString("genre")),rs.getInt("year_of_publication") , TagEnum.valueOf(rs.getString("tag")));

                }
                //there is no tag for that track hence use the other constructor, the one without the tag
                else {
                    track = new Track(rs.getString("title"), rs.getString("author"), GenreEnum.valueOf(rs.getString("genre")), rs.getInt("year_of_publication"));
                }
                track.setCountPlayed(rs.getInt("count_played"));
                track.setId(rs.getInt("id"));
                track.setLength(rs.getInt("length"));
                track.setFilePath(rs.getString("file_path"));
                return Optional.of(track);
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Set<Track> getAll() {
        String sql = "SELECT * FROM track";
        try{
            PreparedStatement stmt = sqlConnection.prepareStatement(sql);
            Set<Track> tracks = new HashSet<>();
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Track track;
                //it there is the tag then use constructor with the tag
                if (!rs.getString("tag").isEmpty()) {
                    track  = new Track(rs.getString("title"),rs.getString("author") , GenreEnum.valueOf(rs.getString("genre")),rs.getInt("year_of_publication") , TagEnum.valueOf(rs.getString("tag")));

                }
                //there is no tag for that track hence use the other constructor, the one without the tag
                else {
                     track = new Track(rs.getString("title"), rs.getString("author"), GenreEnum.valueOf(rs.getString("genre")), rs.getInt("year_of_publication"));
                }
                track.setCountPlayed(rs.getInt("count_played"));
                track.setId(rs.getInt("id"));
                track.setFilePath(rs.getString("file_path"));
                track.setLength(rs.getDouble("length"));
                tracks.add(track);
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

        //if we got till here it means we can proceed with insertion hence has not been found any duplicate record
        try{
            String sql = "INSERT INTO track (title, tag, author, genre, year_of_publication, length, count_played, file_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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

            // delete also the copy of the file from the disk
            File file = new File(track.getFilePath());
            if (file.exists()) {
                file.delete();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void update(Track track) {
        //TODO: creare un metodo isPresentWithTitleAndAuthor a parte per la verifica dei duplicati nel DB

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
}
