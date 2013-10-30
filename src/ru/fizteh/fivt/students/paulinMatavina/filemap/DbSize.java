package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbSize implements Command {
    @Override
    public int execute(String[] args, State state) {
        MultiDbState multiState = (MultiDbState) state;
        System.out.println(multiState.size());
        return 0;
    }
    
    @Override
    public String getName() {
        return "size";
    }
    
    @Override
    public int getArgNum() {
        return 0;
    }   
    
    @Override
    public boolean spaceAllowed() {
        return false;
    }
}
