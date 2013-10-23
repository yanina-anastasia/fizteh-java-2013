package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MultiDbPut implements Command {
    @Override
    public int execute(String[] args, State state) {
        String key = args[0];
        MultiDbState multiState = (MultiDbState) state;
        if (!multiState.isDbChosen() || multiState.isDropped) {
            System.out.println("no table");
            return 0;
        }
        
        int folder = multiState.getFolderNum(key);
        int file = multiState.getFileNum(key);
        multiState.data[folder][file].put(args);        
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
