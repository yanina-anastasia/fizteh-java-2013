package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MyTableProvider implements TableProvider {
    private MultiDbState table;
    private String rootDir;
    
    public MyTableProvider(String dir) {
        table = new MultiDbState(dir);
        rootDir = dir;
    }
    
    public Table getTable(String name) {
        /*try {
            table.use(name);
        } catch (DbReturnStatus e) {
            if (Integer.parseInt(e.getMessage()) == 2) {
                return null;
            }
        }
        return table;*/
        MultiDbState newTable = new MultiDbState(rootDir);
        try {
            newTable.use(name);
        } catch (DbReturnStatus e) {
            if (Integer.parseInt(e.getMessage()) == 2) {
                return null;
            }
        }
        return newTable;
    }

    public Table createTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (!MultiDbState.checkNameValidity(name)) {
            throw new RuntimeException();
        }
        
        MultiDbState newTable = new MultiDbState(rootDir);
        try {
            newTable.create(name);
        } catch (DbReturnStatus e) {
            if (Integer.parseInt(e.getMessage()) == 2) {
                return null;
            }
        }
        newTable.use(name);
        return newTable;
    }

    public void removeTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (!table.fileExist(name)) {
            throw new IllegalStateException();
        }
        Command remove = new DbRemove();
        remove.execute(new String[] {name}, table);
        return;
    }
}
