package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.utests;

import org.junit.*;

import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBStoreable;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DBTableTest {
    private static Table table;
    private static TableProvider tableProvider;
    private List<Class<?>> columnTypes = new ArrayList<>();
    @Rule
    public TemporaryFolder rootDBDirectory = new TemporaryFolder();

    @Before
    public void createTable() throws IOException, ParseException {
        columnTypes.add(Integer.class);
        tableProvider = new DBTableProvider(rootDBDirectory.newFolder());
        table = tableProvider.createTable("testTable", columnTypes);
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
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Storeable row = new DBStoreable(types);
        Assert.assertNull(table.put("newTestKey", row));
        table.remove("newTestKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKey() {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Storeable row = new DBStoreable(types);
        table.put(null, row);
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
    public void sizeWork() {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Storeable row = new DBStoreable(types);
        Assert.assertEquals(0, table.size());
        table.put("1", row);
        table.put("2", row);
        table.put("3", row);
        Assert.assertEquals(3, table.size());
        table.remove("1");
        Assert.assertEquals(2, table.size());
        table.remove("2");
        table.remove("3");
        Assert.assertEquals(0, table.size());
    }

    @Test
    public void commitWork() throws IOException {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Storeable row = new DBStoreable(types);
        table.put("1", row);
        table.put("2", row);
        table.put("3", row);
        Assert.assertEquals(3, table.commit());
        table.remove("1");
        table.remove("2");
        table.remove("3");
        Assert.assertEquals(3, table.commit());
    }

    @Test
    public void rollBackWork() {
        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        Storeable row = new DBStoreable(types);
        table.put("new1", row);
        table.put("new2", row);
        table.put("new3", row);
        Assert.assertEquals(3, table.rollback());
        Assert.assertEquals(0, table.rollback());
        Storeable oldValue = table.put("new1", row);
        Assert.assertNull(oldValue);
    }
}
