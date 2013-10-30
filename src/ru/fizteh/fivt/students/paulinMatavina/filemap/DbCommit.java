package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbCommit implements Command {
    @Override
    public int execute(String[] args, State state) {
        MultiDbState multiState = (MultiDbState) state;
        if (!multiState.isDbChosen() || multiState.isDropped) {
            System.out.println("no table");
            return 0;
        }
        
        int result = multiState.commit();
        System.out.println(result);
        return 0;
    }
    
    @Override
    public String getName() {
        return "commit";
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
