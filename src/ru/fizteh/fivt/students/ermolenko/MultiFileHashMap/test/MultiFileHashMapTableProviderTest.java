package ru.fizteh.fivt.students.ermolenko.multifilehashmap.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.ermolenko.multifilehashmap.MultiFileHashMapTableProvider;
import ru.fizteh.fivt.students.ermolenko.multifilehashmap.MultiFileHashMapTableProviderFactory;

public class MultiFileHashMapTableProviderTest {

    private MultiFileHashMapTableProvider tableProvider;

    @Before
    public void setUp() throws Exception {

        MultiFileHashMapTableProviderFactory tableProviderFactory = new MultiFileHashMapTableProviderFactory();
        tableProvider = tableProviderFactory.create("javatest");
        tableProvider.createTable("existingTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() throws Exception {

        tableProvider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableEmpty() throws Exception {

        tableProvider.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableIncorrect() throws Exception {

        tableProvider.getTable("$#*_%");
    }

    @Test
    public void testGetTableExisted() throws Exception {

        Assert.assertEquals(tableProvider.getTable("existingTable").getName(), "existingTable");
    }

    @Test
    public void testGetTableNotExisted() throws Exception {

        Assert.assertNull(tableProvider.getTable("notExistingTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() throws Exception {

        tableProvider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableEmpty() throws Exception {

        tableProvider.createTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableIncorrect() throws Exception {

        tableProvider.createTable("$#*_%");
    }

    @Test
    public void testCreateTableExisted() throws Exception {

        Assert.assertNull(tableProvider.createTable("existingTable"));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableNotExisted() throws Exception {

        tableProvider.removeTable("notExistingTable");
    }

    @Test
    public void testRemoveTableExisted() throws Exception {

        tableProvider.removeTable("existingTable");
    }

    @Test
    public void testCreateTableNotExisted() throws Exception {

        Assert.assertNotNull(tableProvider.createTable("notExistingTable"));
        tableProvider.removeTable("notExistingTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() throws Exception {

        tableProvider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableEmpty() throws Exception {

        tableProvider.removeTable("");
    }
}
