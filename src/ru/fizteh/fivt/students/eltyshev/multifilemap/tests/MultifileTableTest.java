package ru.fizteh.fivt.students.eltyshev.multifilemap.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.eltyshev.multifilemap.DatabaseFactory;

public class MultifileTableTest {
    private static final int KEYS_COUNT = 20;
    private static final String TABLE_NAME = "test_table";

    TableProviderFactory factory = new DatabaseFactory();
    TableProvider provider = factory.create("C:\\temp\\database_test");
    Table currentTable;

    @Before
    public void setUp() throws Exception {
        currentTable = provider.createTable(TABLE_NAME);
        prepareData();
    }

    @After
    public void afterTest() throws Exception {
        provider.removeTable(TABLE_NAME);
    }

    @Test
    public void testTableExistingData() {
        // existing data
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String expectedValue = String.format("value%d", index);
            String key = String.format("key%d", index);
            Assert.assertEquals(expectedValue, currentTable.get(key));
        }
    }

    @Test
    public void testTableNonExistingData() {
        // non-existing data
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("k%d", (int) (Math.random() * 100));
            Assert.assertNull(currentTable.get(key));
        }
    }

    @Test
    public void testTableNewData() {
        // new data
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String newKey = String.format("new_key%d", index);
            String newValue = String.format("new_value%d", index);
            Assert.assertNull(currentTable.put(newKey, newValue));
        }
    }

    @Test
    public void testTableReplaceData() {
        // replacing
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String oldValue = String.format("value%d", index);
            String newValue = String.format("new_value%d", index);
            Assert.assertEquals(oldValue, currentTable.put(key, newValue));
        }
    }

    @Test
    public void testTableCommit() {
        Assert.assertEquals(KEYS_COUNT, currentTable.commit());

        for (int index = 0; index < 2 * KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            currentTable.put(key, value);
        }

        Assert.assertEquals(2 * KEYS_COUNT, currentTable.commit());
    }

    @Test
    public void testTableRollback() {
        Assert.assertEquals(KEYS_COUNT, currentTable.rollback());

        for (int index = 0; index < 2 * KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            currentTable.put(key, value);
        }

        Assert.assertEquals(2 * KEYS_COUNT, currentTable.rollback());
    }

    @Test
    public void testTableSize() {
        Assert.assertEquals(KEYS_COUNT, currentTable.size());
    }

    @Test
    public void testTableGetName() {
        Assert.assertEquals(TABLE_NAME, currentTable.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableExceptions() {
        // get
        currentTable.get(null);

        // put
        currentTable.put(null, "value");
        currentTable.put("key", null);

        // remove
        currentTable.remove(null);
    }

    private void prepareData() {
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            String value = String.format("value%d", index);
            currentTable.put(key, value);
        }
    }
}
