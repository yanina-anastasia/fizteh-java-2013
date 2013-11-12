package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapProvider;
import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.ErrorShell;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

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
    public void createNull() throws IOException {
        multiMap.createTable(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNull() {
        multiMap.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNull() throws IOException {
        multiMap.removeTable(null);
    }

    @Test()
    public void createGetTable() throws IOException {
        List<Class<?>> columnType = new ArrayList<>();
        columnType.add(Integer.class);
        multiMap.createTable("123", columnType);
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
    public void removeCreateTable() throws IOException {
        File file = new File(String.valueOf(pathTables.resolve("12345").toAbsolutePath()));
        List<Class<?>> columnType = new ArrayList<>();
        columnType.add(Integer.class);
        multiMap.createTable("12345", columnType);
        assertTrue(file.isDirectory());
        multiMap.removeTable("12345");
        assertFalse(file.exists());
    }

    @Test()
    public void setSerialize() throws IOException {
        List<Class<?>> columnType = new ArrayList<>();
        columnType.add(Integer.class);
        columnType.add(Long.class);
        columnType.add(Byte.class);
        columnType.add(Float.class);
        columnType.add(Double.class);
        columnType.add(Boolean.class);
        columnType.add(String.class);
        Table table = multiMap.createTable("12345", columnType);

        List column = new ArrayList<Serializable>();
        column.add(Integer.parseInt("12"));
        column.add(Long.parseLong("13"));
        column.add(Byte.parseByte("14"));
        column.add(Float.parseFloat("12.2"));
        column.add(Double.parseDouble("12.3"));
        column.add(Boolean.parseBoolean("true"));
        column.add("qwe");

        Storeable st = multiMap.createFor(table, column);

        String res = "<row><col>12</col><col>13</col><col>14</col><col>12.2</col>"
                      + "<col>12.3</col><col>true</col><col>qwe</col></row>";

        assertEquals(res, multiMap.serialize(table, st));
    }

    @Test(expected = ParseException.class)
    public void setErrorXml() throws IOException, ParseException {
        List<Class<?>> columnType = new ArrayList<>();
        columnType.add(Integer.class);
        columnType.add(Long.class);
        Table table = multiMap.createTable("123456", columnType);
        String res = "<row><col>12</col><col>13</col><col>12</col></row>";
        multiMap.deserialize(table, res);
    }

    @Test(expected = ParseException.class)
    public void setErrorXmlType() throws IOException, ParseException {
        List<Class<?>> columnType = new ArrayList<>();
        columnType.add(Integer.class);
        columnType.add(Long.class);
        Table table = multiMap.createTable("123456", columnType);
        String res = "<row><col>12.4</col><col>13</col><col>12</col></row>";
        multiMap.deserialize(table, res);
    }

    @Test(expected = ParseException.class)
    public void setErrorXmlNull() throws IOException, ParseException {
        List<Class<?>> columnType = new ArrayList<>();
        columnType.add(Integer.class);
        columnType.add(Long.class);
        Table table = multiMap.createTable("123456", columnType);
        String res = "<row><col>12.4</col><col><null></col><col>12</col></row>";
        multiMap.deserialize(table, res);
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

