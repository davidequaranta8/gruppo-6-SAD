package group6.java.group6.controllers;

public class LibraryState implements ViewState {

    @Override
    public void enter(MainViewContext ctx) {
        ctx.setVisible(ctx.filterBar,             true);
        ctx.setVisible(ctx.addTrackBtn,           true);
        ctx.setVisible(ctx.deleteTrackBtn,        true);
        ctx.setVisible(ctx.addToPlaylistBtn,      false);
        ctx.setVisible(ctx.removeFromPlaylistBtn, false);
        ctx.setVisible(ctx.playlistTitleLabel,    false);
        ctx.setVisible(ctx.renamePlaylistBtn, false);
        ctx.setVisible(ctx.undoBtn,false);
    }
 
    @Override
    public void exit(MainViewContext ctx) {}
}