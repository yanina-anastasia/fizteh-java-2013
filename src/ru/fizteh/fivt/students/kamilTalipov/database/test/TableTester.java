package ru.fizteh.fivt.students.kamilTalipov.database.test;


import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.DatabaseException;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;
import ru.fizteh.fivt.students.kamilTalipov.database.core.TableRow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableTester {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public MultiFileHashTableProvider provider;
    public MultiFileHashTable table;

    @Before
    public void tableInit() throws IOException, DatabaseException {
        provider = new MultiFileHashTableProvider(folder.getRoot().getAbsolutePath());

        List<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        table = provider.createTable("Test", types);
    }

    @Test
    public void getNameTest() {
        Assert.assertEquals(table.getName(), "Test");
    }

    @Test
    public void putGetCommitTest() {
        ArrayList<Object> values = new ArrayList<>();
        values.add(1);
        values.add("hello");
        Storeable storeable = new TableRow(table, values);
        table.put("123", storeable);
        Assert.assertEquals(table.get("123").toString(), storeable.toString());
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.commit(), 1);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypesPutTest() {
        ArrayList<Object> values = new ArrayList<>();
        values.add(1);
        values.add(2);
        Storeable storeable = new TableRow(table, values);
        table.put("123", storeable);
    }

    @Test
    public void removeTest() {
        Assert.assertEquals(table.remove("fff"), null);

        ArrayList<Object> values = new ArrayList<>();
        values.add(42);
        values.add("don't panic");
        Storeable storeable = new TableRow(table, values);
        table.put("answer", storeable);
        Assert.assertEquals(table.remove("answer").toString(), storeable.toString());
        Assert.assertEquals(table.rollback(), 1);
    }

    @Test
    public void rollbackTest() {
        ArrayList<Object> values = new ArrayList<>();
        values.add(1);
        values.add("hello");
        Storeable storeable = new TableRow(table, values);
        Assert.assertEquals(table.put("fits", storeable), null);
        Assert.assertEquals(table.rollback(), 1);
        Assert.assertEquals(table.get("fits"), null);
    }

    @Test
    public void overwriteTest() {
        ArrayList<Object> values = new ArrayList<>();
        values.add(1);
        values.add("hello");
        Storeable storeable = new TableRow(table, values);
        table.put("123", storeable);
        Assert.assertEquals(table.put("123", storeable).toString(), storeable.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullTest() {
        table.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTest() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTest() {
        table.remove(null);
    }
}
