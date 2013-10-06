package ru.fizteh.fivt.students.asaitgalin.shell.commands;

public class ExitCommand implements Command {

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public void execute(String[] args) {
        System.exit(0);
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
