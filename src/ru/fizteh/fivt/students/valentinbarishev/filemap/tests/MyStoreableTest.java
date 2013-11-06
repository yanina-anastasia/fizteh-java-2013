package ru.fizteh.fivt.students.valentinbarishev.filemap.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.valentinbarishev.filemap.MyTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyStoreableTest {
    static Table table;
    static TableProviderFactory factory;
    static TableProvider provider;
    static List<Class<?>> types;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();
    }

    @Before
    public void beforeTest() throws IOException {
        provider = factory.create(folder.newFolder("folder").getCanonicalPath());
        types = new ArrayList<>();
        types.add(String.class);
        types.add(Integer.class);

        table = provider.createTable("simple", types);

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testBoundsException() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(2, "12");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testBoundsExceptionMore() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(-2, "44");
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatException() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(1, true);
    }

    @Test(expected = ColumnFormatException.class)
    public void testColumnFormatExceptionMore() {
        Storeable storeable = provider.createFor(table);
        storeable.setColumnAt(0, "asdasd");
        storeable.getIntAt(0);
    }
}
