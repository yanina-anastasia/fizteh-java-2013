package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MultiDbCreate implements Command {
    @Override
    public int execute(String[] args, State state) {
        String name = args[0];
        if (name == null) {
            throw new IllegalArgumentException();
        }
        MultiDbState multiState = (MultiDbState) state;
        
        try {
            multiState.create(name);
        } catch (DbReturnStatus e) {
            if (Integer.parseInt(e.getMessage()) == 2) {
                System.out.println(name + " exists");
            } else {
                System.out.println("created");
            }
        }
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
