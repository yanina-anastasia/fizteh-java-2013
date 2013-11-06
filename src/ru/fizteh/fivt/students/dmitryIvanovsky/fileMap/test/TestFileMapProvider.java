package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapProvider;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.ErrorShell;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

    @Test()
    public void removeCreateTable() {
        File file = new File(String.valueOf(pathTables.resolve("12345").toAbsolutePath()));
        multiMap.createTable("12345");
        assertTrue(file.isDirectory());
        multiMap.removeTable("12345");
        assertFalse(file.exists());
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

