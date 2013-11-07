package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestsDatabaseProvider {
    DatabaseTableProviderFactory factory;
    DatabaseTableProvider provider;
    List<Class<?>> columnTypes = new ArrayList<>();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void beforeTest() {
        columnTypes.add(Integer.class);
        factory = new DatabaseTableProviderFactory();
        try {
            provider = factory.create(folder.getRoot().getPath());
        } catch (IOException e) {
            //
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableExceptions() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableExceptions() {
        provider.createTable(null, columnTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableIllegalArgumentException() {
        provider.removeTable(null);
    }

    @Test(expected = RuntimeException.class)
    public void testBadSymbolsCreateTable() {
        provider.createTable("table:newTable", columnTypes);
    }

    @Test(expected = RuntimeException.class)
    public void testBadSymbolsRemoveTable() {
        provider.createTable("C:\\temp", columnTypes);
    }

    @Test(expected = RuntimeException.class)
    public void testBadSymbolsGetTable() {
        provider.createTable("table?", columnTypes);
    }

    @Test(expected = RuntimeException.class)
    public void anotherTestBadSymbols() {
        provider.createTable("table\\..", columnTypes);
    }

    @Test(expected = RuntimeException.class)
    public void oneMoreTestBadSymbols() {
        provider.createTable("bigTable>smallTable", columnTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNoName() {
        provider.removeTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNoName() {
        provider.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNoName() {
        provider.createTable("", columnTypes);
    }

    @Test
    public void testCreateRmTable() {
        provider.createTable("test1", columnTypes);
        Assert.assertNull(provider.createTable("test1", columnTypes));
        provider.removeTable("test1");
        Assert.assertNotNull(provider.createTable("test1", columnTypes));
    }

    @Test
    public void testCreateGetTable() {
        provider.createTable("test2", columnTypes);
        Assert.assertNotNull(provider.getTable("test2"));
    }

    @Test
    public void testCreateRemoveGet() {
        Assert.assertNotNull(provider.createTable("test3", columnTypes));
        provider.removeTable("test3");
        Assert.assertNull(provider.getTable("test3"));
    }

    @Test
    public void testGetTableNotExists() {
        Assert.assertNull(provider.getTable("test4"));
        Assert.assertNotNull(provider.createTable("test4", columnTypes));
        Assert.assertNotNull(provider.getTable("test4"));
        provider.removeTable("test4");
    }
}
