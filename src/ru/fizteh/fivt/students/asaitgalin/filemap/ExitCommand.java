package ru.fizteh.fivt.students.asaitgalin.filemap;

import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class ExitCommand implements Command {

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public void execute(String[] args) throws IOException {
        System.out.println("exit");
        System.exit(0);
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
