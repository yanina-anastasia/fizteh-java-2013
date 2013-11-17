package ru.fizteh.fivt.students.anastasyev.filemap.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;

import ru.fizteh.fivt.students.anastasyev.filemap.FileMapTableProviderFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

public class FileMapTableTest {
    TableProviderFactory factory;
    TableProvider tableProvider;
    Table currTable;
    String currTableName;
    List<Class<?>> classes;
    String value = "[0,1,2,3,4,5.4,false,\"string1\",\"string2\"]";

    private boolean storeableEquals(Storeable first, Storeable second, Table table) {
        if (first == null && second == null) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            Object val1 = first.getColumnAt(i);
            Object val2 = second.getColumnAt(i);
            if (val1 == null) {
                if (val2 != null) {
                    return false;
                }
            } else if (!val1.equals(val2)) {
                return false;
            }
        }
        return true;
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setCurrTable() throws IOException {
        factory = new FileMapTableProviderFactory();
        classes = new ArrayList<Class<?>>();
        classes.add(Integer.class);
        classes.add(Integer.class);
        classes.add(Integer.class);
        classes.add(Long.class);
        classes.add(Float.class);
        classes.add(Double.class);
        classes.add(Boolean.class);
        classes.add(String.class);
        classes.add(String.class);
        tableProvider = factory.create(folder.newFolder().toString());
        assertNotNull(tableProvider);
        currTable = tableProvider.createTable("TestTable", classes);
        currTableName = "TestTable";
        assertEquals(currTable.getName(), currTableName);
    }

