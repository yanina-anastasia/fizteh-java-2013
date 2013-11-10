package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.filemap.TableState;
import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;

public class CommandSize implements Command {
    private TableState state;

    public CommandSize(TableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "size";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.getCurrentTable() == null) {
            System.out.println("no table");
        } else {
            System.out.println(state.sizeOfCurrentTable());
        }
    }

    @Override
    public int getArgCount() {
        return 0;
    }
}
