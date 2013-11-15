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
        cl.add(String.class);
        cl.add(Double.class);
        table = provider.createTable("testTable", cl);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullValue() {
        table.put("key", null);
    }

    @Test
    public void testIsCorrect() throws IOException {
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(String.class);
        cl.add(Double.class);
        Table t = provider.createTable("1ff1", cl);
        List<Object> data = new ArrayList<>();
        data.add(2);
        data.add("fq");
        data.add(3.3);
        Storeable st = provider.createFor(t, data);
        table.put("blabla", st);
    }

    @Test(expected = ColumnFormatException.class)
    public void testLessArguments() throws IOException {
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(String.class);
        Table t = provider.createTable("less", cl);
        List<Object> x = new ArrayList<>();
        x.add(24);
        x.add("lbaba");
        Storeable st = provider.createFor(t, x);
        table.put("asd", st);
    }

    @Test(expected = ColumnFormatException.class)
    public void testMoreArguments() throws IOException {
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(String.class);
        cl.add(Double.class);
        cl.add(Integer.class);
        Table t = provider.createTable("more", cl);
        List<Object> x = new ArrayList<>();
        x.add(1);
        x.add("2");
        x.add(3.4);
        x.add(1);
        Storeable st = provider.createFor(t, x);
        table.put("asd", st);
    }


}
