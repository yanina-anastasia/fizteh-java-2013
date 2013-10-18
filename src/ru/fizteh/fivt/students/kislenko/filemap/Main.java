package ru.fizteh.fivt.students.kislenko.filemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;
import ru.fizteh.fivt.students.kislenko.shell.Shell;
import ru.fizteh.fivt.students.kislenko.shell.State;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        //String dbAddress = System.getProperty("fizteh.db.dir");
        MapBuilder mb = new MapBuilder();
        try {
            String dbAddress = "";
            Path db = new File(dbAddress).getCanonicalFile().toPath().resolve("db.dat");
            State state = new FilemapState(db);
            mb.buildMap((FilemapState) state);
            Command[] commandList = new Command[3];
            commandList[0] = new CommandRemove();
            commandList[1] = new CommandGet();
            commandList[2] = new CommandPut();
            Shell shell = new Shell(state, commandList);
            if (args.length == 0) {
                shell.interactiveMode();
            } else {
                shell.batchMode(args);
            }
            mb.fillFile((FilemapState) state);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}