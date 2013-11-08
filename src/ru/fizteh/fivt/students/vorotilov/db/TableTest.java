package ru.fizteh.fivt.students.vorotilov.db;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class TableTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testGetName() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        Assert.assertEquals(currentTableName, currentTable.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        currentTable.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        currentTable.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        currentTable.put("  ", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyValue() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        currentTable.put("key", "  ");
    }

    @Test
    public void testPutNewKey() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        Assert.assertNull(currentTable.put("key", "value"));
    }

    @Test
    public void testPutOldKey() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        Assert.assertNull(currentTable.put("key", "valueOld"));
        Assert.assertEquals(currentTable.put("key", "valueNew"), "valueOld");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKey() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        currentTable.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmptyKey() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        currentTable.remove("");
    }

    @Test
    public void testRemove() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        Assert.assertNull(currentTable.put("key", "value"));
        Assert.assertNotNull(currentTable.remove("key"));
        Assert.assertNull(currentTable.remove("key"));
    }

    @Test
    public void testGetNotExistedKey() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        Assert.assertNull(currentTable.get("notExistedKey"));
    }

    @Test
    public void testGetExistedKey() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        Assert.assertNull(currentTable.put("newKey", "value"));
        Assert.assertEquals(currentTable.get("newKey"), "value");
    }

    @Test
    public void testCommit() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        int sizeBefore = currentTable.size();
        currentTable.put("key", "value");
        Assert.assertEquals(currentTable.commit(), 1);
        Assert.assertEquals(currentTable.size(), sizeBefore + 1);
    }

    @Test
    public void testRollback() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        int sizeBefore = currentTable.size();
        currentTable.put("key", "value");
        Assert.assertEquals(currentTable.rollback(), 1);
        Assert.assertEquals(sizeBefore, currentTable.size());
    }

    @Test
    public void testSize() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        int sizeBefore = currentTable.size();
        currentTable.put("key1", "value");
        currentTable.put("key2", "value");
        Assert.assertEquals(currentTable.commit(), 2);
        Assert.assertEquals(sizeBefore + 2, currentTable.size());
    }

    @Test
    public void testRollbackOverwritePutAndRemove() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        currentTable.put("key1", "value1");
        Assert.assertEquals(currentTable.commit(), 1);
        currentTable.put("key1", "newValue");
        currentTable.put("key2", "value2");
        currentTable.remove("key1");
        Assert.assertEquals(2, currentTable.rollback());
    }

    @Test
    public void testCommitRollback() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        currentTable.put("1", "1");
        currentTable.commit();
        int size = currentTable.size();
        currentTable.rollback();
        Assert.assertEquals(size, currentTable.size());
    }

    @Test
    public void testPutOverwrite() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        currentTable.put("key1", "value1");
        currentTable.put("key1", "value2");
        Assert.assertEquals(1, currentTable.rollback());
    }

    @Test
    public void testSizeAfterRollback() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        VorotilovTable currentTable;
        String currentTableName = "TestTable";
        currentTable = tableProvider.createTable(currentTableName);
        currentTable.put("key1", "value1");
        currentTable.put("key2", "value2");
        currentTable.put("key3", "value3");
        Assert.assertEquals(currentTable.commit(), 3);
        Assert.assertEquals(currentTable.size(), 3);
        currentTable.put("key2", "valueNew");
        Assert.assertEquals(currentTable.size(), 3);
        Assert.assertEquals(currentTable.rollback(), 1);
        Assert.assertEquals(currentTable.size(), 3);
    }
}
