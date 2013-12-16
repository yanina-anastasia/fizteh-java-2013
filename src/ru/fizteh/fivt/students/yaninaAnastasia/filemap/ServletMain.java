package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;
import ru.fizteh.fivt.students.yaninaAnastasia.shell.Shell;

import java.io.IOException;
import java.util.ArrayList;

public class ServletMain {
    public static void main(String[] args) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("Error with getting property");
            System.exit(1);
        }
        DatabaseTableProviderFactory factory = new DatabaseTableProviderFactory();
        DatabaseTableProvider tempProvider = null;
        try {
            tempProvider = factory.create(path);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException exp) {
            System.err.println(exp.getMessage());
            System.exit(1);
        }

        ServletState curState = new ServletState(tempProvider);
        curState.database = tempProvider;
        Shell shell = new Shell(curState);
        ArrayList<Command> cmdList = new ArrayList<>();

        cmdList.add(new CommandStartHTTP());
        cmdList.add(new CommandStopHTTP());
        cmdList.add(new CommandExit());

        shell.fillHashMap(cmdList);

        shell.interActive();
    }
}
