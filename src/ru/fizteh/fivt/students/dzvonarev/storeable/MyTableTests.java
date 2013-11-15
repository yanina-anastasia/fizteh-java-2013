package ru.fizteh.fivt.students.dzvonarev.storeable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyTableTests {

    private Table table;
    private TableProvider provider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void test() throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder().getCanonicalPath());
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(Integer.class);
        cl.add(Integer.class);
        table = provider.createTable("testTable", cl);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullValue() {
        table.put("key", null);
    }

    @Test(expected = ColumnFormatException.class)
    public void testAlienStoreavble() throws IOException {
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(Boolean.class);
        Table table2 = provider.createTable("asd", cl);
        Storeable x = provider.createFor(table2);
        table.put("asd", x);
    }

    @Test
    public void testIsValid() {
        List<Object> data = new ArrayList<>();
        Integer num =  new Integer(2);
        data.add(num);
        data.add(24);
        data.add();
        Storeable st = provider.createFor(table, data);
        table.put("blabla", st);
    }


}
