package ru.fizteh.fivt.students.vyatkina.shell;

import java.io.File;
import java.io.IOException;
/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 25.09.13
 * Time: 20:46
 * To change this template use File | Settings | File Templates.
 */
public class cdCommand implements Command {
    @Override
    public String getName () {
        return "cd";
    }
    @Override
    public void execute (Shell shell) {
      File newDirectory = new File (shell.currentDirectory, shell.shellScanner.next ());
        try {
        if (newDirectory.exists ()) {
            shell.currentDirectory = newDirectory.getCanonicalFile();
        } else {
            System.err.println (getName() + ": no such file or directory " + newDirectory.toString());
        }
        } catch (IOException io)  {
            System.err.println (getName() + ": failed to enter " + newDirectory.toString());
        }



    }
}
