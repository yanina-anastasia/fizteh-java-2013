package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MultiDbUse implements Command {
    @Override
    public int execute(String[] args, State state) {
        String dbName = args[0];
        if (dbName == null) {
            throw new IllegalArgumentException();
        }
        MultiDbState multiState = (MultiDbState) state;

        int result = multiState.changeBase(dbName);
        if (result == 2) {
            System.out.println(dbName + " not exists");
            return 0;
        } else if (result == 0) {
            System.out.println("using " + dbName);
            return 0;
        } else {
            throw new IllegalArgumentException();
        }
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
