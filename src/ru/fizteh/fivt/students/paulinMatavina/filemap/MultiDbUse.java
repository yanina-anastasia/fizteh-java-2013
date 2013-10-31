package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MultiDbUse implements Command {
    @Override
    public int execute(String[] args, State state) {
        String dbName = args[0];
        MultiDbState multiState = (MultiDbState) state;
        try {
            multiState.use(dbName);
        } catch (DbReturnStatus e) {
            if (Integer.parseInt(e.getMessage()) == 2) {
                System.out.println(dbName + " not exists");
            } else {
                System.out.println("using " + dbName);
            }
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
