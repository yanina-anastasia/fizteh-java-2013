package ru.fizteh.fivt.students.eltyshev.storable.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableShellState;

import java.io.IOException;

public class RollbackCommand extends AbstractCommand<StoreableShellState> {
    public RollbackCommand() {
        super("rollback", "rollback");
    }

    @Override
    public void executeCommand(String params, StoreableShellState shellState) throws IOException {
        if (CommandParser.getParametersCount(params) > 0) {
            throw new IllegalArgumentException("too many parameters");
        }
        if (shellState.table == null) {
            System.err.println("no table");
            return;
        }
        int recordsDeleted = shellState.table.rollback();
        System.out.println(recordsDeleted);
    }
}
