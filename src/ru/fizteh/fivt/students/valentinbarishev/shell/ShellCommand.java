package ru.fizteh.fivt.students.valentinbarishev.shell;

/**
 * Created with IntelliJ IDEA.
 * User: Valik
 * Date: 02.10.13
 * Time: 23:37
 * To change this template use File | Settings | File Templates.
 */

public interface ShellCommand {
    public void run();
    public boolean isMyCommand(String[] command);
    public String getName();
}