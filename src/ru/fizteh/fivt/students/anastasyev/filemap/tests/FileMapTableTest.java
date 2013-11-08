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

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyValue() throws ParseException {
        Storeable storeable = tableProvider.deserialize(currTable, value);
        storeable.setColumnAt(7, "     ");
        currTable.put("key", storeable);
    }

    @Test
    public void testPutNewKey() throws ParseException {
        assertNull(currTable.put("new", tableProvider.deserialize(currTable, value)));
    }

    @Test
    public void testPutOldKey() throws ParseException {
        Storeable valueOld = tableProvider.deserialize(currTable, value);
        String valueNewString = "[10,11,22,33,4.4,5,true,\"new1\",\"new2\"]";
        Storeable valueNew = tableProvider.deserialize(currTable, valueNewString);
        assertNull(currTable.put("key", valueOld));
        assertEquals(currTable.put("key", valueNew), valueOld);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNl() throws ParseException, IOException {
        String val = "[15,\"string\",\"second string\"]";
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        classList.add(String.class);
        classList.add(String.class);
        Table table = tableProvider.createTable("table", classList);
        Storeable valueOld = tableProvider.deserialize(table, val);
        valueOld.setColumnAt(2, "     ");
        currTable.put("key", valueOld);
    }

    @Test
    public void testPut() throws ParseException {
        Storeable valueOld = tableProvider.deserialize(currTable, value);
        String valueNewString = "[null,null,null,null,null,null,null,null,null]";
        Storeable valueNew = tableProvider.deserialize(currTable, valueNewString);
        assertNull(currTable.put("key", valueOld));
        assertEquals(currTable.put("key", valueNew), valueOld);
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
        assertEquals(currTable.remove("key"), valueStoreable);
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
        assertEquals(table.put("key1", value1Storeable), value1Storeable);
        assertNull(table.put("key2", value2Storeable));
        assertEquals(table.put("key2", value2Storeable), value2Storeable);
        assertNull(table.put("key3", value3Storeable));
        assertEquals(table.put("key3", value3Storeable), value3Storeable);
        assertEquals(table.remove("key1"), value1Storeable);
        assertEquals(table.put("key2", value1Storeable), value2Storeable);
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
        Assert.assertEquals(tableProvider.serialize(table, value1Storeable), value1);
        String value2 = "[7,null,\"val2\"]";
        Storeable value2Storeable = tableProvider.deserialize(table, value2);
        Assert.assertEquals(tableProvider.serialize(table, value2Storeable), value2);
        String value3 = "[7,\"value3\",null]";
        Storeable value3Storeable = tableProvider.deserialize(table, value3);
        Assert.assertEquals(tableProvider.serialize(table, value3Storeable), value3);
        String value4 = "[1,\"value4\",\"val4\"]";
        Storeable valueStoreable = tableProvider.deserialize(table, value4);
        Assert.assertEquals(tableProvider.serialize(table, valueStoreable), value4);
        String newValue = "[1,\"value\",\"newValue\"]";
        Storeable newValueStoreable = tableProvider.deserialize(table, newValue);
        Assert.assertEquals(tableProvider.serialize(table, newValueStoreable), newValue);

        assertNull(table.put("key1", value1Storeable));
        assertNull(table.put("key2", value2Storeable));
        assertEquals(table.remove("key2"), value2Storeable);
        assertNull(table.put("key3", value3Storeable));

        assertEquals(table.commit(), 2);

        assertEquals(table.remove("key1"), value1Storeable);
        assertNull(table.put("key4", valueStoreable));
        assertEquals(table.put("key3", newValueStoreable), value3Storeable);
        assertEquals(table.remove("key4"), valueStoreable);
        assertNull(table.get("key1"));

        assertEquals(table.rollback(), 2);
        assertNotNull(table.get("key1"));

        assertEquals(tableProvider.serialize(table, table.remove("key1")), value1);
        assertNull(table.put("key1", value1Storeable));
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
        Assert.assertEquals(tableProvider.serialize(table, value1Storeable), value1);
        String value2 = "[7,\"value2\"]";
        Storeable value2Storeable = tableProvider.deserialize(table, value2);
        Assert.assertEquals(tableProvider.serialize(table, value2Storeable), value2);
        String value3 = "[7,\"value3\"]";
        Storeable value3Storeable = tableProvider.deserialize(table, value3);
        Assert.assertEquals(tableProvider.serialize(table, value3Storeable), value3);
        String value4 = "[8752,\"value4\"]";
        Storeable valueStoreable = tableProvider.deserialize(table, value4);
        Assert.assertEquals(tableProvider.serialize(table, valueStoreable), value4);
        String newValue = "[1,\"newValue\"]";
        Storeable newValueStoreable = tableProvider.deserialize(table, newValue);
        Assert.assertEquals(tableProvider.serialize(table, newValueStoreable), newValue);

        assertNull(table.put("key1", value1Storeable));
        assertEquals(table.put("key1", value1Storeable), value1Storeable);
        assertNull(table.put("key2", value2Storeable));
        assertEquals(table.put("key2", value2Storeable), value2Storeable);
        assertNull(table.put("key3", value3Storeable));
        assertEquals(table.put("key3", value3Storeable), value3Storeable);
        assertEquals(table.commit(), 3);

        assertEquals(table.remove("key1"), value1Storeable);
        assertNull(table.put("key4", valueStoreable));
        assertEquals(table.put("key2", valueStoreable), value2Storeable);
        assertEquals(table.remove("key4"), valueStoreable);
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

        Assert.assertNull(table.put("commit", rollback));
        Assert.assertEquals(table.get("commit"), rollback);
        Assert.assertEquals(table.rollback(), 1);

        Assert.assertNull(table.get("commit"));
        Assert.assertNull(table.put("commit", rollback));
        Assert.assertEquals(table.get("commit"), rollback);
        Assert.assertEquals(table.commit(), 1);

        Assert.assertEquals(table.remove("commit"), rollback);
        Assert.assertNull(table.put("commit", rollback1));

        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.get("commit"), rollback1);
    }
}
