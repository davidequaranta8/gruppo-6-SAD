package group6.java.group6.utils;

import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.Track;

import java.util.ArrayList;
import java.util.List;

public class RemoveTrackCommand implements Command{
    private final Track track;
    private final Playlist playlist;
    private int originalPosition; // salva la posizione della track all'interno della playlist prima di eliminare

    public RemoveTrackCommand(Track track, Playlist playlist) {
        this.track = track;
        this.playlist = playlist;

    }

    @Override
    public void execute() {
        // 1. Memorizziamo l'indice ESATTO prima di cancellare usando una lista di appoggio
        List<Track> tempForIndex = new ArrayList<>(playlist.getTracks());
        this.originalPosition = tempForIndex.indexOf(track);

        // 2. Usiamo il PlaylistManager per eliminare la traccia dal DB e dal Modello
        PlaylistManager.getInstance().removeTrackFromPlaylist(playlist, track);
    }

    @Override
    public void undo() {
        if (originalPosition == -1) return;

        // 1. Usiamo il PlaylistManager per rimettere la traccia nel DB e nel modello.
        PlaylistManager.getInstance().addTrackToPlaylist(playlist, track);

        // 2. Ripristiniamo l'ordine corretto per la sessione corrente
        List<Track> tempList = new ArrayList<>(playlist.getTracks());

        // La rimuoviamo dal fondo (dove l'ha appena messa il manager)
        tempList.remove(track);

        // La inseriamo nella posizione originale salvata all'inizio
        if (originalPosition >= 0 && originalPosition <= tempList.size()) {
            tempList.add(originalPosition, track);
        } else {
            tempList.add(track);
        }

        // 3. Svuotiamo il set e riversiamo i dati ordinati
        playlist.getTracks().clear();
        playlist.getTracks().addAll(tempList);

        // 4. Forziamo il tuo manager ad avvisare l'interfaccia grafica
        // che l'ordine delle tracce è cambiato!
        PlaylistManager.getInstance().notifyObservers();
    }

}
