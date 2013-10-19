package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;
import ru.fizteh.fivt.students.anastasyev.shell.State;

public class GetCommand implements Command {
    @Override
    public boolean exec(State fileMap, String[] command) {
        if (command.length != 2) {
            System.err.println("get: Usage - get key");
            return false;
        }
        try {
            String str = ((FileMap) fileMap).get(command[1]);
            if (str.equals("not found")) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(str);
            }
        } catch (Exception e) {
            System.err.println("get: Can't get the key");
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "get";
    }
}
