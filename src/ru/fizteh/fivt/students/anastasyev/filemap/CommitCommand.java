package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class CommitCommand implements Command<FileMapTableProvider> {
    @Override
    public boolean exec(FileMapTableProvider state, String[] command) {
        if (command.length != 1) {
            System.err.println("commit: Usage - commit");
            return false;
        }
        FileMapTable fileMapTable = state.getCurrentFileMapTable();
        if (fileMapTable == null) {
            System.out.println("no table");
            return false;
        }
        System.out.println(fileMapTable.commit());
        return true;
    }

    @Override
    public String commandName() {
        return "commit";
    }
}
