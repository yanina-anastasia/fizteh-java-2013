package ru.fizteh.fivt.students.vorotilov.db;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.vorotilov.shell.FileUtil;
import ru.fizteh.fivt.students.vorotilov.shell.FileWasNotDeleted;

import java.io.File;
import java.io.IOException;

public class VorotilovTableProvider implements TableProvider {

    private final File rootDir;

    VorotilovTableProvider(File rootDir) {
        if (rootDir == null) {
            throw new IllegalArgumentException("Main root dir is null");
        } else if (!rootDir.exists()) {
            throw new IllegalArgumentException("Proposed root dir not exists");
        } else if (!rootDir.isDirectory()) {
            throw new IllegalArgumentException("Proposed object is not directory");
        }
        this.rootDir = rootDir;
    }

    @Override
    public VorotilovTable getTable(String name) {
        testTableName(name);
        File tableRootDir = new File(rootDir, name);
        if (!tableRootDir.exists()) {
            return null;
        } else {
            return new VorotilovTable(tableRootDir);
        }
    }

    @Override
    public VorotilovTable createTable(String name) {
        testTableName(name);
        File tableRootDir = new File(rootDir, name);
        if (tableRootDir.exists()) {
            return null;
        } else {
            if (!tableRootDir.mkdir()) {
                throw new IllegalStateException("Can't make table root dir");
            }
            return new VorotilovTable(tableRootDir);
        }
    }

    @Override
    public void removeTable(String name) {
        testTableName(name);
        File tableRootDir = new File(rootDir, name);
        if (!tableRootDir.exists()) {
            throw new IllegalStateException("No table with this name");
        } else {
            try {
                FileUtil.recursiveDelete(rootDir, tableRootDir);
            } catch (IOException | FileWasNotDeleted e) {
                throw new IllegalStateException("Can't delete table");
            }
        }
    }

    /**
     * Проверяет имя таблицы на корректность
     *
     * @param name Название таблицы.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    private void testTableName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("Table name is empty");
        }
    }

    public File getRoot() {
        return rootDir;
    }

}
