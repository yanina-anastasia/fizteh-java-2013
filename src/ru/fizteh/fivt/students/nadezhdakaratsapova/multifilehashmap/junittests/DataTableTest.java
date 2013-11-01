package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.junittests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileHashMapProvider;

import java.io.File;
import java.io.IOException;

public class DataTableTest {
    private static final String TESTED_DIRECTORY = "/home/hope/JavaTests";
    private static final String TESTED_TABLE = "MyFavouriteTable";
    private TableProvider tableProvider;
    private Table dataTable;

    @Before
    public void setUp() throws Exception {
        tableProvider = new MultiFileHashMapProvider(new File(TESTED_DIRECTORY));
        dataTable = tableProvider.createTable(TESTED_TABLE);
    }

    @After
    public void tearDown() throws Exception {
        tableProvider.removeTable(TESTED_TABLE);
    }

    @Test
    public void getNameTest() throws Exception {
        Assert.assertEquals(dataTable.getName(), TESTED_TABLE);
    }

    @Test
    public void getValidKeyTest() throws Exception {
        dataTable.put("one", "ONE");
        Assert.assertEquals(dataTable.get("one"), "ONE");
        Assert.assertNull(dataTable.get("notExistingValue"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullKeyShouldFail() throws Exception {
        dataTable.get(null);
    }

    @Test
    public void putValidValueTest() {
        Assert.assertNull(dataTable.put("two", "TWO"));
        Assert.assertEquals(dataTable.put("two", "THREE"), "TWO");
    }


    @Test(expected = IllegalArgumentException.class)
    public void putNullValueShouldFail() throws Exception {
        dataTable.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKeyShouldFail() throws IOException {
        dataTable.put(null, "value");
    }

    @Test
    public void removeValidKeyTest() {
        Assert.assertNull(dataTable.remove("three"));
        dataTable.put("four", "FOUR");
        Assert.assertEquals(dataTable.remove("four"), "FOUR");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKeyShouldFail() {
        dataTable.remove(null);
    }

    @Test
    public void sizeTest() {
        Assert.assertEquals(dataTable.size(), 0);
        dataTable.put("key", "value");
        dataTable.put("moo", "foo");
        dataTable.remove("moo");
        dataTable.put("story", "detective");
        Assert.assertEquals(dataTable.size(), 2);
    }

    @Test
    public void commitTest() {
        dataTable.put("1", "2");
        dataTable.put("3", "4");
        dataTable.put("5", "6");
        dataTable.remove("3");
        Assert.assertEquals(dataTable.commit(), 2);
    }

    @Test
    public void rollbackTest() {
        dataTable.put("7", "8");
        dataTable.put("9", "10");
        dataTable.remove("9");
        Assert.assertEquals(dataTable.rollback(), 1);
    }

}
