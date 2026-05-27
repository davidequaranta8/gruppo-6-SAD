package group6.java.group6.models;

public interface Library { // interfaccia Subject
    void addObserver(LibraryObserver observer); // metodo attach del pattern Observer
    void removeObserver(LibraryObserver observer); // metodo detach del pattern Observer
    void notifyObservers(); // metodo notify del pattern Observer

    // vedere se introdurre anche i metodi interni alla classe concreteLibrary quindi addTrack,removeTrack, ecc..
    // perchè farlo ci permette di rispettare il principio di Inversione delle Dipendenze perchè lavoriamo con l'interfaccia e non con la classe LibraryConcrete
    // inoltre vedere se usare le interfacce native Observable o meno (l'usare comporta il non usare nomi di metodi contestualizzati)

}
