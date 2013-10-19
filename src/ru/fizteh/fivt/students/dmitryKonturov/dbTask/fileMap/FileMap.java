package ru.fizteh.fivt.students.dmitryKonturov.dbTask.fileMap;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class FileMap {

    public static void main(String[] args) {
        String dbDir = ".";
        final String fileName = "db.dat";
        try {
            dbDir = System.getProperty("fizteh.db.dir");
        } catch (Exception e) {
            System.err.println("Couldn't read property.");
            System.exit(1);
        }

        try {
            FileMapShell shell = new FileMapShell(Paths.get(dbDir), fileName);
            if (args.length > 0) {
                StringBuilder builder = new StringBuilder();
                for (String arg : args) {
                    builder.append(arg);
                    builder.append(" ");
                }
                try {
                    shell.packageMode(builder.toString());
                } catch (FileMapShell.ShellException e) {
                    System.err.println(e);
                    System.exit(3);
                }
            } else {
                shell.interactiveMode();
            }
        } catch (IOException e) {
            System.err.println(String.format("Couldn't load and use db.dat in %s: " + e.getMessage(), dbDir));
            System.exit(2);
        } catch (InvalidPathException e) {
            System.err.println(String.format("Couldn't transform %s to Path", dbDir));
            System.exit(4);
        } catch (Exception e) {
            System.err.println("Something bad occurred: " + e.getMessage());
            System.exit(5);
        }
    }

}
