package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MultiDbUse implements Command {
    @Override
    public int execute(String[] args, State state) {
        String dbName = args[0];
        MyTableProvider multiState = (MyTableProvider) state;    
        Table table = multiState.getTable(dbName);
        
        if (table == null) {
            System.out.println(dbName + " not exists");
        } else {
            if (multiState.getCurrTable() != null) {
                int chNum = ((MultiDbState) multiState.getCurrTable()).changesNum();
                if (chNum > 0) {
                    System.out.println(chNum + " uncommited changes");
                    return 0;
                }
            } 
            multiState.currTableName = dbName;
            System.out.println("using " + dbName);
        }
        return 0;
    }
    
    @Override
    public String getName() {
        return "use";
    }
    
    @Override
    public int getArgNum() {
        return 1;
    }   
    
    @Override
    public boolean spaceAllowed() {
        return false;
    }
}
