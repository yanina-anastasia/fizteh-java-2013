package ru.fizteh.fivt.students.vorotilov.db;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class TableProviderTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testCreateAndRemoveTable() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        Assert.assertNotNull(tableProvider.createTable("newTable"));
        Assert.assertNull(tableProvider.createTable("newTable"));
        tableProvider.removeTable("newTable");
    }

    @Test
    public void testGetTable() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        VorotilovTable table = tableProvider.createTable("newTable");
        Assert.assertNotNull(tableProvider.getTable("newTable"));
        Assert.assertEquals(table, tableProvider.getTable("newTable"));
        tableProvider.removeTable("newTable");
    }

    @Test
    public void testGetNotExistedTable() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        Assert.assertNull(tableProvider.getTable("notExistedTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithNotValidName() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithNotValidName() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithEmptyName() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.createTable(" ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableWithEmptyName() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.removeTable("  ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithEmptyName() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableWithNotValidName() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistedTable() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.removeTable("notExistedTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadSymbolsInTableName() throws IOException {
        VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
        VorotilovTableProvider tableProvider = tableProviderFactory.create(folder.newFolder().toString());
        Assert.assertNotNull(tableProvider);
        tableProvider.createTable("//a");
    }

}
