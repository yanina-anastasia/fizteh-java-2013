package ru.fizteh.fivt.students.dzvonarev.filemap;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;

public class MyTableTests {

    private Table table;

    @Test
    public void testCommitRollback() {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        MyTableProvider provider;
        provider = factory.create(System.getProperty("fizteh.db.dir"));
        table = provider.createTable("my");
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
        MyTableProviderFactory factory = new MyTableProviderFactory();
        MyTableProvider provider;
        provider = factory.create(System.getProperty("fizteh.db.dir"));
        table = provider.createTable("my1");
        Assert.assertNull(table.put("no_changes", "will_be_deleted_soon"));
        Assert.assertEquals(table.remove("no_changes"), "will_be_deleted_soon");
        Assert.assertEquals(table.commit(), 0);
        Assert.assertNull(table.put("key", "value"));
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.put("key", "value_new"), "value");
        Assert.assertEquals(table.put("key", "value"), "value_new");
        Assert.assertEquals(table.commit(), 0);
    }

}
