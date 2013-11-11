package ru.fizteh.fivt.students.kamilTalipov.database.test;


import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.DatabaseException;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTable;
import ru.fizteh.fivt.students.kamilTalipov.database.core.MultiFileHashTableProvider;
import ru.fizteh.fivt.students.kamilTalipov.database.core.TableRow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public void putGetCommitTest() throws IOException {
        Storeable storeable = new TableRow(table, Arrays.asList(1, "hello"));
        table.put("123", storeable);
        Assert.assertEquals(table.get("123").toString(), storeable.toString());
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.commit(), 1);
    }

    @Test(expected = ColumnFormatException.class)
    public void incorrectTypesPutTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(1, 2));
        table.put("123", storeable);
    }

    @Test
    public void removeTest() {
        Assert.assertEquals(table.remove("fff"), null);

        Storeable storeable = new TableRow(table, Arrays.asList(42, "don't panic"));
        table.put("answer", storeable);
        Assert.assertEquals(table.remove("answer").toString(), storeable.toString());
        Assert.assertEquals(table.rollback(), 0);
    }

    @Test
    public void rollbackTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(1, "hello"));
        Assert.assertEquals(table.put("fits", storeable), null);
        Assert.assertEquals(table.rollback(), 1);
        Assert.assertEquals(table.get("fits"), null);
    }

    @Test
    public void overwriteTest() {
        Storeable storeable = new TableRow(table, Arrays.asList(1, "hello"));
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

    @Test
    public void commitRollbackTest() throws IOException {
        Assert.assertEquals(table.commit(), 0);
        Storeable storeable = new TableRow(table, Arrays.asList(1, "hello"));
        Assert.assertEquals(table.put("fits", storeable), null);
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.put("fits", storeable).toString(), storeable.toString());
        Assert.assertEquals(table.commit(), 0);
        table.put("fits", new TableRow(table, Arrays.asList(2, "hfello")));
        table.remove("fits");
        Assert.assertEquals(table.rollback(), 1);
    }

    @Test
    public void commitRollbackTest2() throws IOException {
        Assert.assertEquals(table.commit(), 0);
        Assert.assertEquals(table.size(), 0);
        Storeable storeable = new TableRow(table, Arrays.asList(222, "gell"));
        Storeable storeable2 = new TableRow(table, Arrays.asList(202, "ffll"));
        Assert.assertEquals(table.put("fsss", storeable), null);
        Assert.assertEquals(table.put("fsss", storeable2), storeable);
        Assert.assertEquals(table.remove("fsss"), storeable2);
        Assert.assertEquals(table.rollback(), 0);
    }

    @Test
    public void commitRollbackTest3() {
        Assert.assertEquals(table.size(), 0);
        table.remove("test");
        Assert.assertEquals(table.rollback(), 0);
    }
}
