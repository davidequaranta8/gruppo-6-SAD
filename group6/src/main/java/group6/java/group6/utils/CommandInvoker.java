package group6.java.group6.utils;

import java.util.Stack;

// Il suo unico scopo è eseguire i comandi che le vengono passati dal Client e memorizzarli nello stack.
// In modo tale da poterli eventualmente annullare
public class CommandInvoker {
    private final Stack<Command> history = new Stack<>();

    // Esegue il comando e lo aggiunge alla pila
    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
    }

    // Tenta di annullare l'ultimo comando. Restituisce false se la pila è vuota.
    public boolean undoLastCommand() {
        if (history.isEmpty()) {
            return false;
        }
        Command lastCommand = history.pop();
        lastCommand.undo();
        return true;
    }
}
