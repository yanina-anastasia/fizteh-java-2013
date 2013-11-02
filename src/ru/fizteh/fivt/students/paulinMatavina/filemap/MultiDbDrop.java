package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.File;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MultiDbDrop implements Command {
    @Override
    public int execute(String[] args, State state) {
        args[0] = state.makeNewSource(args[0]);
        MultiDbState multiState = (MultiDbState) state;
        int result = multiState.shell.rm(args);
        if (result == 2) {
            System.out.println(args[0] + " not exists");
            return 0;
        }
        if (result == 0) {
            System.out.println("dropped");
            File file = new File(args[0]);
            if (multiState.tableName.equals(file.getName())) {
                multiState.isDropped = true;
                multiState.tableName = null;
                
            }
        }
        return result;
    }
    
    @Override
    public String getName() {
        return "drop";
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
