package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class CreateCommand implements Command<FileMapTableProvider> {
    @Override
    public boolean exec(FileMapTableProvider state, String[] command) {
        if (command.length != 2) {
            System.err.println("create: Usage - create tablename");
            return false;
        }
        try {
            Table res = state.createTable(command[1]);
            if (res == null) {
                System.out.println(command[1] + " exists");
            } else {
                System.out.println("created");
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return false;
        } catch (RuntimeException e) {
            System.err.println("Bad symbol in name");
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "create";
    }
}