    @After
    public void delete() throws IOException {
        tableProvider.removeTable(currTableName);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(currTableName, currTable.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws ParseException {
        currTable.put(null, tableProvider.deserialize(currTable, value));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() {
        currTable.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() throws ParseException {
        currTable.put("    ", tableProvider.deserialize(currTable, value));
    }

    @Test
    public void testPutNewKey() throws ParseException {
        assertNull(currTable.put("new", tableProvider.deserialize(currTable, value)));
    }

    @Test
    public void testPutChangedKey() throws ParseException {
        Storeable valueOld = tableProvider.deserialize(currTable, value);
        assertNull(currTable.put("key", valueOld));
        valueOld.setColumnAt(0, 1);
        assertFalse(storeableEquals(currTable.get("key"), valueOld, currTable));
    }

    @Test
    public void testPutChangedKey2() throws ParseException, IOException {
        Storeable valueOld = tableProvider.deserialize(currTable, value);
        assertNull(currTable.put("key", valueOld));
        valueOld.setColumnAt(0, 1);
        assertEquals(currTable.commit(), 1);
        assertFalse(storeableEquals(currTable.get("key"), valueOld, currTable));
    }

    @Test
    public void testPutChangedKey3() throws ParseException, IOException {
        Storeable valueOld = tableProvider.deserialize(currTable, value);
        assertNull(currTable.put("key", valueOld));
        assertEquals(currTable.commit(), 1);
        valueOld.setColumnAt(0, 1);
        assertFalse(storeableEquals(currTable.get("key"), valueOld, currTable));
    }

    @Test
    public void testPutOldKey() throws ParseException {
        Storeable valueOld = tableProvider.deserialize(currTable, value);
        String valueNewString = "[10,11,22,33,4.4,5,true,\"new1\",\"new2\"]";
        Storeable valueNew = tableProvider.deserialize(currTable, valueNewString);
        assertNull(currTable.put("key", valueOld));
        assertTrue(storeableEquals(currTable.put("key", valueNew), valueOld, currTable));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutAlienSmallerStoreable() throws ParseException, IOException {
        String val = "[15,\"string\",\"second string\"]";
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        classList.add(String.class);
        classList.add(String.class);
        Table table = tableProvider.createTable("table", classList);
        Storeable valueOld = tableProvider.deserialize(table, val);
        tableProvider.removeTable("table");
        currTable.put("key", valueOld);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutAlienBiggerStoreable() throws ParseException, IOException {
        String val = "[0,1,2,3,4,5.4,false,\"string1\",\"string2\", 1]";
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        classList.add(Integer.class);
        classList.add(Integer.class);
        classList.add(Long.class);
        classList.add(Float.class);
        classList.add(Double.class);
        classList.add(Boolean.class);
        classList.add(String.class);
        classList.add(String.class);
        classList.add(Byte.class);
        Table table = tableProvider.createTable("table", classList);
        Storeable valueOld = tableProvider.deserialize(table, val);
        tableProvider.removeTable("table");
        currTable.put("key", valueOld);
    }

    @Test
    public void testPut() throws ParseException {
        Storeable valueOld = tableProvider.deserialize(currTable, value);
        String valueNewString = "[null,null,null,null,null,null,null,null,null]";
        Storeable valueNew = tableProvider.deserialize(currTable, valueNewString);
        assertNull(currTable.put("key", valueOld));
        assertTrue(storeableEquals(currTable.put("key", valueNew), valueOld, currTable));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKey() {
        currTable.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmptyKey() {
        currTable.remove(" ");
    }

    @Test
    public void testRemoveNotExistsKey() throws ParseException {
        Storeable valueStoreable = tableProvider.deserialize(currTable, value);
        assertNull(currTable.put("key", valueStoreable));
        assertNotNull(currTable.remove("key"));
        assertNull(currTable.remove("key"));
    }

    @Test
    public void testRemoveExistsKey() throws ParseException {
        Storeable valueStoreable = tableProvider.deserialize(currTable, value);
        currTable.put("key", valueStoreable);
        assertTrue(storeableEquals(currTable.remove("key"), valueStoreable, currTable));
    }

    @Test
    public void testGetNotExistsKey() throws Exception {
        assertNull(currTable.get("notExistsKey"));
    }

    @Test
    public void testGetExistsKey() throws Exception {
        Storeable valueStoreable = tableProvider.deserialize(currTable, value);
        assertNull(currTable.put("newKey", valueStoreable));
        assertNotNull(currTable.get("newKey"));
        assertNotNull(currTable.remove("newKey"));
    }

    @Test
    public void testCommit() throws Exception {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        classList.add(String.class);
        Table table = tableProvider.createTable("table", classList);
        int sizeBefore = currTable.size();
        String value1 = "[5, \"value1\"]";
        Storeable value1Storeable = tableProvider.deserialize(table, value1);
        String value2 = "[7, \"value2\"]";
        Storeable value2Storeable = tableProvider.deserialize(table, value2);
        String value3 = "[7, \"value3\"]";
        Storeable value3Storeable = tableProvider.deserialize(table, value3);
        assertNull(table.put("key1", value1Storeable));
        assertTrue(storeableEquals(table.put("key1", value1Storeable), value1Storeable, table));
        assertNull(table.put("key2", value2Storeable));
        assertTrue(storeableEquals(table.put("key2", value2Storeable), value2Storeable, table));
        assertNull(table.put("key3", value3Storeable));
        assertTrue(storeableEquals(table.put("key3", value3Storeable), value3Storeable, table));
        assertTrue(storeableEquals(table.remove("key1"), value1Storeable, table));
        assertTrue(storeableEquals(table.put("key2", value1Storeable), value2Storeable, table));
        assertEquals(table.commit(), 2);
        int sizeAfter = table.size();
        assertEquals(2, sizeAfter - sizeBefore);
    }

    @Test
    public void testRollback() throws Exception {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        classList.add(String.class);
        classList.add(String.class);
        Table table = tableProvider.createTable("table", classList);
        String value1 = "[5,\"value1\",null]";
        Storeable value1Storeable = tableProvider.deserialize(table, value1);
        assertEquals(tableProvider.serialize(table, value1Storeable), value1);
        String value2 = "[7,null,\"val2\"]";
        Storeable value2Storeable = tableProvider.deserialize(table, value2);
        assertEquals(tableProvider.serialize(table, value2Storeable), value2);
        String value3 = "[7,\"value3\",null]";
        Storeable value3Storeable = tableProvider.deserialize(table, value3);
        assertEquals(tableProvider.serialize(table, value3Storeable), value3);
        String value4 = "[1,\"value4\",\"val4\"]";
        Storeable valueStoreable = tableProvider.deserialize(table, value4);
        assertEquals(tableProvider.serialize(table, valueStoreable), value4);
        String newValue = "[1,\"value\",\"newValue\"]";
        Storeable newValueStoreable = tableProvider.deserialize(table, newValue);
        assertEquals(tableProvider.serialize(table, newValueStoreable), newValue);

        assertNull(table.put("key1", value1Storeable));
        assertNull(table.put("key2", value2Storeable));
        assertNotNull(table.put("key2", value2Storeable));
        assertTrue(storeableEquals(table.remove("key2"), value2Storeable, table));
        assertEquals(table.size(), 1);
        assertNull(table.put("key3", value3Storeable));

        assertEquals(table.size(), 2);
        assertEquals(table.rollback(), 2);
        assertEquals(table.rollback(), 0);
        assertEquals(table.commit(), 0);

        assertNull(table.put("key1", value1Storeable));
        assertNull(table.put("key2", value2Storeable));
        assertNull(table.put("key3", value3Storeable));

        assertEquals(table.size(), 3);
        assertEquals(table.commit(), 3);
        assertEquals(table.rollback(), 0);
        assertEquals(table.size(), 3);

        assertTrue(storeableEquals(table.put("key1", value2Storeable), value1Storeable, table));
        assertEquals(table.size(), 3);
        assertTrue(storeableEquals(table.remove("key1"), value2Storeable, table));
        assertNull(table.remove("key1"));
        assertEquals(table.size(), 2);
        assertNull(table.put("key4", valueStoreable));
        assertEquals(table.size(), 3);
        assertTrue(storeableEquals(table.put("key3", newValueStoreable), value3Storeable, table));
        assertTrue(storeableEquals(table.remove("key4"), valueStoreable, table));
        assertNull(table.remove("key4"));
        assertEquals(table.size(), 2);
        assertNull(table.get("key1"));
        assertNull(table.get("key4"));

        assertEquals(table.rollback(), 2);
        assertEquals(table.size(), 3);
        assertTrue(storeableEquals(table.get("key1"), value1Storeable, table));

        assertEquals(tableProvider.serialize(table, table.remove("key1")), value1);
        assertNull(table.put("key1", value1Storeable));
        assertEquals(table.size(), 3);

        assertEquals(table.rollback(), 0);
    }

    @Test
    public void testSize() throws Exception {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        classList.add(String.class);
        Table table = tableProvider.createTable("table", classList);
        String value1 = "[5,\"value1\"]";
        Storeable value1Storeable = tableProvider.deserialize(table, value1);
        assertEquals(tableProvider.serialize(table, value1Storeable), value1);
        String value2 = "[7,\"value2\"]";
        Storeable value2Storeable = tableProvider.deserialize(table, value2);
        assertEquals(tableProvider.serialize(table, value2Storeable), value2);
        String value3 = "[7,\"value3\"]";
        Storeable value3Storeable = tableProvider.deserialize(table, value3);
        assertEquals(tableProvider.serialize(table, value3Storeable), value3);
        String value4 = "[8752,\"value4\"]";
        Storeable valueStoreable = tableProvider.deserialize(table, value4);
        assertEquals(tableProvider.serialize(table, valueStoreable), value4);
        String newValue = "[1,\"newValue\"]";
        Storeable newValueStoreable = tableProvider.deserialize(table, newValue);
        assertEquals(tableProvider.serialize(table, newValueStoreable), newValue);

        assertNull(table.put("key", value1Storeable));
        assertTrue(storeableEquals(table.remove("key"), value1Storeable, table));
        assertNull(table.get("key"));
        assertNull(table.put("key", tableProvider.deserialize(table, value1)));
        assertTrue(storeableEquals(table.get("key"), value1Storeable, table));

        assertNull(table.put("key2", value2Storeable));
        assertTrue(storeableEquals(table.put("key2", value1Storeable), value2Storeable, table));
        assertTrue(storeableEquals(table.get("key2"), value1Storeable, table));
        assertNull(table.put("key3", value3Storeable));
        assertTrue(storeableEquals(table.put("key3", value3Storeable), value3Storeable, table));
        assertEquals(table.commit(), 3);
        assertEquals(table.size(), 3);

        assertTrue(storeableEquals(table.remove("key"), value1Storeable, table));
        assertEquals(table.size(), 2);
        assertNull(table.put("key4", valueStoreable));
        assertEquals(table.size(), 3);
        assertTrue(storeableEquals(table.put("key2", valueStoreable), value1Storeable, table));
        assertTrue(storeableEquals(table.get("key2"), valueStoreable, table));
        assertEquals(table.size(), 3);
        assertTrue(storeableEquals(table.remove("key4"), valueStoreable, table));
        assertEquals(table.size(), 2);
    }

    @Test
    public void testCommitRollback() throws ParseException, IOException {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        classList.add(String.class);
        Table table = tableProvider.createTable("table", classList);

        String rollbackVal = "[7,\"rollback\"]";
        Storeable rollback = tableProvider.deserialize(table, rollbackVal);
        String rollbackVal1 = "[7,\"rollback1\"]";
        Storeable rollback1 = tableProvider.deserialize(table, rollbackVal1);

        assertNull(table.put("commit", rollback));
        assertTrue(storeableEquals(table.get("commit"), rollback, table));
        assertEquals(table.rollback(), 1);

        assertNull(table.get("commit"));
        assertNull(table.put("commit", rollback));
        assertTrue(storeableEquals(table.get("commit"), rollback, table));
        assertEquals(table.commit(), 1);

        assertTrue(storeableEquals(table.remove("commit"), rollback, table));
        assertNull(table.put("commit", rollback1));

        assertEquals(table.commit(), 1);
        assertTrue(storeableEquals(table.get("commit"), rollback1, table));
    }
}
