 package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbRemove implements Command {
    @Override
    public int execute(String[] args, State state) {
        String key = args[0];
        if (((DbState) state).data.containsKey(key)) {
            ((DbState) state).data.remove(args[1]);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
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
}
