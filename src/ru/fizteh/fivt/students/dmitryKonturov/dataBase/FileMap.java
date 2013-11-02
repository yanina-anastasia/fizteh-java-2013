package ru.fizteh.fivt.students.dmitryKonturov.dataBase;

import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMap {

    public static void main(String[] args) {
        String dbDir = System.getProperty("fizteh.db.dir");
        if (dbDir == null) {
            System.err.println("Empty property");
            System.exit(1);
        }

        MultiFileMapShell shell = null;
        try {
            Path dbDirPath = Paths.get(dbDir);
            shell = new MultiFileMapShell(dbDirPath);
        } catch (DatabaseException e) {
            System.err.println(e.toString());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Wrong property");
            System.exit(1);
        }

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
