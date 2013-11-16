package ru.fizteh.fivt.students.inaumov.multifilemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;

import java.io.IOException;

public class DropCommand<State extends MultiFileMapShellState> extends AbstractCommand<State> {
    public DropCommand() {
        super("drop", 1);
    }

    public void execute(String[] args, State shell) {
        try {
            shell.dropTable(args[1]);
            System.out.println("dropped");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
}
