package group6.java.group6.utils;

import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.Track;

public class AddTrackCommand implements Command{
    private final Track track;
    private final Playlist playlist;

    public AddTrackCommand(Track track, Playlist playlist) {
        this.track = track;
        this.playlist = playlist;
    }

    @Override
    public void execute() {
        PlaylistManager.getInstance().addTrackToPlaylist(playlist, track);
    }

    @Override
    public void undo() {
        PlaylistManager.getInstance().removeTrackFromPlaylist(playlist, track);
    }
}
