package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.IOException;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MyTableProvider implements TableProvider {
    private MultiDbState table;
    
    public MyTableProvider(String dir) {
        table = new MultiDbState(dir);
    }
    
    public Table getTable(String name) {
        try {
            table.use(name);
        } catch (DbReturnStatus e) {
            if (Integer.parseInt(e.getMessage()) == 2) {
                return null;
            }
        }
        return table;
    }

    public Table createTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (!MultiDbState.checkNameValidity(name)) {
            throw new RuntimeException();
        }
        
        try {
            table.create(name);
        } catch (DbReturnStatus e) {
            if (Integer.parseInt(e.getMessage()) == 2) {
                return null;
            }
        }
        
        if (table.isDbChosen()) {
            try {
                table.closeAll();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        table.tableName = null;
        try {
            table.use(name);
        } catch (DbReturnStatus e) {
            if (Integer.parseInt(e.getMessage()) != 0) {
                throw new RuntimeException();
            }
        }
        return table;
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
