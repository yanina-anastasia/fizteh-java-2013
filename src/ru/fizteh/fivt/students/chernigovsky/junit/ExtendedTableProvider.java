package ru.fizteh.fivt.students.chernigovsky.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;

public interface ExtendedTableProvider extends TableProvider {
    File getDbDirectory();
    ExtendedTable getTable(String name);
    ExtendedTable createTable(String name);
}
