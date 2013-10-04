package ru.fizteh.fivt.students.valentinbarishev.shell;

public interface ShellCommand {
    public void run();
    public boolean isMyCommand(String[] command);
    public String getName();
}