package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MyTableProvider implements TableProvider {
    private MultiDbState table;
    
    public MyTableProvider(String dir) throws IllegalArgumentException {
        table = new MultiDbState(dir);
    }
    
    public Table getTable(String name) {
        Command use = new MultiDbUse();
        use.execute(new String[] {name}, table);
        return table;
    }

    public Table createTable(String name) {
        Command create = new MultiDbCreate();
        create.execute(new String[] {name}, table);
        return table;
    }

    public void removeTable(String name) {
        Command remove = new DbRemove();
        remove.execute(new String[] {name}, table);
        return;
    }
}
