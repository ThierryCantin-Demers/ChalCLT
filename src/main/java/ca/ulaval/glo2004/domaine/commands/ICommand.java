package ca.ulaval.glo2004.domaine.commands;

/**
 * Interface that represents a command.
 */
public interface ICommand {
    void execute();
    void undo();
}
