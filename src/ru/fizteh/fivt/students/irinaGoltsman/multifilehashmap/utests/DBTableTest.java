package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.utests;

import org.junit.*;

import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTable;

import java.io.File;
import java.io.IOException;

public class DBTableTest {
    private static Table table;
    @Rule
    public TemporaryFolder rootDBDirectory = new TemporaryFolder();

    @Before
    public void createTable() throws IOException {
        File newTable = rootDBDirectory.newFolder("testTable");
        table = new DBTable(newTable);
    }

    @Test
    public void getName() {
        Assert.assertEquals("testTable", table.getName());
    }

    @Test
    public void getNotExistingKey() {
        Assert.assertNull(table.get("notExistingKey"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullKey() {
        table.get(null);
    }

    @Test
    public void putNewKey() {
        Assert.assertNull(table.put("newTestKey", "newValue"));
        table.remove("newTestKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKey() {
        table.put(null, "value");
    }

    @Test
    public void removeNotExistingKey() {
        Assert.assertNull(table.remove("notExistingKeyTest"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKey() {
        table.remove(null);
    }

    @Test
    public void size() {
        Assert.assertEquals(0, table.size());
        table.put("1", "1");
        table.put("2", "2");
        table.put("3", "3");
        Assert.assertEquals(3, table.size());
        table.remove("1");
        Assert.assertEquals(2, table.size());
        table.remove("2");
        table.remove("3");
        Assert.assertEquals(0, table.size());
    }


}
