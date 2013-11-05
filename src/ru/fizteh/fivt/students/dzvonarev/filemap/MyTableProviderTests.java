package ru.fizteh.fivt.students.dzvonarev.filemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;

public class MyTableProviderTests {

    private TableProvider provider;

    @Before
    public void test() {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        File file = new File(".");
        String path = file.getAbsolutePath();
        provider = factory.create(path);
    }

    @Test
    public void testSameInstanceGetCreate() {
        Assert.assertEquals(provider.createTable("instance"), provider.getTable("instance"));
        Assert.assertEquals(provider.getTable("instance"), provider.getTable("instance"));
        provider.removeTable("instance");
    }

}
