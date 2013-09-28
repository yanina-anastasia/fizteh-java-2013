package ru.fizteh.fivt.students.vyatkina.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 25.09.13
 * Time: 20:04
 * To change this template use File | Settings | File Templates.
 */
public class cpCommand implements Command {
    @Override
    public String getName () {
        return "cp";
    }
    @Override
    public void execute (Shell shell) {
        File sourse = new File (shell.shellScanner.next());
        if (sourse.isDirectory ()) {
        }
        File destination = new File (shell.shellScanner.next (), sourse.getName ());
        if (!destination.exists ()) {
            try {
            destination.getParentFile().mkdirs();
            destination.createNewFile();
            FileUtils.copyFile(sourse, destination);
            } catch (IOException io) {
                System.err.println (getName() + ": failed to copy file " + sourse.getName() + " to directory " + destination.getParent());
                io.printStackTrace();
            }
        } else {
            System.err.println (getName() + ": failed to copy file " + sourse.getName() + " to directory" + destination.getParent()
                                + ": this file already exists");
        }
    }
}
