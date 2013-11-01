
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.valentinbarishev.filemap.MyTableProviderFactory;

import java.io.File;

public class MyTableProviderFactoryTest {
    public TemporaryFolder folder = new TemporaryFolder();

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() {
        TableProviderFactory factory = new MyTableProviderFactory();
        factory.create(null);
    }

    @Test
    public void testCreateNotNull() {
        TableProviderFactory factory = new MyTableProviderFactory();
        Assert.assertNotNull(factory.create(folder.toString()));
        new File(folder.toString()).delete();
    }
}