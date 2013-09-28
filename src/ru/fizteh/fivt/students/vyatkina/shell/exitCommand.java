package ru.fizteh.fivt.students.vyatkina.shell;

/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 25.09.13
 * Time: 14:23
 * To change this template use File | Settings | File Templates.
 */
public class exitCommand implements Command {
    @Override
    public String getName () {
       return "exit";
    }
    @Override
    public void execute (Shell shell) {
      System.exit (0);
    }
}
