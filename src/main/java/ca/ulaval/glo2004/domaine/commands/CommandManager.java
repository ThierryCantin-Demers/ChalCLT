package ca.ulaval.glo2004.domaine.commands;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Class that manages the commands to undo and redo user actions
 */
public class CommandManager {
    /**
     * Stack of commands that have been executed
     */
    private final Deque<ICommand> commandHistory;

    /**
     * Stack of commands that have been undone
     */
    private final Deque<ICommand> commandUndoHistory;

    /**
     * Default constructor
     */
    public CommandManager()
    {
        this.commandHistory = new ArrayDeque<>();
        this.commandUndoHistory = new ArrayDeque<>();
    }

    /**
     * Executes a command and adds it to the command history
     * @param command_ : Command to execute
     */
    public void executeCommand(ICommand command_)
    {
        command_.execute();
        this.commandHistory.push(command_);
    }

    /**
     * Undo the last command executed (the command is on the top of the command history stack).
     * If there is no command to undo, do nothing.
     */
    public void undoCommand()
    {
        if(this.commandHistory.isEmpty())
        {
            return;
        }

        ICommand command = this.commandHistory.pop();
        command.undo();
        commandUndoHistory.push(command);
    }

    /**
     * Redo the last command undone (the command is on the top of the command undo history stack).
     * If there is no command to redo, do nothing.
     */
    public void redoCommand()
    {
        if(this.commandUndoHistory.isEmpty())
        {
            return;
        }

        ICommand command = this.commandUndoHistory.pop();
        command.execute();
        commandHistory.push(command);
    }

    public void clearHistories()
    {
        this.commandHistory.clear();
        this.commandUndoHistory.clear();
    }
}
