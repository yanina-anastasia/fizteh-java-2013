package ru.fizteh.fivt.students.surakshina.filemap;



public class TableTest {
    /*private TableProviderFactory factory;
    private TableProvider provider;
    private Table table;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();*/

   /* @Before
    public void begin() throws IOException {
        factory = new NewTableProviderFactory();
        provider = factory.create(folder.newFolder().toString());
        table = provider.createTable("Tabel");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() {
        table.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() {
        table.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() {
        table.put("    ", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyValue() {
        table.put("key", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKey() {
        table.remove(null);
    }

    @Test
    public void testRemoveNotExistsKey() {
        assertNull(table.put("key", "value"));
        assertNotNull(table.remove("key"));
        assertNull(table.remove("key"));
    }

    @Test
    public void testGetNotExistsKey() {
        assertNull(table.put("key", "value"));
        assertNotNull(table.remove("key"));
        assertNull(table.get("key"));
    }

    @After
    public void deleteTable() {
        provider.removeTable(table.getName());
    }*/
}
