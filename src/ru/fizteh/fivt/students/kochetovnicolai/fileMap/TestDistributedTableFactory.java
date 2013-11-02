package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.kochetovnicolai.shell.FileManager;

import java.io.File;
import java.io.IOException;

public class TestDistributedTableFactory extends FileManager {

    protected DistributedTableProviderFactory factory;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void createWorkingDirectoryAndFactory() {
        factory = new DistributedTableProviderFactory();
    }

    @After
    public void removeWorkingDirectoryAndFactory() {
        factory = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void createProviderEmptyShouldFail() {
        factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createProviderOnFileShouldFail() throws IOException {
        String name = "file";
        File file = folder.newFile(name);
        factory.create(file.getName());
    }

    @Test
    public void createProvider() {
        Assert.assertTrue("failed create provider", factory.create(folder.getRoot().getName()) != null);
    }
}
