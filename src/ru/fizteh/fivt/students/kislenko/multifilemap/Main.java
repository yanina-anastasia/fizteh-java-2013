package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;
import ru.fizteh.fivt.students.kislenko.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        String dbAddress = System.getProperty("fizteh.db.dir");
        try {
            if (dbAddress == null) {
                System.err.println("fuck");
                System.exit(0);
            }
            File dbDir = new File(dbAddress).getCanonicalFile();
            if (!dbDir.isDirectory()) {
                System.err.println("Incorrect database directory.");
                System.exit(1);
            }
            File[] tables = dbDir.listFiles();
            if (tables != null) {
                for (File table : tables) {
                    if (!table.isDirectory()) {
                        System.err.println("Incorrect table format.");
                        System.exit(1);
                    }
                }
            }
            Path db = dbDir.toPath();
            MultiFileHashMapState state = new MultiFileHashMapState(db);
            Command[] commandList = new Command[]{new CommandMultiRemove(), new CommandMultiPut(), new CommandCreate(),
                    new CommandDrop(), new CommandMultiGet(), new CommandUse(), new CommandSize()};
            MultiFilemapBuilder builder = new MultiFilemapBuilder();
            builder.build(state);
            Shell<MultiFileHashMapState> shell = new Shell<MultiFileHashMapState>(state, commandList);
            if (args.length == 0) {
                shell.interactiveMode();
            } else {
                shell.batchMode(args);
            }
            builder.finish(state);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}