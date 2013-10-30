package ru.fizteh.fivt.students.drozdowsky.database;

import ru.fizteh.fivt.students.drozdowsky.pathController.PathController;
import ru.fizteh.fivt.students.drozdowsky.pathController.ShellCommands;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMap {
    FileHashMap currentDatabase;
    File dir;
    PathController curDir;

    public MultiFileHashMap(File dir) throws IOException {
        this.dir = dir;
        if (!(dir.exists())) {
            fatalError("Database doesn't exist");
        }
        String[] content = dir.list();
        for (String directory : content) {
            File temp = new File(dir.getAbsoluteFile() + "/" + directory);
            FileHashMap base = new FileHashMap(temp);
        }
        curDir = new PathController(dir.getAbsolutePath());
    }

    public boolean create(String[] args) {
        if (args.length != 2) {
            error("usage: create name");
            return false;
        }

        File newTable = new File(dir.getAbsolutePath() + "/" + args[1]);
        if (newTable.exists()) {
            System.out.println(args[1] + " exists");
            return true;
        } else {
            newTable.mkdir();
            System.out.println("created");
            return true;
        }
    }

    public boolean drop(String[] args) {
        if (args.length != 2) {
            error("usage: drop name");
            return false;
        }

        File table = new File(dir.getAbsolutePath() + "/" + args[1]);
        if (table.exists()) {
            if (table.getAbsolutePath().equals(currentDatabase.getPath())) {
                currentDatabase.close();
            }
            String[] newArgs = {"rm", args[1]};
            ShellCommands.rm(curDir, newArgs);
            System.out.println("dropped");
            return true;
        } else {
            System.out.println(args[1] + " not exists");
            return true;
        }
    }

    public boolean use(String[] args) {
        if (args.length != 2) {
            error("usage: use name");
            return false;
        }

        File table = new File(dir.getAbsolutePath() + "/" + args[1]);
        if (table.exists()) {
            if (currentDatabase != null) {
                currentDatabase.close();
            }
            try {
                currentDatabase = new FileHashMap(table);
            } catch (IOException e) {
                error(e.getMessage());
                return false;
            }
            System.out.println("using " + args[1]);
            return true;
        } else {
            System.out.println(args[1] + " not exists");
            return true;
        }

    }

    public boolean put(String[] args) {
        if (currentDatabase == null) {
            System.err.println("no table");
            return false;
        }
        return currentDatabase.put(args);
    }

    public boolean get(String[] args) {
        if (currentDatabase == null) {
            System.err.println("no table");
            return false;
        }
        return currentDatabase.get(args);
    }

    public boolean remove(String[] args) {
        if (currentDatabase == null) {
            System.err.println("no table");
            return false;
        }
        return currentDatabase.remove(args);
    }

    public boolean exit(String[] args) {
        if (currentDatabase == null) {
            System.exit(0);
            return true;
        }
        return currentDatabase.exit(args);
    }

    private void fatalError(String error) throws IOException {
        throw new IOException(error);
    }

    private void error(String aError) {
        System.err.println(aError);
    }
}
