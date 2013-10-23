package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MultiDbGet implements Command {
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
        multiState.data[folder][file].get(args);        
        return 0;
    }
    
    @Override
    public String getName() {
        return "get";
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
