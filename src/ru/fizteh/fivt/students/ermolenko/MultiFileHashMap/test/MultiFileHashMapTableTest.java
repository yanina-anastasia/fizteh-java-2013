package ru.fizteh.fivt.students.ermolenko.multifilehashmap.test;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.ermolenko.multifilehashmap.MultiFileHashMapTable;
import ru.fizteh.fivt.students.ermolenko.multifilehashmap.MultiFileHashMapTableProvider;
import ru.fizteh.fivt.students.ermolenko.multifilehashmap.MultiFileHashMapTableProviderFactory;

public class MultiFileHashMapTableTest {

    private MultiFileHashMapTableProviderFactory factory = new MultiFileHashMapTableProviderFactory();
    private MultiFileHashMapTableProvider provider = factory.create("Java_test");
    MultiFileHashMapTable table;

    @Before
    public void setUp() throws Exception {

        if (provider.getTable("testingTable") != null) {
            provider.removeTable("testingTable");
        }
        table = provider.createTable("testingTable");
    }

    @Test
    public void testGetName() throws Exception {

        Assert.assertEquals(table.getName(), "testingTable");
    }

    @Test
    public void testGetInEnglish() throws Exception {

        table.put("getEnglishKey", "getEnglishValue");
        Assert.assertEquals(table.get("getEnglishKey"), "getEnglishValue");
    }

    @Test
    public void testGetInRussian() throws Exception {

        table.put("ключ", "значение");
        Assert.assertEquals(table.get("ключ"), "значение");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {

        table.get(null);
    }

    @Test
    public void testPutNew() throws Exception {

        Assert.assertNull(table.put("putNewKey", "putNewValue"));
        table.remove("putNewKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {

        table.put(null, "valueOfNullKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() throws Exception {

        table.put("", "valueOfEmptyKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {

        table.remove(null);
    }

    @Test
    public void testRemoveExisted() throws Exception {

        table.put("Key", "Value");
        Assert.assertEquals(table.remove("Key"), "Value");
    }

    @Test
    public void testRemoveNotExisted() throws Exception {

        Assert.assertNull(table.remove("Key"));
    }

    @Test
    public void testSize() throws Exception {
        table.put("Key1", "Value1");
        table.put("Key2", "Value2");
        table.put("Key3", "Value3");
        Assert.assertEquals(table.size(), 3);
    }

    @Test
    public void testPutAndRollback() throws Exception {
        table.put("Key1", "Value1");
        table.put("Key2", "Value2");
        table.put("Key3", "Value3");
        Assert.assertEquals(table.rollback(), 3);
    }

    @Test
    public void testPutCommit() throws Exception {
        table.put("Key1", "Value1");
        table.put("Key2", "Value2");
        table.put("Key3", "Value3");
        Assert.assertEquals(table.commit(), 3);
    }

    @Test
    public void testRemovePutCommit() throws Exception {
        table.put("Key", "Value");
        table.commit();
        table.remove("Key");
        table.put("Key", "Value");
        Assert.assertEquals(table.commit(), 0);
    }
}
