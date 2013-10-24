package ru.fizteh.fivt.students.dmitryKonturov.dbTask.fileMap;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMap {

    public static void main(String[] args) {
        String dbDir;
        final String fileName = "db.dat";
        dbDir = "/home/kontr/testDir";
        /* todo
        dbDir = System.getProperty("fizteh.db.dir");
        if (dbDir == null) {
            System.err.println("Empty property.");
            System.exit(1);
        } */
        try {
            Path dbDirPath = Paths.get(dbDir);
            if (!Files.isDirectory(dbDirPath)) {
                System.err.println("Property is not directory.");
                System.exit(1);
            }
            FileMapShell shell = new FileMapShell(dbDirPath, fileName);
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
                    try {
                        shell.closeDbFile();
                    } catch (Exception exc) {
                        System.err.println(exc.toString());
                    }
                    System.exit(1);
                }
            } else {
                shell.interactiveMode();
            }
        } catch (InvalidPathException e) {
            System.err.println(String.format("Couldn't transform %s to Path", dbDir));
            System.exit(1);
        } catch (FileMapShell.ShellException e) {
            System.err.println("Something bad occurred: " + e.toString());
            System.exit(1);
        }
    }

}
