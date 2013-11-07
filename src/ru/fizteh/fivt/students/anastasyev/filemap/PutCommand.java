package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class PutCommand implements Command<FileMapTableProvider> {
    @Override
    public boolean exec(FileMapTableProvider state, String[] command) {
        if (command.length < 3) {
            System.err.println("put: Usage - put key value");
            return false;
        }
        String arg1 = command[1];
        StringBuilder builderArg2 = new StringBuilder();
        for (int i = 2; i < command.length; ++i) {
            builderArg2.append(command[i]).append(" ");
        }
        String arg2 = builderArg2.toString();
        FileMapTable currTable = state.getCurrentFileMapTable();
        if (currTable == null) {
            System.out.println("no table");
            return false;
        }
        String str = currTable.put(arg1.trim(), arg2.trim());
        if (str == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println("old " + str);
        }
        return true;
    }

    @Override
    public String commandName() {
        return "put";
    }
}
