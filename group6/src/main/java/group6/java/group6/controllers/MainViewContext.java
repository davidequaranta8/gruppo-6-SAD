package group6.java.group6.controllers;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class MainViewContext {
    
    // Singleton degli stati
    public static final LibraryState  LIBRARY_STATE  = new LibraryState();
    public static final PlaylistState PLAYLIST_STATE = new PlaylistState();

    
    public final HBox filterBar;
    public final Button addTrackBtn;
    public final Button addToPlaylistBtn;
    public final Button deleteTrackBtn;
    public final Button removeFromPlaylistBtn;
    public final Label playlistTitleLabel;
    public final Button renamePlaylistBtn;
 
    private ViewState currentState;
 
    public MainViewContext(HBox filterBar, Button addTrackBtn, Button addToPlaylistBtn, Button deleteTrackBtn,Button removeFromPlaylistBtn, Button renamePlaylistBtn, Label playlistTitleLabel) {
        this.filterBar             = filterBar;
        this.addTrackBtn           = addTrackBtn;
        this.addToPlaylistBtn      = addToPlaylistBtn;
        this.deleteTrackBtn        = deleteTrackBtn;
        this.removeFromPlaylistBtn = removeFromPlaylistBtn;
        this.renamePlaylistBtn     = renamePlaylistBtn;
        this.playlistTitleLabel    = playlistTitleLabel;
    }
 
    
    public void setState(ViewState newState) {
        if (currentState != null) currentState.exit(this);
        currentState = newState;
        currentState.enter(this);
    }
 
    public ViewState getCurrentState() {
        return currentState;
    }
 
    public void setVisible(Node node, boolean visible) {
        if (node == null) return;
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
