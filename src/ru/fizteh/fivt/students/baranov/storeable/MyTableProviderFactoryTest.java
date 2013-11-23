package ru.fizteh.fivt.students.baranov.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import java.io.File;
import java.io.IOException;

public class MyTableProviderFactoryTest {
    String path;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void beforeTest() {
        path = folder.getRoot().getPath();
    }

    @Test
    public void testCreationOfFactory() throws IOException {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        Assert.assertNotNull(factory.create(new File(path).getCanonicalPath()));
    }
}
