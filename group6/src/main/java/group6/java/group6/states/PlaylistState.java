package group6.java.group6.states;


public class PlaylistState implements ViewState {
 
    @Override
    public void enter(MainViewContext ctx) {
        ctx.setVisible(ctx.filterBar,             false);
        ctx.setVisible(ctx.addTrackBtn,           false);
        ctx.setVisible(ctx.deleteTrackBtn,        false);
        ctx.setVisible(ctx.addToPlaylistBtn,      true);
        ctx.setVisible(ctx.removeFromPlaylistBtn, true);
        ctx.setVisible(ctx.playlistTitleLabel,    true);
        ctx.setVisible(ctx.renamePlaylistBtn, true);
        ctx.setVisible(ctx.undoBtn, true);
    }
 
    @Override
    public void exit(MainViewContext ctx) {}
}
 