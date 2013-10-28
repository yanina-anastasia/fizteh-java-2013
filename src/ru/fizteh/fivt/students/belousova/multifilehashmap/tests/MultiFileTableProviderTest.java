package ru.fizteh.fivt.students.belousova.multifilehashmap.tests;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.belousova.multifilehashmap.MultiFileTableProvider;

import java.io.File;

public class MultiFileTableProviderTest {
    private TableProvider tableProvider;
    private File testDirectory;
    @Before
    public void setUp() throws Exception {
        testDirectory = new File("javatest");
        if (testDirectory.exists()) {
            testDirectory.delete();
        }
        testDirectory.mkdir();
        tableProvider = new MultiFileTableProvider(testDirectory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() throws Exception {
        tableProvider.getTable(null);
    }

    @Test
    public void testGetTableExisted() throws Exception {
        Table table = tableProvider.createTable("newExistingTable");
        Assert.assertEquals(tableProvider.getTable("newExistingTable").getName(), "newExistingTable");
    }

    @Test
    public void testGetTableNotExisted() throws Exception {
        Assert.assertNull(tableProvider.getTable("newNotExistingTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() throws Exception {
        tableProvider.createTable(null);
    }

    @Test
    public void testCreateTableExisted() throws Exception {
        tableProvider.createTable("newExistingTable");
        Assert.assertNull(tableProvider.createTable("newExistingTable"));
    }

    @Test
    public void testCreateTableNotExisted() throws Exception {
        Assert.assertNotNull(tableProvider.createTable("newNotExistingTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() throws Exception {
        tableProvider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableNotExisted() throws Exception {
        tableProvider.removeTable("table");
    }

    @Test
    public void testRemoveTableExisted() throws Exception {
        tableProvider.createTable("table");
        tableProvider.removeTable("table");
    }
}
