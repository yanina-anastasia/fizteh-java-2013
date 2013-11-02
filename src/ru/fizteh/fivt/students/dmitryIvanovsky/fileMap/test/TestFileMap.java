package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMap;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.ErrorShell;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.assertEquals;

public class TestFileMap {

    private static Table fileMap;
    private static String nameTable;
    private static CommandShell mySystem;
    private static Path pathTables;

    @BeforeClass
    public static void setUp() {
        nameTable = "1_table";
        pathTables = Paths.get(".");
        mySystem = new CommandShell(pathTables.toString(), false, false);

        try {
            mySystem.mkdir(new String[]{pathTables.resolve(nameTable).toString()});
        } catch (ErrorShell e) {
            e.printStackTrace();
        }

        try {
            fileMap = new FileMap(pathTables, nameTable);
        } catch (Exception e) {
            e.printStackTrace();
            FileMapUtils.getMessage(e);
        }

    }

    @Test
    public void correctGetNameShouldEquals() {
        assertEquals(nameTable, fileMap.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullKeyShouldFail() {
        fileMap.get(null);
    }

    @Test
    public void getPutOneKey() {
        fileMap.put("1", "2");
        assertEquals("2", fileMap.get("1"));
        fileMap.put("1", "3");
        assertEquals("3", fileMap.get("1"));
        fileMap.put("1", "2");
        assertEquals("2", fileMap.get("1"));
        fileMap.remove("1");
    }

    @Test
    public void getPutManyKey() {
        fileMap.put("qwe1", "345");
        fileMap.put("qwe2", "123");
        assertEquals("123", fileMap.get("qwe2"));
        fileMap.put("qwe3", "456");
        fileMap.put("qwe4", "789");
        assertEquals("789", fileMap.get("qwe4"));
        fileMap.put("1", "1");
        fileMap.put("1", "2");
        fileMap.put("1", "3");
        assertEquals("3", fileMap.get("1"));
        fileMap.remove("qwe1");
        fileMap.remove("qwe2");
        fileMap.remove("qwe3");
        fileMap.remove("qwe4");
        fileMap.remove("1");
        fileMap.remove("2");
    }

    @Test
    public void correctSizeAfterPut() {
        fileMap.put("1", "123");
        fileMap.put("2", "123");
        fileMap.put("3", "123");
        fileMap.put("4", "123");
        assertEquals(4, fileMap.size());
        fileMap.remove("1");
        fileMap.remove("2");
        assertEquals(2, fileMap.size());
        fileMap.remove("1");
        fileMap.remove("2");
        fileMap.remove("3");
        fileMap.remove("4");
        assertEquals(0, fileMap.size());
    }

    @Test
    public void getOutKey() {
        assertEquals(null, fileMap.get("1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKey() {
        fileMap.put(null, "132");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullValue() {
        fileMap.put("!23", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNlValue() {
        fileMap.put("!23\nwerf", "!23");
    }

    @Test()
    public void commitSimpleCount() {
        fileMap.commit();
        fileMap.put("123", "3453");
        assertEquals(1, fileMap.commit());
        fileMap.remove("123");
    }

    @Test()
    public void commitHardCount() {
        fileMap.remove("1");
        fileMap.remove("2");
        fileMap.commit();
        fileMap.put("1", "1");
        fileMap.put("2", "2");
        assertEquals(2, fileMap.commit());

        fileMap.put("1", "1");
        fileMap.remove("1");
        fileMap.put("1", "1");
        assertEquals(0, fileMap.commit());

        fileMap.put("1", "1");
        fileMap.remove("1");
        fileMap.put("1", "2");
        assertEquals(1, fileMap.commit());
        fileMap.remove("1");
        fileMap.remove("2");
        fileMap.commit();
    }

    @Test()
    public void rollbackAfterRemove() {
        fileMap.remove("1");
        fileMap.remove("2");
        fileMap.commit();
        fileMap.put("1", "1");
        fileMap.put("2", "2");
        assertEquals(fileMap.rollback(), 2);
        assertEquals(null, fileMap.get("1"));
        assertEquals(null, fileMap.get("2"));
        fileMap.remove("1");
        fileMap.remove("2");
    }

    @Test()
    public void rollbackAfterEdit() {
        fileMap.put("1", "1");
        fileMap.commit();
        fileMap.put("1", "2");
        fileMap.rollback();
        assertEquals("1", fileMap.get("1"));
    }

    @AfterClass
    public static void tearDown() {
        try {
            mySystem.rm(new String[]{pathTables.resolve(nameTable).toString()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
