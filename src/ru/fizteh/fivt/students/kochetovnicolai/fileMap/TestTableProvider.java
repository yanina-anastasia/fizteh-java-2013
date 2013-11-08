package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import org.junit.*;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.ArrayList;

@RunWith(Theories.class)
public class TestTableProvider {
    protected DistributedTableProviderFactory factory;
    protected TableProvider provider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void createWorkingDirectoryAndProvider() throws IOException {
        factory = new DistributedTableProviderFactory();
        provider = factory.create(folder.getRoot().getPath());
    }

    @After
    public void removeWorkingDirectoryAndProvider() {
        provider = null;
        factory = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableEmptyShouldFail() throws IOException {
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
    public void removeTableBadSymbolShouldFail(String name) throws IOException {
        thrown.expect(IllegalArgumentException.class);
        provider.removeTable(name);
    }

    @Test
    public void removeNotExistingTableShouldFail() throws IOException {
        thrown.expect(IllegalStateException.class);
        provider.removeTable("test");
    }

    @Theory
    public void createTableBadSymbolShouldFail(String name) throws IOException {
        thrown.expect(IllegalArgumentException.class);
        ArrayList<Class<?>> type = new ArrayList<>();
        type.add(String.class);
        provider.createTable(name, type);
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
    public void createTableShouldBeOK() throws IOException {
        ArrayList<Class<?>> type = new ArrayList<>();
        type.add(String.class);
        Table table = provider.createTable("abcd", type);
        Assert.assertTrue("table shouldn't be null", table != null);
        Table table2 = provider.createTable("abcd", type);
        /***/
        Assert.assertEquals("createTable should return null on the same names", null, table2);
        //
        table2 = provider.getTable("abcd");
        /***/
        Assert.assertEquals("getTable should return same objects on the same names", table, table2);
        //
        provider.removeTable("abcd");
        Assert.assertEquals("getTable should return null after remove", provider.getTable("abcd"), null);
        table = provider.createTable("abcd", type);
        Assert.assertTrue("createTable should return table after remove", table != null);
    }
}
