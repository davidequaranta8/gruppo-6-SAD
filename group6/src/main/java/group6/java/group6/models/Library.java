package group6.java.group6.models;

public interface Library { // interfaccia Subject
    void addObserver(LibraryObserver observer); // metodo attach del pattern Observer
    void removeObserver(LibraryObserver observer); // metodo detach del pattern Observer
    void notifyObservers(); // metodo notify del pattern Observer

}
