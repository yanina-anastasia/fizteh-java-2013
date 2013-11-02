package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.utests;

import org.junit.*;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTableProvider;

import ru.fizteh.fivt.students.irinaGoltsman.shell.MapOfCommands;
import ru.fizteh.fivt.students.irinaGoltsman.shell.ShellCommands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DBTableProviderTest {
    private static Path rootDBDirectory;
    private static TableProvider provider;

    @BeforeClass
    public static void createDataBase() throws IOException {
        rootDBDirectory = Files.createTempDirectory(Paths.get(System.getProperty("user.dir")), null);
    }

    @Before
    public void createTableProvider() throws IOException {
        provider = new DBTableProvider(rootDBDirectory.toFile());
    }

    @AfterClass
    public static void deleteDataBase() throws IOException {
        MapOfCommands cm = new MapOfCommands();
        cm.addCommand(new ShellCommands.Remove());
        cm.addCommand(new ShellCommands.ChangeDirectory());
        cm.commandProcessing("cd " + rootDBDirectory.toString());
        cm.commandProcessing("cd .");
        cm.commandProcessing("rm " + rootDBDirectory.toString());
    }

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
    public void getTableRecallShouldReturnTheSameTable() {
        provider.createTable("tmp");
        Assert.assertEquals(provider.getTable("tmp"), provider.getTable("tmp"));
        provider.removeTable("tmp");
    }
}
