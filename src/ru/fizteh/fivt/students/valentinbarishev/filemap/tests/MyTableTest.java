package ru.fizteh.fivt.students.valentinbarishev.filemap.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.valentinbarishev.filemap.MyTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyTableTest {
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

    public void storeableEquals(Storeable a, Storeable b) {
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            Assert.assertEquals(a.getColumnAt(i),b.getColumnAt(i));
        }
    }

    @Test
    public void testGetPutSimple() throws IOException {
        Storeable storeable = provider.createFor(table);

        storeable.setColumnAt(0, "new_value");
        storeable.setColumnAt(1, 100);

        Storeable old = provider.createFor(table);
        old.setColumnAt(0, "new_value");
        old.setColumnAt(1, 100);

        Assert.assertNull(table.put("simple", storeable));
        storeableEquals(table.get("simple"), storeable);

        Assert.assertEquals(table.commit(), 1);
        storeable.setColumnAt(0, "very_new");
        storeable.setColumnAt(1, null);
        Assert.assertNull(table.put("null", storeable));
        storeableEquals(table.get("null"), storeable);

        storeableEquals(table.remove("null"), storeable);
        storeableEquals(table.put("simple", storeable), old);

        Assert.assertEquals(table.rollback(), 1);

        storeableEquals(table.remove("simple"), old);
        Assert.assertEquals(table.commit(), 1);

        Assert.assertNull(table.put("key", old));
        Assert.assertEquals(table.commit(), 1);

        storeableEquals(table.put("key", old), old);
        Assert.assertEquals(table.commit(), 0);
    }

}
