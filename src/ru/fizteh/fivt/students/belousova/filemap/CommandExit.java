package ru.fizteh.fivt.students.belousova.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;

public class CommandExit implements Command {
    private final String name = "exit";
    private Table state;

    public CommandExit(Table state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getArgCount() {
        return 0;
    }

    @Override
    public void execute(String[] args) throws IOException {
        state.commit();
        System.out.println("exit");
        System.exit(0);
    }
}