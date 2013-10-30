
import org.junit.*;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.valentinbarishev.filemap.MyTableProviderFactory;

public class MyTableProviderTest {
    static TableProviderFactory factory;
    static TableProvider provider;

    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();
    }

    @Before
    public void before() {
        provider = factory.create("//home/bajiuk/database");
        Assert.assertNotNull(provider);
    }

    @Test
    public void testCreateRemoveTable() {
        Assert.assertNotNull(provider.createTable("test_create_table"));
        Assert.assertNull(provider.createTable("test_create_table"));
        provider.removeTable("test_create_table");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNonExistingTable() {
        provider.removeTable("non_existing_table");
    }

    @Test
    public void testGetTable() {
        provider.createTable("test_get_table");
        Assert.assertNotNull(provider.getTable("test_get_table"));
        provider.removeTable("test_get_table");
    }

    @Test
    public void testGetNonExistingTable() {
        Assert.assertNull(provider.getTable("non_existing_table"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableWithNull(){
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableWithNull() {
        provider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableWithNull(){
        provider.removeTable(null);
    }

}

