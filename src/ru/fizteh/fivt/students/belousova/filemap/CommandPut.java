package ru.fizteh.fivt.students.belousova.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;

public class CommandPut implements Command {
    private static final String name = "put";
    private Table state;

    public CommandPut(Table state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getArgCount() {
        return 2;
    }

    @Override
    public void execute(String[] args) throws IOException {
        String key = args[1];
        String value = args[2];
        String oldValue = state.put(key, value);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}
