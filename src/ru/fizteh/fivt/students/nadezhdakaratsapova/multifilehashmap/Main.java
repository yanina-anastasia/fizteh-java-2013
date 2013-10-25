package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Shell;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.StringMethods;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        DataWriter dataWriter = new DataWriter();
        try {
            System.setProperty("fizteh.db.dir", "/home/hope/");
            File dataDirectory = new File(System.getProperty("fizteh.db.dir"));
            if (!dataDirectory.exists()) {
                throw new IOException("The working directory doesn't exist");
            }
            if (!dataDirectory.isDirectory()) {
                throw new IOException("The root directory should be a directory");
            }
            Shell multiFileHashMap = new Shell();
            MultiFileHashMapState state = new MultiFileHashMapState(dataDirectory);
            multiFileHashMap.addCommand(new CreateCommand(state));
            multiFileHashMap.addCommand(new DropCommand(state));
            multiFileHashMap.addCommand(new ExitCommand(state));
            multiFileHashMap.addCommand(new GetCommand(state));
            multiFileHashMap.addCommand(new PutCommand(state));
            multiFileHashMap.addCommand(new RemoveCommand(state));
            multiFileHashMap.addCommand(new UseCommand(state));
            if (args.length == 0) {
                multiFileHashMap.interactiveMode();
            } else {
                String arguments = StringMethods.join(Arrays.asList(args), " ");
                try {
                    multiFileHashMap.batchMode(arguments);
                    if (state.getCurTable() != null) {
                        dataWriter.writeData(state);
                    }
                } catch (IOException e) {
                    if (state.getCurTable() != null) {
                        dataWriter.writeData(state);
                    }
                    System.err.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
