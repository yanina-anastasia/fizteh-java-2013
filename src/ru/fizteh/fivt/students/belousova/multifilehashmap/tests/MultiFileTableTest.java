package ru.fizteh.fivt.students.belousova.multifilehashmap.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.belousova.multifilehashmap.MultiFileTable;

import java.io.File;

public class MultiFileTableTest {
    private Table multiFileTable;
    private File testDirectory;
    @Before
    public void setUp() throws Exception {
        testDirectory = new File("javatest");
        if (testDirectory.exists()) {
            testDirectory.delete();
        }
        testDirectory.mkdir();
        multiFileTable = new MultiFileTable(testDirectory);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(multiFileTable.getName(), testDirectory.getName());
    }

    @Test
    public void testGetEnglish() throws Exception {
        multiFileTable.put("key", "value");
        Assert.assertEquals(multiFileTable.get("key"), "value");
    }

    @Test
    public void testGetRussian() throws Exception {
        multiFileTable.put("ключ", "значение");
        Assert.assertEquals(multiFileTable.get("ключ"), "значение");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() throws Exception {
        multiFileTable.get(null);
    }

    @Test
    public void testPutNew() throws Exception {
        Assert.assertNull(multiFileTable.put("key", "value"));
    }

    @Test
    public void testPutOld() throws Exception {
        multiFileTable.put("key", "value");
        Assert.assertEquals(multiFileTable.put("key", "value1"), "value");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {
        multiFileTable.put(null, "value");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {
        multiFileTable.put("key", null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        multiFileTable.remove(null);
    }

    @Test
    public void testRemoveExisted() throws Exception {
        multiFileTable.put("key", "value");
        Assert.assertEquals(multiFileTable.remove("key"), "value");
    }

    @Test
    public void testRemoveNotExisted() throws Exception {
        Assert.assertNull(multiFileTable.remove("key"));
    }

    @Test
    public void testSize() throws Exception {
        multiFileTable.put("key1", "value1");
        multiFileTable.put("key2", "value2");
        multiFileTable.put("key3", "value3");
        Assert.assertEquals(multiFileTable.size(), 3);
    }

    @Test
    public void testCommit() throws Exception {
        multiFileTable.put("key1", "value1");
        multiFileTable.put("key2", "value2");
        multiFileTable.put("key3", "value3");
        Assert.assertEquals(multiFileTable.commit(), 3);
    }

    @Test
    public void testRollback() throws Exception {
        multiFileTable.put("key1", "value1");
        multiFileTable.put("key2", "value2");
        multiFileTable.put("key3", "value3");
        Assert.assertEquals(multiFileTable.rollback(), 3);
    }
}

