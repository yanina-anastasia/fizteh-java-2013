package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;

import java.io.IOException;

public class PutCommand implements Command<FileMapTable> {
    @Override
    public boolean exec(FileMapTable state, String[] command) {
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
            FileMap db = null;
            try {
                db = state.getMyState(arg1.trim().hashCode());
            } catch (IOException e) {
                if (e.getMessage().equals("no table")) {
                    System.out.println("no table");
                    return true;
                }
                System.err.println(e.getMessage());
                return false;
            }
            if (db == null) {
                try {
                    state.openFileMap((arg1.trim().hashCode()));
                } catch (IOException e) {
                    throw e;
                }
            }
            String str = state.put(arg1.trim(), arg2.trim());
            if (str == null) {
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
