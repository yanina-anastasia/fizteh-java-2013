package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbPut implements Command {
    @Override
    public int execute(String[] args, State state) {
        String key = args[0];
        String value = args[1];
        String result = ((DbState) state).data.put(key, value);
        if (result != null) {
                System.out.println("overwrite");
                System.out.println(result);
        } else {
                System.out.println("new");
        }
        return 0;
    }
    
    @Override
    public String getName() {
        return "put";
    }
    
    @Override
    public int getArgNum() {
        return 2;
    }
}
