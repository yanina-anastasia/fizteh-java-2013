package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;

import java.io.IOException;

public class GetCommand implements Command<FileMapTableProvider> {
    @Override
    public boolean exec(FileMapTableProvider state, String[] command) {
        if (command.length != 2) {
            System.err.println("get: Usage - get key");
            return false;
        }
        try {
            FileMapTable currTable = state.getCurrentFileMapTable();
            if (currTable == null) {
                System.out.println("no table");
                return false;
            }
            FileMap db = null;
            try {
                db = currTable.getMyState(command[1].hashCode());
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return false;
            }
            if (db == null) {
                System.out.println("not found");
                return true;
            }
            String str = currTable.get(command[1]);
            if (str == null) {
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
