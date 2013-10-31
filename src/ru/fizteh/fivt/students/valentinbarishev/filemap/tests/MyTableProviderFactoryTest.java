
import org.junit.*;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.valentinbarishev.filemap.MyTableProviderFactory;

public class MyTableProviderFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() {
        TableProviderFactory factory = new MyTableProviderFactory();
        factory.create(null);
    }

    @Test
    public void testCreateNotNull() {
        TableProviderFactory factory = new MyTableProviderFactory();
        Assert.assertNotNull(factory.create("//home/bajiuk/database"));
    }
}