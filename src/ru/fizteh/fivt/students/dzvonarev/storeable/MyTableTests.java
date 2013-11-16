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
        Table t = provider.createTable("correct", cl);
        List<Object> data = new ArrayList<>();
        data.add(2);
        data.add("qwerty");
        data.add(3.3);
        Storeable st = provider.createFor(t, data);
        table.put("blabla", st);
    }

    @Test(expected = ColumnFormatException.class)
    public void testLessArguments() throws IOException {
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(String.class);
        Table newTable = provider.createTable("less", cl);
        List<Object> args = new ArrayList<>();
        args.add(24);
        args.add("less");
        Storeable st = provider.createFor(newTable, args);
        table.put("less", st);
    }

    @Test(expected = ColumnFormatException.class)
    public void testMoreArguments() throws IOException {
        List<Class<?>> cl = new ArrayList<>();
        cl.add(Integer.class);
        cl.add(String.class);
        cl.add(Double.class);
        cl.add(Integer.class);
        Table newTable = provider.createTable("more", cl);
        List<Object> args = new ArrayList<>();
        args.add(1);
        args.add("2");
        args.add(3.4);
        args.add(1);
        Storeable st = provider.createFor(newTable, args);
        table.put("more", st);
    }

    @Test(expected = ColumnFormatException.class)
    public void wrongTypes() {
        List<Object> args = new ArrayList<>();
        args.add(1);
        args.add(2);
        args.add(3);
        Storeable st = provider.createFor(table, args);
        table.put("wrongTypes", st);
    }

    @Test
    public void ifHaveNulls() {
        List<Object> args = new ArrayList<>();
        args.add(null);
        args.add("null");
        args.add(null);
        Storeable st = provider.createFor(table, args);
        table.put("hasnull", st);
    }

}
