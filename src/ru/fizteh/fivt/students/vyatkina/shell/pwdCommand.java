package ru.fizteh.fivt.students.vyatkina.shell;

/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 25.09.13
 * Time: 13:38
 * To change this template use File | Settings | File Templates.
 */
public class pwdCommand implements Command {
    @Override
    public String getName () {
        return "pwd";
    }
    @Override
    public void execute (Shell shell) {
        System.out.println (shell.currentDirectory.getAbsolutePath ());
    }
}
