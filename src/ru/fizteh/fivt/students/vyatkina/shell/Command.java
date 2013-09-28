package ru.fizteh.fivt.students.vyatkina.shell;

/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 25.09.13
 * Time: 13:36
 * To change this template use File | Settings | File Templates.
 */
public interface Command {
    public String getName ();
    void execute (Shell shell);
}
