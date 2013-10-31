package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import org.junit.*;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.FileManager;

import java.io.File;

@RunWith(Theories.class)
public class TestDistributedTableProvider extends FileManager {
    protected DistributedTableProviderFactory factory = new DistributedTableProviderFactory();
    protected DistributedTableProvider provider;
    protected File workingDirectory = new File("./TestDistributedTableFactory");

    @Before
    public void createWorkingDirectoryAndProvider() {
        Assert.assertTrue(workingDirectory.mkdir());
        provider = factory.create(workingDirectory.getPath());
    }

    @After
    public void removeWorkingDirectoryAndProvider() {
        if (workingDirectory.exists()) {
            recursiveRemove(workingDirectory, "TestDistributedTableProvider");
        }
        provider = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableEmptyShouldFail() {
       provider.removeTable(null);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @DataPoints
    public static String[] argumentsWithBadSymbols = new String [] {
            "",
            ".",
            "..",
            "....",
            "...dir",
            "\\",
            "dir/17.dir",
    };

    @Theory
    public void removeTableBadSymbolShouldFail(String name) {
        thrown.expect(IllegalArgumentException.class);
        provider.removeTable(name);
    }

    @Theory
    public void createTableBadSymbolShouldFail(String name) {
        thrown.expect(IllegalArgumentException.class);
        provider.createTable(name);
    }

    @Theory
    public void getTableBadSymbolShouldFail(String name) {
        thrown.expect(IllegalArgumentException.class);
        provider.getTable(name);
    }

    @Test
    public void getTableShouldGetNullIfTableDoesNotExists() {
        Assert.assertEquals("getTable should return null", provider.getTable("abcd"), null);
    }

    @Test
    public void createTableShouldBeOK() {
        Table table = provider.createTable("abcd");
        Assert.assertEquals("table shouldn't be null", table != null);
    }
}
