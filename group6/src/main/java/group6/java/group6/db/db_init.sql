/*SQL creation scheme script to run to test the application*/

-- Playlist table
CREATE TABLE Playlist (
    title VARCHAR(255) PRIMARY KEY,
    count_played INTEGER DEFAULT 0
);

-- Track table
CREATE TABLE Track (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    tag VARCHAR(100),
    author VARCHAR(255),
    genre VARCHAR(100),
    year_of_publication INTEGER,
    length INTEGER, -- in seconds
    count_played INTEGER DEFAULT 0
);

/*Association table playlist-track since we are dealing with N-N association*/
CREATE TABLE Playlist_Track (
    playlist_title VARCHAR(255),
    track_id INTEGER,
    PRIMARY KEY (playlist_title, track_id),
    FOREIGN KEY (playlist_title) REFERENCES Playlist(title) ON DELETE CASCADE,
    FOREIGN KEY (track_id) REFERENCES Track(id) ON DELETE CASCADE
);

