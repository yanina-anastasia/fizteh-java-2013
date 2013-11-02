package ru.fizteh.fivt.students.drozdowsky.database;

import ru.fizteh.fivt.students.drozdowsky.PathController;
import ru.fizteh.fivt.students.drozdowsky.Commands.ShellCommands;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.drozdowsky.utils.Utils;

import java.awt.geom.IllegalPathStateException;
import java.io.File;

public class MultiFileHashMap implements TableProvider {
    File dir;
    PathController curDir;

    public MultiFileHashMap(String workingDir) {
        this.dir = new File(workingDir);
        if (!(dir.exists())) {
            throw new IllegalPathStateException();
        }
        String[] content = dir.list();
        for (String directory : content) {
            File temp = new File(dir.getAbsoluteFile() + "/" + directory);
            FileHashMap base = new FileHashMap(temp);
        }
        curDir = new PathController(dir.getAbsolutePath());
    }

    public FileHashMap getTable(String name) {
        if (!Utils.isValid(name) || name.contains("/") || name.contains("\\")) {
            throw new IllegalArgumentException();
        }
        File table = new File(dir.getAbsolutePath() + "/" + name);
        if (table.exists()) {
            return new FileHashMap(table);
        } else {
            return null;
        }
    }

    public FileHashMap createTable(String name) {
        if (!Utils.isValid(name) || name.contains("/") || name.contains("\\")) {
            throw new IllegalArgumentException();
        }
        File newTable = new File(dir.getAbsolutePath() + "/" + name);
        if (newTable.exists()) {
            return null;
        } else {
            newTable.mkdir();
            return new FileHashMap(newTable);
        }
    }

    public void removeTable(String name) {
        if (!Utils.isValid(name) || name.contains("/") || name.contains("\\")) {
            throw new IllegalArgumentException();
        }
        File table = new File(dir.getAbsolutePath() + "/" + name);
        if (table.exists()) {
            String[] newArgs = {"rm", name};
            ShellCommands.rm(curDir, newArgs);
        } else {
            throw new IllegalStateException();
        }
    }
}
