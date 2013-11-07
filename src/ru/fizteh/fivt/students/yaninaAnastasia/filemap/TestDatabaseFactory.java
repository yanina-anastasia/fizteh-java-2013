package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class TestDatabaseFactory {
    String path;

    @Before
    public void beforeTest() {
        path = "C:\\temp\\database_factory_test";
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGiveNull() {
        TableProviderFactory factory = new DatabaseTableProviderFactory();
        factory.create(null);
    }

    @Test
    public void testCreateNormalFactory() throws IOException {
        TableProviderFactory factory = new DatabaseTableProviderFactory();
        Assert.assertNotNull(factory.create(new File(path).getCanonicalPath()));
    }
}
