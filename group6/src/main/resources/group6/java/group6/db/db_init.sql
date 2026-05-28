/*SQL creation scheme script to run to test the application*/

-- Playlist table
CREATE TABLE IF NOT EXISTS Playlist (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    count_played INTEGER DEFAULT 0,
    --Constraint to prevent duplicate titles of playlists
    CONSTRAINT uq_playlist_name UNIQUE (title)
);

-- Track table
CREATE TABLE IF NOT EXISTS Track (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    tag VARCHAR(100),
    author VARCHAR(255),
    genre VARCHAR(100),
    year_of_publication INTEGER,
    length INTEGER, -- in seconds
    count_played INTEGER DEFAULT 0,
    --Constraint to avoid having in track table a duplicate record with same title and author , meaning we have the same
    --track added twice
    CONSTRAINT uq_track_title_artist UNIQUE (title, author)
);

/*Association table playlist-track since we are dealing with N-N association*/
CREATE TABLE IF NOT EXISTS Playlist_Track (
    playlist_id INTEGER,
    track_id INTEGER,
    PRIMARY KEY (playlist_id, track_id),
    FOREIGN KEY (playlist_id) REFERENCES Playlist(id) ON DELETE CASCADE,
    FOREIGN KEY (track_id) REFERENCES Track(id) ON DELETE CASCADE
);

