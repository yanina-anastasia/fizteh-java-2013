package ru.fizteh.fivt.students.dmitryKonturov.dataBase;


import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMap {

    public static void main(String[] args) {
        String dbDir;
        dbDir = System.getProperty("fizteh.db.dir");
        if (dbDir == null) {
            System.err.println("Empty property");
        }
        Path dbDirPath = null;
        try {
            dbDirPath = Paths.get(dbDir);
            if (!Files.isDirectory(dbDirPath)) {
                System.err.println(dbDir + " is not directory");
                System.exit(1);
            }
            dbDirPath = dbDirPath.resolve("db.dat");
        } catch (Exception e) {
            System.err.println(dbDir + " is not correct path to directory");
            System.exit(1);
        }

        SimpleFileMapShell shell = new SimpleFileMapShell(dbDirPath);
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg);
                builder.append(" ");
            }
            try {
                shell.packageMode(builder.toString());
            } catch (ShellException e) {
                System.err.println(e.toString());
                System.exit(1);
            }
        } else {
            shell.interactiveMode();
        }

    }

}
