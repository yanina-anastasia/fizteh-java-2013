package ru.fizteh.fivt.students.eltyshev.storable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableShellState;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableUtils;
import ru.fizteh.fivt.students.eltyshev.storable.database.TableInfo;

import java.io.IOException;

public class CreateCommand extends AbstractCommand<StoreableShellState> {
    public CreateCommand() {
        super("create", "create <table name> (type1 type2 ... typeN)");
    }

    @Override
    public void executeCommand(String params, StoreableShellState shellState) throws IOException {

        TableInfo info = null;
        try {
            info = StoreableUtils.parseCreateCommand(params);
        } catch (IllegalArgumentException e) {
            //System.err.println(e.getMessage());
            System.out.println("$ ");
            return;
        }
        Table newTable = shellState.provider.createTable(info.getTableName(), info.getColumnTypes());

        if (newTable == null) {
            System.out.println(String.format("%s exists", info.getTableName()));
        } else {
            System.out.println("created");
        }
    }
}
