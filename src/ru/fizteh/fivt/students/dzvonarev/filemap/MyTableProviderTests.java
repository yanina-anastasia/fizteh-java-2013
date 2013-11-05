package ru.fizteh.fivt.students.dzvonarev.filemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.IOException;

public class MyTableProviderTests {

    private TableProvider provider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void test() throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(folder.newFolder().getCanonicalPath());
    }

    @Test
    public void testSameInstanceGetCreate() {
        Assert.assertEquals(provider.createTable("instance"), provider.getTable("instance"));
        Assert.assertEquals(provider.getTable("instance"), provider.getTable("instance"));
        provider.removeTable("instance");
    }

}
