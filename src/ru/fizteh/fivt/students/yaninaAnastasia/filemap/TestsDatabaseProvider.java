package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestsDatabaseProvider {
    DatabaseTableProviderFactory factory;
    DatabaseTableProvider provider;

    @Before
    public void beforeTest() {
        factory = new DatabaseTableProviderFactory();
        provider = factory.create("C:\\temp\\database_test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableExceptions() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableExceptions() {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
      public void testRemoveTableIllegalArgumentException() {
        provider.removeTable(null);
    }

    @Test(expected = RuntimeException.class)
    public void testBadSymbolsCreateTable() {
        provider.createTable("table:newTable");
    }

    @Test(expected = RuntimeException.class)
    public void testBadSymbolsRemoveTable() {
        provider.createTable("C:\\temp");
    }

    @Test(expected = RuntimeException.class)
    public void testBadSymbolsGetTable() {
        provider.createTable("table?");
    }

    @Test(expected = RuntimeException.class)
    public void anotherTestBadSymbols() {
        provider.createTable("table\\..");
    }

    @Test(expected = RuntimeException.class)
    public void oneMoreTestBadSymbols() {
        provider.createTable("bigTable>smallTable");
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
        provider.createTable("");
    }

    @Test
    public void testCreateRmTable() {
        Assert.assertNotNull(provider.createTable("test1"));
        Assert.assertNull(provider.createTable("test1"));
        provider.removeTable("test1");
        Assert.assertNotNull(provider.createTable("test1"));
    }

    @Test
    public void testCreateGetTable() {
        Assert.assertNotNull(provider.createTable("test2"));
        Assert.assertNotNull(provider.getTable("test2"));
    }

    @Test
    public void testCreateRemoveGet() {
        Assert.assertNotNull(provider.createTable("test3"));
        provider.removeTable("test3");
        Assert.assertNull(provider.getTable("test3"));
    }

    @Test
    public void testGetTableNotExists() {
        Assert.assertNull(provider.getTable("test4"));
        Assert.assertNotNull(provider.createTable("test4"));
        Assert.assertNotNull(provider.getTable("test4"));
        provider.removeTable("test4");
    }
}