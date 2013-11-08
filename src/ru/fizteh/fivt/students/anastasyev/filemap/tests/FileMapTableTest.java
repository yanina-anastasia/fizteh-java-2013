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
    String value = "[15,\"string\"]";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setCurrTable() throws IOException {
        factory = new FileMapTableProviderFactory();
        classes = new ArrayList<Class<?>>();
        classes.add(Integer.class);
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
        Storeable storeable = tableProvider.deserialize(currTable, "[15,\"45\"]");
        storeable.setColumnAt(1, "     ");
        currTable.put("key", storeable);
    }

    @Test
    public void testPutNewKey() throws ParseException {
        assertNull(currTable.put("new", tableProvider.deserialize(currTable, value)));
    }

    @Test
    public void testPutOldKey() throws ParseException {
        String valueOldString = "[5, \"valueOld\"]";
        Storeable valueOld = tableProvider.deserialize(currTable, valueOldString);
        String valueNewString = "[5, \"valueNew\"]";
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
        String valueOldString = "[5, null]";
        Storeable valueOld = tableProvider.deserialize(currTable, valueOldString);
        String valueNewString = "[null, \"valueNew\"]";
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
        int sizeBefore = currTable.size();
        String value1 = "[5, \"value1\"]";
        Storeable value1Storeable = tableProvider.deserialize(currTable, value1);
        String value2 = "[7, \"value2\"]";
        Storeable value2Storeable = tableProvider.deserialize(currTable, value2);
        String value3 = "[7, \"value3\"]";
        Storeable value3Storeable = tableProvider.deserialize(currTable, value3);
        assertNull(currTable.put("key1", value1Storeable));
        assertEquals(currTable.put("key1", value1Storeable), value1Storeable);
        assertNull(currTable.put("key2", value2Storeable));
        assertEquals(currTable.put("key2", value2Storeable), value2Storeable);
        assertNull(currTable.put("key3", value3Storeable));
        assertEquals(currTable.put("key3", value3Storeable), value3Storeable);
        assertEquals(currTable.remove("key1"), value1Storeable);
        assertEquals(currTable.put("key2", value1Storeable), value2Storeable);
        assertEquals(currTable.commit(), 2);
        int sizeAfter = currTable.size();
        assertEquals(2, sizeAfter - sizeBefore);
    }

    @Test
    public void testRollback() throws Exception {
        String value1 = "[5,\"value1\"]";
        Storeable value1Storeable = tableProvider.deserialize(currTable, value1);
        Assert.assertEquals(tableProvider.serialize(currTable, value1Storeable), value1);
        String value2 = "[7,\"value2\"]";
        Storeable value2Storeable = tableProvider.deserialize(currTable, value2);
        Assert.assertEquals(tableProvider.serialize(currTable, value2Storeable), value2);
        String value3 = "[7,\"value3\"]";
        Storeable value3Storeable = tableProvider.deserialize(currTable, value3);
        Assert.assertEquals(tableProvider.serialize(currTable, value3Storeable), value3);
        Storeable valueStoreable = tableProvider.deserialize(currTable, value);
        Assert.assertEquals(tableProvider.serialize(currTable, valueStoreable), value);
        String newValue = "[1,\"newValue\"]";
        Storeable newValueStoreable = tableProvider.deserialize(currTable, newValue);
        Assert.assertEquals(tableProvider.serialize(currTable, newValueStoreable), newValue);

        assertNull(currTable.put("key1", value1Storeable));
        assertNull(currTable.put("key2", value2Storeable));
        assertEquals(currTable.remove("key2"), value2Storeable);
        assertNull(currTable.put("key3", value3Storeable));

        assertEquals(currTable.commit(), 2);

        assertEquals(currTable.remove("key1"), value1Storeable);
        assertNull(currTable.put("key4", valueStoreable));
        assertEquals(currTable.put("key3", newValueStoreable), value3Storeable);
        assertEquals(currTable.remove("key4"), valueStoreable);
        assertNull(currTable.get("key1"));

        assertEquals(currTable.rollback(), 2);
        assertNotNull(currTable.get("key1"));

        assertEquals(tableProvider.serialize(currTable, currTable.remove("key1")), value1);
        assertNull(currTable.put("key1", value1Storeable));
        assertEquals(currTable.rollback(), 0);
    }

    @Test
    public void testSize() throws Exception {
        String value1 = "[5,\"value1\"]";
        Storeable value1Storeable = tableProvider.deserialize(currTable, value1);
        Assert.assertEquals(tableProvider.serialize(currTable, value1Storeable), value1);
        String value2 = "[7,\"value2\"]";
        Storeable value2Storeable = tableProvider.deserialize(currTable, value2);
        Assert.assertEquals(tableProvider.serialize(currTable, value2Storeable), value2);
        String value3 = "[7,\"value3\"]";
        Storeable value3Storeable = tableProvider.deserialize(currTable, value3);
        Assert.assertEquals(tableProvider.serialize(currTable, value3Storeable), value3);
        Storeable valueStoreable = tableProvider.deserialize(currTable, value);
        Assert.assertEquals(tableProvider.serialize(currTable, valueStoreable), value);
        String newValue = "[1,\"newValue\"]";
        Storeable newValueStoreable = tableProvider.deserialize(currTable, newValue);
        Assert.assertEquals(tableProvider.serialize(currTable, newValueStoreable), newValue);

        assertNull(currTable.put("key1", value1Storeable));
        assertEquals(currTable.put("key1", value1Storeable), value1Storeable);
        assertNull(currTable.put("key2", value2Storeable));
        assertEquals(currTable.put("key2", value2Storeable), value2Storeable);
        assertNull(currTable.put("key3", value3Storeable));
        assertEquals(currTable.put("key3", value3Storeable), value3Storeable);
        assertEquals(currTable.commit(), 3);
        assertEquals(currTable.remove("key1"), value1Storeable);
        assertNull(currTable.put("key4", valueStoreable));
        assertEquals(currTable.put("key2", valueStoreable), value2Storeable);
        assertEquals(currTable.remove("key4"), valueStoreable);
        assertEquals(currTable.size(), 2);
    }

    @Test
    public void testCommitRollback() throws ParseException, IOException {
        String rollbackVal = "[7,\"rollback\"]";
        Storeable rollback = tableProvider.deserialize(currTable, rollbackVal);
        String rollbackVal1 = "[7,\"rollback1\"]";
        Storeable rollback1 = tableProvider.deserialize(currTable, rollbackVal1);
        Assert.assertNull(currTable.put("commit", rollback));
        Assert.assertEquals(currTable.get("commit"), rollback);
        Assert.assertEquals(currTable.rollback(), 1);
        Assert.assertNull(currTable.get("commit"));
        Assert.assertNull(currTable.put("commit", rollback));
        Assert.assertEquals(currTable.get("commit"), rollback);
        Assert.assertEquals(currTable.commit(), 1);
        Assert.assertEquals(currTable.remove("commit"), rollback);
        Assert.assertNull(currTable.put("commit", rollback1));
        Assert.assertEquals(currTable.commit(), 1);
        Assert.assertEquals(currTable.get("commit"), rollback1);
    }
}
