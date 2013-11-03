package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.File;
import java.io.IOException;
import ru.fizteh.fivt.students.chernigovsky.filemap.Command;
import ru.fizteh.fivt.students.chernigovsky.filemap.ExitException;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;
import ru.fizteh.fivt.students.chernigovsky.filemap.StateProvider;


public class CommandDrop implements Command {
    public String getName() {
        return "drop";
    }
    public int getArgumentsCount() {
        return 1;
    }

    public void execute(StateProvider stateProvider, String[] args) throws IOException, ExitException {
        File table = new File(stateProvider.getCurrentState().getDbDirectory(), args[1]);
        if (table.exists()) {
            File currentTable = new File(stateProvider.getCurrentState().getDbDirectory(), stateProvider.getCurrentState().getTableName());
            if (table.equals(currentTable)) {
                stateProvider.changeCurrentState(new State(stateProvider.getCurrentState().getDbDirectory(), null));
            }
            delete(table);
            System.out.println("dropped");

        } else {
            System.out.println(args[1] + " not exists");
        }
    }

    private void delete(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            if (file.delete()) {
                return;
            } else {
                throw new IOException("Delete error");
            }
        }
        for (File f : file.listFiles()) {
            delete(f);
        }
        if (!file.delete()) {
            throw new IOException("Delete error");
        }
    }
}
