package ru.fizteh.fivt.students.vorotilov.db;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class TableTest {
    static VorotilovTableProviderFactory tableProviderFactory;
    static VorotilovTableProvider tableProvider;
    VorotilovTable currentTable;
    String currentTableName;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass                           //Before
    public static void setTableProviderFactory() {
        tableProviderFactory = new VorotilovTableProviderFactory();
    }

    @Before
    public void setNewCurrentTable() throws IOException {
        tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(currentTableName, currentTable.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() {
        currentTable.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() {
        currentTable.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() {
        currentTable.put("  ", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyValue() {
        currentTable.put("key", "  ");
    }

    @Test
    public void testPutNewKey() {
        Assert.assertNull(currentTable.put("key", "value"));
    }

    @Test
    public void testPutOldKey() {
        Assert.assertNull(currentTable.put("key", "valueOld"));
        Assert.assertEquals(currentTable.put("key", "valueNew"), "valueOld");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKey() {
        currentTable.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmptyKey() {
        currentTable.remove("");
    }

    @Test
    public void testRemove() {
        Assert.assertNull(currentTable.put("key", "value"));
        Assert.assertNotNull(currentTable.remove("key"));
        Assert.assertNull(currentTable.remove("key"));
    }

    @Test
    public void testGetNotExistedKey() throws Exception {
        Assert.assertNull(currentTable.get("notExistedKey"));
    }

    @Test
    public void testGetExistedKey() throws Exception {
        Assert.assertNull(currentTable.put("newKey", "value"));
        Assert.assertEquals(currentTable.get("newKey"), "value");
    }

    @Test
    public void testCommit() throws Exception {
        int sizeBefore = currentTable.size();
        currentTable.put("key", "value");
        Assert.assertEquals(currentTable.commit(), 1);
        Assert.assertEquals(currentTable.size(), sizeBefore + 1);
    }

    @Test
    public void testRollback() throws Exception {
        int sizeBefore = currentTable.size();
        currentTable.put("key", "value");
        Assert.assertEquals(currentTable.rollback(), 1);
        Assert.assertEquals(sizeBefore, currentTable.size());
    }

    @Test
    public void testSize() throws Exception {
        int sizeBefore = currentTable.size();
        currentTable.put("key1", "value");
        currentTable.put("key2", "value");
        Assert.assertEquals(currentTable.commit(), 2);
        Assert.assertEquals(sizeBefore + 2, currentTable.size());
    }

    @Test
    public void testRollbackOverwritePutAndRemove() {
        VorotilovTable testTable = tableProvider.createTable("newTable");
        testTable.put("key1", "value1");
        Assert.assertEquals(testTable.commit(), 1);
        testTable.put("key1", "newValue");
        testTable.put("key2", "value2");
        testTable.remove("key1");
        Assert.assertEquals(2, testTable.rollback());
    }

    @Test
    public void testCommitRollback() {
        VorotilovTable testTable = tableProvider.createTable("newTable");
        testTable.put("1", "1");
        testTable.commit();
        int size = testTable.size();
        testTable.rollback();
        Assert.assertEquals(size, testTable.size());
    }

    @Test
    public void testPutOverwrite() {
        VorotilovTable testTable = tableProvider.createTable("newTable");
        testTable.put("key1", "value1");
        testTable.put("key1", "value2");
        Assert.assertEquals(1, testTable.rollback());
    }
}
