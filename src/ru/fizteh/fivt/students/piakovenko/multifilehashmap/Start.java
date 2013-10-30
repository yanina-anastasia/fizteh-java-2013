package ru.fizteh.fivt.students.piakovenko.multifilehashmap;

import ru.fizteh.fivt.students.piakovenko.filemap.DataBase;
import ru.fizteh.fivt.students.piakovenko.shell.CurrentStatus;
import ru.fizteh.fivt.students.piakovenko.shell.Shell;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 19.10.13
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */

public class Start {
        public static void main(String[] args) {
            File fileMapStorage = new File(System.getProperty("fizteh.db.dir"));
            if (!fileMapStorage.exists()) {
                System.err.println(fileMapStorage + " doesn't exist!");
                System.exit(1);
            }
            Shell shell = new Shell();
            DataBasesCommander dbc = new DataBasesCommander(shell, fileMapStorage);
            dbc.initialize();
            shell.changeInvitation("MultiFile Database $ ");
            shell.start(args);
        }
}
