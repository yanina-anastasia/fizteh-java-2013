package ru.fizteh.fivt.students.valentinbarishev.filemap.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.valentinbarishev.filemap.MyTableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MyTableProviderTest {
    static TableProviderFactory factory;
    static TableProvider provider;
    static String path;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();
    }

    @Before
    public void before() throws IOException{
        path = folder.newFolder().getCanonicalPath();
        provider = factory.create(path);
        Assert.assertNotNull(provider);
    }

    @Test(expected = Error.class)
    public void testSignature() {
        new File(path, "test").mkdirs();
        provider.getTable("test");
    }
}
