package ru.fizteh.fivt.students.eltyshev.multifilemap.tests;

import org.junit.Before;
import org.junit.Assert;
import org.junit.*;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.eltyshev.multifilemap.DatabaseFactory;

public class DatabaseTableProviderTest {
    TableProviderFactory factory;
    TableProvider provider;

    @Before
    public void beforeTest() {
        factory = new DatabaseFactory();
        provider = factory.create("C:\\temp\\database_test");
    }

    @Test
    public void testGetTable() throws Exception {
        // non-existing tables
        Assert.assertNull(provider.getTable("non-existing_table"));
        Assert.assertNull(provider.getTable("there_is_no_such_table"));
        // existing tables
        Assert.assertNotNull(provider.getTable("table1"));
        Assert.assertNotNull(provider.getTable("table2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableExceptions() {
        provider.getTable(null);
    }

    @Test
    public void testCreateTable() throws Exception {
        // non-existing tables
        Assert.assertNotNull(provider.createTable("new_table1"));
        Assert.assertNotNull(provider.createTable("new_table2"));
        // existing tables
        Assert.assertNull(provider.createTable("table1"));
        Assert.assertNull(provider.createTable("table2"));

        // clean-up
        provider.removeTable("new_table1");
        provider.removeTable("new_table2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableExceptions() {
        provider.createTable(null);
    }

    @Test
    public void testRemoveTable() throws Exception {
        //prepare
        provider.createTable("new_table1");
        provider.createTable("new_table2");

        // existing tables
        provider.removeTable("new_table1");
        provider.removeTable("new_table2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableIllegalArgumentException() {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableIllegalStateException() {
        provider.removeTable("non-existing-table");
        provider.removeTable("no-such-table");
    }
}
