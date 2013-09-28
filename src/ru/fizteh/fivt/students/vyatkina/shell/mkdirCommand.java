package ru.fizteh.fivt.students.vyatkina.shell;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 25.09.13
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class mkdirCommand implements Command {
    @Override
    public String getName () {
        return "mkdir";
    }
    @Override
    public void execute (Shell shell) {
            File directory = new File (shell.currentDirectory, shell.shellScanner.next());
            directory.mkdirs();
    }

}
