package utils;

import group6.java.group6.utils.Command;
import group6.java.group6.utils.CommandInvoker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandInvokerTest {

    private CommandInvoker invoker;

    @BeforeEach
    public void setUp() {
        invoker = new CommandInvoker();
    }

    @Test
    public void testExecuteCommand() {
        TestCommand cmd = new TestCommand();
        invoker.executeCommand(cmd);
        assertTrue(cmd.isExecuted());
    }

    @Test
    public void testUndoLastCommand() {
        TestCommand cmd = new TestCommand();
        invoker.executeCommand(cmd);
        
        boolean undoResult = invoker.undoLastCommand();
        
        assertTrue(undoResult);
        assertTrue(cmd.isUndone());
    }

    @Test
    public void testUndoEmptyHistory() {
        boolean undoResult = invoker.undoLastCommand();
        assertFalse(undoResult);
    }

    @Test
    public void testMultipleCommands() {
        TestCommand cmd1 = new TestCommand();
        TestCommand cmd2 = new TestCommand();
        
        invoker.executeCommand(cmd1);
        invoker.executeCommand(cmd2);
        
        assertTrue(cmd1.isExecuted());
        assertTrue(cmd2.isExecuted());
        
        invoker.undoLastCommand(); // Undo cmd2
        assertTrue(cmd2.isUndone());
        assertFalse(cmd1.isUndone());
        
        invoker.undoLastCommand(); // Undo cmd1
        assertTrue(cmd1.isUndone());
    }

    // Un finto comando per testare l'invoker isolato
    private static class TestCommand implements Command {
        private boolean executed = false;
        private boolean undone = false;

        @Override
        public void execute() {
            executed = true;
            undone = false;
        }

        @Override
        public void undo() {
            undone = true;
        }

        public boolean isExecuted() {
            return executed;
        }

        public boolean isUndone() {
            return undone;
        }
    }
}
