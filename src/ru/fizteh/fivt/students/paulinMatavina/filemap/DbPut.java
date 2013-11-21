package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbPut implements Command {
    @Override
    public int execute(String[] args, State state) {
        String key = args[0];
        MyTableProvider multiState = (MyTableProvider) state;
        if (multiState.getCurrTable() == null) {
            System.out.println("no table");
            return 0;
        }
        
        String result = multiState.getCurrTable().put(key, args[1]);  
        if (result == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(result);
        }
        return 0;
    }
    
    @Override
    public boolean spaceAllowed() {
        return true;
    }
    @Override
    public String getName() {
        return "put";
    }
    
    @Override
    public int getArgNum() {
        return 2;
    }
}
