package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MultiDbRemove implements Command {
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
        multiState.data[folder][file].remove(args);        
        return 0;
    }
    
    @Override
    public String getName() {
        return "remove";
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
