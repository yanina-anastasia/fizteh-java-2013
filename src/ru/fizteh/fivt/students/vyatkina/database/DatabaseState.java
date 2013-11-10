package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.vyatkina.FileManager;
import ru.fizteh.fivt.students.vyatkina.IOStreams;
import ru.fizteh.fivt.students.vyatkina.State;


public class DatabaseState extends State {

    protected Table table = null;
    protected TableProvider tableProvider = null;

    public DatabaseState (FileManager fileManager) {
        super (fileManager);
    }

    public void setTable (Table table) {
        this.table = table;
    }

    public void setTableProvider (TableProvider tableProvider) {
        this.tableProvider = tableProvider;
    }

    public Table getTable () {
        return table;
    }

    public TableProvider getTableProvider () {
        return tableProvider;
    }

}
