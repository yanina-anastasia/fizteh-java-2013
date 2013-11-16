package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.FileMapShellState;
import ru.fizteh.fivt.students.inaumov.shell.exceptions.UserInterruptionException;

public class ExitCommand<State extends FileMapShellState> extends AbstractCommand<State> {
    public ExitCommand() {
        super("exit", 0);
    }

    public void execute(String[] args, State state) throws UserInterruptionException {
        if (state.getTable() != null) {
            state.rollback();
        }

        throw new UserInterruptionException();
    }
}
