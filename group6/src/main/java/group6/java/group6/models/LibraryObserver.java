package group6.java.group6.models;
/*
    utilizziamo questa interfaccia per applicare il pattern Observer,
    questo pattern permette di separare completamente la logica di aggiornamento della Library
    dall'interfaccia grafica gestita da MainController.

 */
public interface LibraryObserver { // interfaccia Observer

    /*update of concrete observer in observer pattern*/

    void onLibraryChanged(); // metodo update del pattern Observer
    // Questo metodo verrà chiamato ogni volta che la libreria subisce una modifica
}