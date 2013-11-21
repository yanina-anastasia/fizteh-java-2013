package ru.fizteh.fivt.students.dzvonarev.filemap;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.IOException;

public class MyTableTests {

    private Table table;
    private TableProvider provider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void test() throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder().getCanonicalPath());
        table = provider.createTable("testTable");
    }

    @Test
    public void testCommitRollback() {
        Assert.assertNull(table.put("commit", "rollback"));
        Assert.assertEquals(table.get("commit"), "rollback");
        Assert.assertEquals(table.rollback(), 1);
        Assert.assertNull(table.get("commit"));
        Assert.assertNull(table.put("commit", "rollback"));
        Assert.assertEquals(table.get("commit"), "rollback");
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.remove("commit"), "rollback");
        Assert.assertNull(table.put("commit", "rollback1"));
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.get("commit"), "rollback1");
    }

    @Test
    public void testCommitWithNoChanges() {
        Assert.assertNull(table.put("no_changes", "will_be_deleted_soon"));
        Assert.assertEquals(table.remove("no_changes"), "will_be_deleted_soon");
        Assert.assertEquals(table.commit(), 0);
        Assert.assertNull(table.put("key", "value"));
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.put("key", "value_new"), "value");
        Assert.assertEquals(table.put("key", "value"), "value_new");
        Assert.assertEquals(table.commit(), 0);
    }

    @Test
    public void checkSize() {
        table.put("key", "value");
        table.remove("key");
        table.put("newKey", "value");
        Assert.assertEquals("Incorrect size", 1, table.size());
    }

    @Test
    public void testGet() {
        table.put("Dmitry", "value");
        table.put("Kolya", "value");
        table.remove("Dmitry");
        Assert.assertNotNull("expected value", table.get("Kolya"));
    }

    @Test
    public void testGetAfterRemove() {
        table.put("Pasha", "value");
        table.remove("Pasha");
        Assert.assertNull("expected null when get removed value", table.get("Pasha"));
    }

}
