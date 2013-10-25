package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.File;
import java.io.IOException;

public class CommandDrop implements Command {
    public String getName() {
        return "drop";
    }
    public int getArgumentsCount() {
        return 1;
    }

    public void execute(State state, String[] args) throws IOException, ExitException {
        File table = new File(state.getDbDirectory(), args[1]);
        if (table.exists()) {
            if (table.equals(state.getCurrentTable())) {
                state.changeCurrentTable(null);
            }
            delete(table);
            System.out.println("dropped");

        } else {
            state.writeTable(state.getCurrentTable());
            state.changeCurrentTable(table);
            state.readTable(state.getCurrentTable());
            System.out.print("using ");
            System.out.println(args[1]);
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
