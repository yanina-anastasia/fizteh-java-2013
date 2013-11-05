package ru.fizteh.fivt.students.dzvonarev.filemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class MyTableProviderTests {

    private TableProvider provider;

    @Before
    public void test() {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(System.getProperty("fizteh.db.dir"));
    }

    @Test
    public void testSameInstanceGetCreate() {
        Assert.assertEquals(provider.createTable("instance"), provider.getTable("instance"));
        Assert.assertEquals(provider.getTable("instance"), provider.getTable("instance"));
        provider.removeTable("instance");
    }

}
