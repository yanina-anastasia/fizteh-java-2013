package ru.fizteh.fivt.students.anastasyev.filemap;

import java.io.IOException;

public class PutCommand implements Command {
    @Override
    public boolean exec(String[] command) {
        if (command.length != 3) {
            System.err.println("put: Usage - put key value");
            return false;
        }
        try {
            String str = FileMap.put(command[1], command[2]);
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
