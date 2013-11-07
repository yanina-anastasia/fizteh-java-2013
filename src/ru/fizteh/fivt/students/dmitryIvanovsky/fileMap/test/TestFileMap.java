package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapProvider;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.ErrorShell;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestFileMap {

    private static Table fileMap;
    private static String nameTable;
    private static CommandShell mySystem;
    private static Path pathTables;
    private static FileMapProvider multiMap;

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

        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(String.class);
        list.add(Integer.class);

        try {
            nameTable = "table";
            fileMap = multiMap.createTable("table", list);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
    public void getPutOneKey() throws ParseException {
        Storeable st = multiMap.deserialize(fileMap, "<row><col>qwe</col><col>12</col></row>");
        fileMap.put("1", st);
        assertEquals(st, fileMap.get("1"));
        fileMap.remove("1");
    }

    /*@Test
    public void getPutManyKey() throws ParseException {
        Storeable st1 = multiMap.deserialize(fileMap, "<row><col>qwe1</col><col>1</col></row>");
        fileMap.put("1", st1);
        Storeable st2 = multiMap.deserialize(fileMap, "<row><col>qwe2</col><col>2</col></row>");
        fileMap.put("2", st2);
        Storeable st3 = multiMap.deserialize(fileMap, "<row><col>qwe3</col><col>3</col></row>");
        fileMap.put("3", st3);
        Storeable st4 = multiMap.deserialize(fileMap, "<row><col>qwe4</col><col>4</col></row>");
        fileMap.put("4", st4);

        assertEquals("123", fileMap.get("qwe2"));


        assertEquals("789", fileMap.get("qwe4"));

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
    }  */

    @AfterClass
    public static void tearDown() {
        try {
            mySystem.rm(new String[]{pathTables.toString()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
