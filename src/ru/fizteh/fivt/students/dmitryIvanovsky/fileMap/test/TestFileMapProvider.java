package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMap;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapProvider;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.ErrorShell;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestFileMapProvider {

    private static TableProvider multiMap;
    private static CommandShell mySystem;
    private static Path pathTables;

    @BeforeClass
    public static void setUp() {

        pathTables = Paths.get(".");
        mySystem = new CommandShell(pathTables.toString(), false, false);
        pathTables = pathTables.resolve("bdTest");
        try {
            mySystem.mkdir(new String[]{pathTables.toString()});
        } catch (ErrorShell e) {
            e.printStackTrace();
        }

        try {
            multiMap = new FileMapProvider(pathTables.toAbsolutePath().toString());
        } catch (Exception e) {
            e.printStackTrace();
            FileMapUtils.getMessage(e);
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull() {
        multiMap.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNull() {
        multiMap.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNull() {
        multiMap.removeTable(null);
    }

    @Test()
    public void createGetTable() {
        multiMap.createTable("123");
        Table a = multiMap.getTable("123");
        Table b = multiMap.getTable("123");
        assertEquals(a, b);
        multiMap.removeTable("123");
    }

    @Test()
    public void getTableNotExist() {
        assertNull(multiMap.getTable("12345679"));
    }

    @AfterClass
    public static void tearDown() {
        try {
            mySystem.rm(new String[]{pathTables.toString()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

