package ru.fizteh.fivt.students.vyatkina.shell;

import java.io.File;
/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 25.09.13
 * Time: 16:07
 * To change this template use File | Settings | File Templates.
 */
public class rmCommand implements Command {
    @Override
    public String getName () {
        return "rm";
    }
    @Override
    public void execute (Shell shell) {
        File fileToDelete = new File (shell.currentDirectory, shell.shellScanner.next ());
        if (fileToDelete.exists ()) {
            delete (fileToDelete);
        }  else {
            System.err.println (this.getName() + " :cannot remove '" +fileToDelete.getName() + "': no such file or directory" );
        }
    }

    private void delete (File file) {
        if (file.isDirectory ()) {
            File [] innerFiles = file.listFiles ();
            for (File f: innerFiles)  {
                delete (f);
            }
        }
        file.delete ();
    }
}
