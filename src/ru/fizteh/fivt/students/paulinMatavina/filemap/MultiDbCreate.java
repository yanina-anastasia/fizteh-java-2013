package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MultiDbCreate implements Command {
    @Override
    public int execute(String[] args, State state) {
        String name = args[0];
        args[0] = ((MultiDbState) state).makeNewSource(args[0]);
        int result = ((MultiDbState) state).shell.mkdir(args);
        if (result == 2) {
            System.out.println(name + " exists");
            return 0;
        }
        if (result == 0) {
            System.out.println("created");
        }
        return result;
    }
    
    @Override
    public String getName() {
        return "create";
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
