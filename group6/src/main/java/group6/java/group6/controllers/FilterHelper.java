package group6.java.group6.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import group6.java.group6.dao.TrackDao;
import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Playlist;
import group6.java.group6.models.PlaylistManager;
import group6.java.group6.models.Track;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Gestisce la logica di ricerca, filtri per genere/anno e il loro reset.
 * Estrae i metodi handleFilter, handleResetFilter, handleSearch, initFilters dal MainController.
 */
public class FilterHelper {

    private final TrackDao trackDao;
    private final ComboBox<GenreEnum> genreFilter;
    private final ComboBox<String> yearFilter;
    private final TableView<Track> tracksTableView;
    private final TextField searchField;

    public FilterHelper(TrackDao trackDao, ComboBox<GenreEnum> genreFilter,
                        ComboBox<String> yearFilter, TableView<Track> tracksTableView,
                        TextField searchField) {
        this.trackDao = trackDao;
        this.genreFilter = genreFilter;
        this.yearFilter = yearFilter;
        this.tracksTableView = tracksTableView;
        this.searchField = searchField;
    }

    public void initFilters() {
        initGenreFilter();
        initYearFilter();

        // ButtonCell custom: mostra il promptText quando non c'è selezione
        genreFilter.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(GenreEnum item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? genreFilter.getPromptText() : item.toString());
            }
        });
        yearFilter.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? yearFilter.getPromptText() : item);
            }
        });
    }

    private void initYearFilter() {
        yearFilter.getItems().clear();
        Set<Integer> years = trackDao.getAllYears();
        List<String> yearStrings = years.stream()
                .map(String::valueOf)
                .sorted()
                .toList();
        yearFilter.getItems().addAll(yearStrings);
    }

    private void initGenreFilter() {
        genreFilter.getItems().clear();
        genreFilter.getItems().addAll(GenreEnum.values());
    }

    public void handleFilter() {
        GenreEnum selectedGenre = genreFilter.getValue();
        String selectedYear = yearFilter.getValue();

        List<Track> filtered = new ArrayList<>(ConcreteLibrary.getInstance().getTracks())
                .stream()
                .filter(t -> selectedGenre == null || t.getGenre() == selectedGenre)
                .filter(t -> selectedYear == null || String.valueOf(t.getYear()).equals(selectedYear))
                .collect(Collectors.toList());

        tracksTableView.getItems().setAll(filtered);
    }

    public void handleResetFilter() {
        genreFilter.getSelectionModel().clearSelection();
        yearFilter.getSelectionModel().clearSelection();
        tracksTableView.getItems().setAll(ConcreteLibrary.getInstance().getTracks());
    }

    public void handleSearch() {
        handleResetFilter();
        String query = searchField.getText();
        if (query == null) query = "";
        String q = query.trim().toLowerCase();

        Playlist selectedPlaylist = PlaylistManager.getInstance().getSelectedPlaylist();
        Set<Track> sourceTracks = selectedPlaylist != null
                ? selectedPlaylist.getTracks()
                : ConcreteLibrary.getInstance().getTracks();

        if (q.isEmpty()) {
            tracksTableView.getItems().setAll(sourceTracks);
            return;
        }

        List<Track> filteredTracks = new ArrayList<>();
        for (Track t : sourceTracks) {
            if (t.getTitle().toLowerCase().contains(q) || t.getAuthor().toLowerCase().contains(q)) {
                filteredTracks.add(t);
            }
        }

        tracksTableView.getItems().setAll(filteredTracks);
    }
}
