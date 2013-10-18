package ru.fizteh.fivt.students.anastasyev.filemap;

import java.io.IOException;
import ru.fizteh.fivt.students.anastasyev.shell.Command;
import ru.fizteh.fivt.students.anastasyev.shell.State;

public class PutCommand implements Command {
    @Override
    public boolean exec(State fileMap, String[] command) {
        if (command.length < 3) {
            System.err.println("put: Usage - put key value");
            return false;
        }
        try {
            String arg1 = command[1];
            StringBuilder builderArg2 = new StringBuilder();
            for (int i = 2; i < command.length; ++i) {
                builderArg2.append(command[i]).append(" ");
            }
            String arg2 = builderArg2.toString();
            String str = ((FileMap) fileMap).put(arg1.trim(), arg2.trim());
            if (str.equals("new")) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(str);
            }
        } catch (IOException e) {
            System.err.println("put: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "put";
    }
}
