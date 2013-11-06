package ru.fizteh.fivt.students.mikhaylova_daria.db;

import org.junit.*;
import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.mikhaylova_daria.shell.MyFileSystem;


import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

public class TesterOfTable {

//    private static Table table;
//    private static File tableTempFile;
//    private static String workingTable;
//
//    public static void removeFile(String name) {
//        try {
//            MyFileSystem.removing(name);
//        } catch (IOException e) {
//            System.err.println("Ошибка при удалении временного файла");
//            System.exit(1);
//        }
//    }
//
//    @BeforeClass
//    public static void beforeClass() throws Exception {
//        File tempDb = File.createTempFile("darya", "mikhailova");
//        workingTable = tempDb.getName();
//        tableTempFile = new File(workingTable);
//        if (!tempDb.delete()) {
//            System.err.println("Ошибка при удалении временного файла");
//            System.exit(1);
//        }
//        if (!tableTempFile.mkdir()) {
//            System.err.println("Ошибка при создании временного файла");
//            System.exit(1);
//        }
//        table = new TableData(tableTempFile);
//    }
//
//    @Test
//    public void correctGetNameShouldEquals() {
//        assertEquals(workingTable, table.getName());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void getNullKeyShouldFail() {
//        table.get(null);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void getSpaceKeyShouldFail() {
//        table.get("");
//    }
//
//    @Test
//    public void getExistingKey() {
//        table.put("a", "b");
//        assertEquals(table.get("a"), "b");
//    }
//
//    @Test
//    public void getNonexistentKeyShouldNull() {
//        table.remove("b");
//        assertNull(table.get("b"));
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void putNullKeyShouldFail() {
//        table.put(null, "ds");
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void putNullValueShouldFail() {
//        table.put("p", null);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void putSpaceKeyShouldFail() {
//        table.put("    ", "ds");
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void putSpaceValueShouldFail() {
//        table.put("p", "  ");
//    }
//
//    @Test
//    public void putNewKeyShouldNull() {
//        assertNull(table.put("new", "value"));
//    }
//
//    @Test
//    public void putOldKeyReturnOverwrite() {
//        table.put("key", "valueOld");
//        assertEquals(table.put("key", "valueNew"), "valueOld");
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void removeNullKeyShouldFail() {
//        table.remove(null);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void removeSpaceKeyShouldFail() {
//        table.remove(" ");
//    }
//
//    @Test
//    public void removeNonexistentKeyShouldNull() {
//        table.remove("key");
//        assertNull("Неправильно работает remove или get", table.get("key"));
//        assertNull(table.remove("key"));
//    }
//
//    @Test
//    public void removeExistentKeyShouldNull() {
//        table.put("key", "value");
//        assertEquals("Неправильно работает put или get", "value", table.get("key"));
//        assertEquals(table.remove("key"), "value");
//    }
//
//    @Test
//    public void putToEmptyTableFourKeysGetOneRemoveOneOverwriteOneCountSize() {
//        int nBefore = table.size();
//        table.put("new1", "value");
//        table.put("new2", "value");
//        table.put("new3", "value");
//        assertEquals("Не работает put или get", table.get("new1"), "value");
//        assertEquals("не работает remove", table.remove("new1"), "value");
//        table.put("new2", "value");
//        int commitSize = table.commit();
//        int nAfter = table.size();
//        assertEquals("неправильный подсчёт элементов", 2, nAfter - nBefore);
//        assertNotEquals("неправильно работает commit", 0, commitSize);
//    }
//
//    @Test
//    public void commitEmpty() {
//        table.commit();
//        assertEquals(table.commit(), 0);
//    }
//
//    @Test
//    public void commitRollback() {
//        table.commit();
//        assertEquals(table.rollback(), 0);
//    }
//
//    @Test
//    public void commitNewState() {
//        table.put("new5", "v");
//        table.put("new6", "value");
//        table.put("new7", "d");
//        table.put("new8", "a");
//        table.commit();
//        table.put("new5", "v");
//        table.put("new6", "new value");
//        assertEquals("неправильно работает remove или put", table.remove("new7"), "d");
//        assertEquals("неправильно работает remove или put", table.remove("new8"), "a");
//        table.put("new8", "b");
//        assertNull("неправильно работает remove", table.remove("nonexistent"));
//        table.get("new8");
//        assertEquals(table.commit(), 3);
//    }
//
//    @Test
//    public void rollbackOldState() {
//        table.put("new5", "v");
//        table.put("new6", "value");
//        table.put("new7", "d");
//        table.put("new8", "a");
//        table.commit();
//        table.put("new5", "v");
//        table.put("new6", "new value");
//        assertEquals("неправильно работает remove или put", table.remove("new7"), "d");
//        assertEquals("неправильно работает remove или put", table.remove("new8"), "a");
//        table.put("new8", "b");
//        assertNull("неправильно работает remove", table.remove("nonexistent"));
//        table.get("new8");
//        assertEquals(table.rollback(), 3);
//    }
//
//    @AfterClass
//    public static void afterAll() {
//        String name = tableTempFile.toPath().toString();
//        removeFile(name);
//    }
}


