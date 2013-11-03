package ru.fizteh.fivt.students.asaitgalin.storable.commands;


import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class CreateCommand implements Command {

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String[] parseCommandLine(String s) {
        // split by spaces except expressions in ()-brackets
        String[] args = s.split("\\s+(?![^\\(]*\\))");
        return args;
    }

    @Override
    public void execute(String[] args) throws IOException {
        // args[0] = name
        // args[1] = tablename
        // args[2] = columns (...)
    }

    @Override
    public int getArgsCount() {
        return 2;
    }
}
