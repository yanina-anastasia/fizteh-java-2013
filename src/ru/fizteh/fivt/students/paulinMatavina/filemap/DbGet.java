 package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbGet implements Command {
    @Override
    public int execute(String[] args, State state) {
        String key = args[0];
        if (((DbState) state).data.containsKey(key)) {
            System.out.println("found");
            System.out.println(((DbState) state).data.get(key));
        } else {
            System.out.println("not found");
        }
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
}
