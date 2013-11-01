package ru.fizteh.fivt.students.belousova.multifilehashmap.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.belousova.multifilehashmap.ChangesCountingTableProvider;
import ru.fizteh.fivt.students.belousova.multifilehashmap.ChangesCountingTableProviderFactory;
import ru.fizteh.fivt.students.belousova.multifilehashmap.MultiFileTable;
import ru.fizteh.fivt.students.belousova.multifilehashmap.MultiFileTableProviderFactory;

import java.io.File;

public class MultiFileTableTest {
    private ChangesCountingTableProviderFactory tableProviderFactory = new MultiFileTableProviderFactory();
    private ChangesCountingTableProvider tableProvider = tableProviderFactory.create("javatest");
    private Table multiFileTable;

    @Before
    public void setUp() throws Exception {
        if (tableProvider.getTable("testTable") != null) {
            tableProvider.removeTable("testTable");
        }
        multiFileTable = tableProvider.createTable("testTable");
    }

    @After
    public void tearDown() throws Exception {
        tableProvider.removeTable("testTable");
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(multiFileTable.getName(), "testTable");
    }

    @Test
    public void testGetEnglish() throws Exception {
        multiFileTable.put("testGetEnglishKey", "testGetEnglishValue");
        Assert.assertEquals(multiFileTable.get("testGetEnglishKey"), "testGetEnglishValue");
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

    @Test(expected = IllegalArgumentException.class)
    public void testGetEmpty() throws Exception {
        multiFileTable.get("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNl() throws Exception {
        multiFileTable.get("    ");
    }

    @Test
    public void testPutNew() throws Exception {
        Assert.assertNull(multiFileTable.put("testPutNewKey", "testPutNewValue"));
        multiFileTable.remove("testPutNewKey");
    }

    @Test
    public void testPutOld() throws Exception {
        multiFileTable.put("testPutOldKey", "testPutOldValue");
        Assert.assertEquals(multiFileTable.put("testPutOldKey", "testPutOldValue1"), "testPutOldValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws Exception {
        multiFileTable.put(null, "testPutNullKeyValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() throws Exception {
        multiFileTable.put("", "testPutEmptyKeyValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNlKey() throws Exception {
        multiFileTable.put("   ", "testPutNlKeyValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws Exception {
        multiFileTable.put("testPutNullValueKey", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyValue() throws Exception {
        multiFileTable.put("testPutEmptyValueKey", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNlValue() throws Exception {
        multiFileTable.put("testPutNlValueKey", "   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        multiFileTable.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmpty() throws Exception {
        multiFileTable.remove("");
    }

    @Test
    public void testRemoveExisted() throws Exception {
        multiFileTable.put("testRemoveExistedKey", "testRemoveExistedValue");
        Assert.assertEquals(multiFileTable.remove("testRemoveExistedKey"), "testRemoveExistedValue");
    }

    @Test
    public void testRemoveNotExisted() throws Exception {
        Assert.assertNull(multiFileTable.remove("testRemoveNotExistedKey"));
    }

    @Test
    public void testSize() throws Exception {
        multiFileTable.put("testSizeKey1", "testSizeValue1");
        multiFileTable.put("testSizeKey2", "testSizeValue2");
        multiFileTable.put("testSizeKey3", "testSizeValue3");
        Assert.assertEquals(multiFileTable.size(), 3);
    }

    @Test
    public void testCommit() throws Exception {
        multiFileTable.put("testCommitKey1", "testCommitValue1");
        multiFileTable.put("testCommitKey2", "testCommitValue2");
        multiFileTable.put("testCommitKey3", "testCommitValue3");
        Assert.assertEquals(multiFileTable.commit(), 3);
    }

    @Test
    public void testRollback() throws Exception {
        multiFileTable.put("testRollbackKey1", "testRollbackValue1");
        multiFileTable.put("testRollbackKey2", "testRollbackValue2");
        multiFileTable.put("testRollbackKey3", "testRollbackValue3");
        Assert.assertEquals(multiFileTable.rollback(), 3);
    }
}

