package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.utests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTableProvider;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DBTableProviderTest {
    private static TableProvider provider;
    @Rule
    public TemporaryFolder rootDBDirectory = new TemporaryFolder();

    @Before
    public void createTableProvider() throws IOException, ParseException {
        provider = new DBTableProvider(rootDBDirectory.newFolder());
    }

    /*
    @AfterClass
    public static void deleteDataBase() throws IOException {
        MapOfCommands cm = new MapOfCommands();
        cm.addCommand(new ShellCommands.Remove());
        cm.addCommand(new ShellCommands.ChangeDirectory());
        cm.commandProcessing("cd " + rootDBDirectory.toString());
        cm.commandProcessing("cd .");
        cm.commandProcessing("rm " + rootDBDirectory.toString());
    }
    */

    //-------Tests for getTable
    @Test(expected = IllegalArgumentException.class)
    public void getTableNullTableName() {
        provider.getTable(null);
    }

    @Test
    public void getTableNotExistingTable() {
        Assert.assertNull(provider.getTable("notExistingTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTableErrorTableName() {
        provider.getTable("\\htke4*&&/..");
    }

    @Test
    public void getTableRecallShouldReturnTheSameTable() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        provider.createTable("tmp", columnTypes);
        Assert.assertEquals(provider.getTable("tmp"), provider.getTable("tmp"));
        provider.removeTable("tmp");
    }

    //------Tests for createTable
    @Test
    public void createTableForExistingTableReturnsNull() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        provider.createTable("tmp", columnTypes);
        Assert.assertNull(provider.createTable("tmp", columnTypes));
        provider.removeTable("tmp");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableErrorTableName() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        provider.createTable("//\0", columnTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableNullTableName() throws IOException {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        provider.createTable(null, columnTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableNullColumnTypes() throws IOException {
        provider.createTable("tmp", null);
    }

    //-------Tests for removeTable
    @Test(expected = IllegalArgumentException.class)
    public void removeTableNullNameTable() throws IOException {
        provider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableErrorTableName() throws IOException {
        provider.removeTable("//\0");
    }

    @Test(expected = IllegalStateException.class)
    public void removeTableNotExistingTable() throws IOException {
        provider.removeTable("newNotExistingTable");
    }
}
