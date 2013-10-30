package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.File;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MultiDbCreate implements Command {
    @Override
    public int execute(String[] args, State state) {
        String name = args[0];
        if (name == null) {
            throw new IllegalArgumentException();
        }
        MultiDbState multiState = (MultiDbState) state;
        if (new File(multiState.makeNewSource(name)).exists()) {
            System.out.println(name + " exists");
            return 0;
        }
        
        args[0] = multiState.makeNewSource(args[0]);
        multiState.shell.mkdir(args);
        System.out.println("created");
        return 0;
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
