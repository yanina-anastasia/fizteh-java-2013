package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;
import ru.fizteh.fivt.students.anastasyev.shell.State;

public class RemoveCommand implements Command {
    @Override
    public boolean exec(State fileMap, String[] command) {
        if (command.length != 2) {
            System.err.println("remove: Usage - remove key");
            return false;
        }
        try {
            String str = ((FileMap) fileMap).remove(command[1]);
            if (str.equals("not found")) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } catch (Exception e) {
            System.err.println("remove: Can't remove the key");
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "remove";
    }
}
