package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Launcher;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (System.getProperty("fizteh.db.dir") == null) {
            System.err.println("Set home data base's directory");
            System.err.println("Use: -Dfizteh.db.dir=<directory>");
            System.exit(1);
        }
        FileMapTableProvider fileMapTableProvider = null;
        try {
            File dbProviderDir = new File(System.getProperty("fizteh.db.dir")).getCanonicalFile();
            if (!dbProviderDir.exists()) {
                if (!dbProviderDir.mkdirs()) {
                    System.err.println("Can't create " + dbProviderDir.getName());
                }
            }
            if (dbProviderDir.isFile()) {
                if (!dbProviderDir.delete()) {
                    System.err.println("Can't delete file " + dbProviderDir.getName());
                }
                if (!dbProviderDir.mkdirs()) {
                    System.err.println("Can't create " + dbProviderDir.getName());
                }
            }
            fileMapTableProvider = new FileMapTableProvider(System.getProperty("fizteh.db.dir"));
            Launcher launcher = new Launcher(fileMapTableProvider, args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
