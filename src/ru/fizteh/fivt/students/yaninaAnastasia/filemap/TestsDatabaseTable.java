/*package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TestsDatabaseTable {
    Table table;
    TableProviderFactory factory;
    TableProvider provider;

    @Before
    public void beforeTest() {
        factory = new DatabaseTableProviderFactory();
        provider = factory.create("C:\\temp\\database_table_test");
        table = provider.createTable("testTable");
    }

    @After
    public void afterTest() {
        provider.removeTable("testTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() {
        table.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() {
        table.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullName() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullName() {
        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() {
        table.put("", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyValue() {
        table.put("key", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEmptyName() {
        table.get("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmptyName() {
        table.remove("");
    }

    @Test
    public void testPutGet() {
        Assert.assertNull(table.put("key", "value1"));
        Assert.assertEquals(table.get("key"), "value1");
        Assert.assertNotNull(table.put("key", "value2"));
        Assert.assertEquals(table.put("key", "value3"), "value2");
        Assert.assertEquals(table.get("key"), "value3");
        table.remove("key");
    }

    @Test
    public void testPutGetRemove() {
        Assert.assertNull(table.put("key", "value1"));
        Assert.assertEquals(table.remove("key"), "value1");
        Assert.assertNull(table.put("key", "value1"));
        Assert.assertEquals(table.get("key"), "value1");
        Assert.assertEquals(table.remove("key"), "value1");
        Assert.assertNull(table.get("key"));
    }

    @Test
    public void oneMoreTestPutGetRemove() {
        Assert.assertNull(table.put("ключ", "значение1"));
        Assert.assertEquals(table.remove("ключ"), "значение1");
        Assert.assertNull(table.put("ключ", "значение1"));
        Assert.assertEquals(table.get("ключ"), "значение1");
        Assert.assertEquals(table.remove("ключ"), "значение1");
        Assert.assertNull(table.get("ключ"));
    }

    @Test
    public void firstTestCommit() {
        Assert.assertEquals(table.size(), 0);
        table.put("key1", "value1");
        table.put("key2", "value2");
        table.put("key3", "value3");
        table.remove("key3");
        Assert.assertEquals(table.size(), 2);
        Assert.assertEquals(table.commit(), 2);
    }

    @Test
    public void secondTestCommit() {
        Assert.assertEquals(table.size(), 0);
        table.put("key", "value1");
        table.put("key", "value2");
        table.put("key", "value3");
        Assert.assertEquals(table.get("key"), "value3");
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.commit(), 1);
        table.remove("key");
        Assert.assertEquals(table.size(), 0);
        Assert.assertEquals(table.commit(), 1);
    }

    @Test
    public void thirdTestCommit() {
        Assert.assertEquals(table.size(), 0);
        table.put("key", "value1");
        table.remove("key");
        table.put("key2", "value2");
        table.remove("key2");
        Assert.assertNull(table.get("key2"));
        Assert.assertEquals(table.size(), 0);
        Assert.assertEquals(table.commit(), 0);
    }

    @Test
    public void firstTestRollback() {
        Assert.assertEquals(table.size(), 0);
        table.put("key", "value1");
        table.put("key2", "value2");
        table.put("key3", "value3");
        Assert.assertEquals(table.rollback(), 3);
    }

    @Test
    public void secondTestRollback() {
        Assert.assertEquals(table.size(), 0);
        table.put("key", "value1");
        table.remove("key");
        Assert.assertEquals(table.size(), 0);
        Assert.assertEquals(table.rollback(), 0);
        table.put("key2", "value2");
        Assert.assertEquals(table.commit(), 1);
        table.put("key2", "value3");
        table.put("key2", "value2");
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.rollback(), 0);
    }

    @Test
    public void commonTest() {
        Assert.assertEquals(table.size(), 0);
        Assert.assertNull(table.put("1", "один"));
        Assert.assertNull(table.put("2", "два"));
        Assert.assertNull(table.put("3", "четыре"));
        Assert.assertNotNull(table.put("3", "три"));
        Assert.assertEquals(table.size(), 3);
        Assert.assertEquals(table.commit(), 3);
        Assert.assertEquals(table.remove("1"), "один");
        Assert.assertEquals(table.remove("2"), "два");
        Assert.assertEquals(table.remove("3"), "три");
        Assert.assertEquals(table.size(), 0);
        Assert.assertEquals(table.rollback(), 3);
        Assert.assertEquals(table.size(), 3);
    }
}     */
