package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.StringMethods;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        try {
            System.setProperty("fizteh.db.dir", "/home/hope");
            File dataFile = new File(System.getProperty("fizteh.db.dir"), "db.dat");
            FileMapController fileMap = new FileMapController(dataFile);
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }
            fileMap.addCommand(new GetCommand());
            fileMap.addCommand(new PutCommand());
            fileMap.addCommand(new RemoveCommand());
            fileMap.addCommand(new ExitCommand());
            if (args.length == 0) {
                fileMap.interactiveMode();
            } else {
                String arguments = StringMethods.join(Arrays.asList(args), " ");
                fileMap.batchMode(arguments);
            }

        } catch (IOException e) {
            System.err.println("not managed to create file");
        }
    }
}
