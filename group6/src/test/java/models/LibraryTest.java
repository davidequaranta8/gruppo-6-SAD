package models;

import group6.java.group6.enumerations.GenreEnum;
import group6.java.group6.enumerations.TagEnum;
import group6.java.group6.models.ConcreteLibrary;
import group6.java.group6.models.Track;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashSet;

public class LibraryTest {

    private ConcreteLibrary library;
    private Track t;

    // @BeforeEach fa sì che questo metodo venga eseguito PRIMA di ogni singolo @Test.
    // Garantisce che ogni test parta da una situazione "pulita".
    @BeforeEach
    public void setUp() {
        library = ConcreteLibrary.getInstance();

        // Svuotiamo forzatamente la libreria per resettare lo stato del Singleton!
        library.setTracks(new HashSet<>());

        // Inizializziamo la traccia di test
        t = new Track("titleTest", "authorTest", 3.1, GenreEnum.BLUES, 2000, TagEnum.Preferiti);
    }

    @Test
    public void testAddTrack() {
        // Metodo da testare
        library.addTrack(t);


        // Verifichiamo che ci sia la traccia inserita
        assertTrue(library.getTracks().contains(t), "La libreria dovrebbe contenere la traccia t");
        // Verifichiamo che ce ne sia esattamente 1
        assertEquals(1, library.getTracks().size(), "La dimensione della libreria dovrebbe essere 1");
    }

    @Test
    public void testRemoveTrack() {
        // inserisco una traccia per testare la rimozione
        library.addTrack(t);
        assertEquals(1, library.getTracks().size()); // Assicuriamoci che l'inserimento sia andato a buon fine

        // Metodo da testare
        library.removeTrack(t);

        // verifiche
        assertFalse(library.getTracks().contains(t), "La traccia t dovrebbe essere stata rimossa");
        assertEquals(0, library.getTracks().size(), "La libreria dovrebbe essere vuota");
    }

}
