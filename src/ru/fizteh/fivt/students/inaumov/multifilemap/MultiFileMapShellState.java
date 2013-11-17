package ru.fizteh.fivt.students.inaumov.multifilemap;

import ru.fizteh.fivt.students.inaumov.filemap.FileMapShellState;

import java.io.IOException;

public interface MultiFileMapShellState<Table, Key, Value> extends FileMapShellState<Table, Key, Value> {
    public Table createTable(String tableName);

    public void dropTable(String tableName) throws IOException;

    public Table useTable(String tableName);

    public String getCurrentTableName();

    public String[] parseCreateCommand(String argumentsLine);
}
