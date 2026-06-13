package group6.java.group6.utils;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.models.Track;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TrackMapper {

    public static Track extractTrackFromResultSet(ResultSet rs) throws SQLException {
        Track track;

        // Estraiamo in sicurezza il tag gestendo sia il caso null che stringa vuota
        String tagStr = rs.getString("tag");

        if (tagStr == null || tagStr.trim().isEmpty()) {
            track = new Track(
                    rs.getString("title"),
                    rs.getString("author"),
                    parseGenre(rs.getString("genre")),
                    rs.getInt("year_of_publication")
            );
        } else {
            track = new Track(
                    rs.getString("title"),
                    rs.getString("author"),
                    parseGenre(rs.getString("genre")),
                    rs.getInt("year_of_publication"),
                    TagEnum.valueOf(tagStr)
            );
        }

        track.setCountPlayed(rs.getInt("count_played"));
        track.setId(rs.getInt("id"));
        track.setLength(rs.getDouble("length"));
        track.setFilePath(rs.getString("file_path"));

        return track;
    }

    // Metodo di utilità per gestire stringhe di genere non valide nel database
    private static GenreEnum parseGenre(String value) {
        if (value == null || value.trim().isEmpty()) return GenreEnum.OTHER;
        try {
            return GenreEnum.valueOf(value);
        } catch (IllegalArgumentException e) {
            System.err.println("GenreEnum sconosciuto: " + value);
            return GenreEnum.OTHER;
        }
    }
}