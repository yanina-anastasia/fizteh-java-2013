package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TestsIndex {
    static List<Class<?>> columnTypes;
    static List<Class<?>> columnMultiTypes;
    Table table;
    Table multiColumnTable;
    static TableProviderFactory factory;
    DatabaseTableProvider provider;
    private static final String SINGLE_COLUMN_TABLE_NAME = "testTable";
    private static final String MULTI_COLUMN_TABLE_NAME = "MultiColumnTable";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws IOException {
        columnTypes = new ArrayList<Class<?>>() {
            {
                add(Integer.class);
            }
        };
        columnMultiTypes = new ArrayList<Class<?>>() {
            {
                add(Integer.class);
                add(String.class);
                add(Double.class);
            }
        };
        factory = new DatabaseTableProviderFactory();

    }

    @Before
    public void beforeTest() throws IOException {
        provider = (DatabaseTableProvider) factory.create(folder.getRoot().getPath());
        table = provider.createTable(SINGLE_COLUMN_TABLE_NAME, columnTypes);
        multiColumnTable = provider.createTable(MULTI_COLUMN_TABLE_NAME, columnMultiTypes);
    }

    @After
    public void afterTest() throws IOException {
        provider.removeTable(SINGLE_COLUMN_TABLE_NAME);
        provider.removeTable(MULTI_COLUMN_TABLE_NAME);
    }

    public Storeable makeStoreable(int value) {
        try {
            return provider.deserialize(table, String.format("<row><col>%d</col></row>", value));
        } catch (ParseException e) {
            return null;
        }
    }

    public Storeable makeMultiStoreable(int value, String valueString, Double valueDouble) {
        try {
            return provider.deserialize(multiColumnTable,
                    "<row><col>" + value + "</col><col>" + valueString + "</col><col>" + valueDouble + "</col></row>");
        } catch (ParseException e) {
            return null;
        }
    }

    @Test
    public void indexCreate() {
        table.put("key_1", makeStoreable(1));
        table.put("key_2", makeStoreable(2));
        table.put("key_3", makeStoreable(3));
        try {
            table.commit();
        } catch (IOException e) {
            //
        }
        DatabaseIndex testIndex = provider.createIndex(table, 0, "testIndex");
        Assert.assertEquals(testIndex.getName(), "testIndex");
        Assert.assertEquals(testIndex.get("2"), table.get("key_2"));
        table.remove("key_1");
        table.remove("key_2");
        table.remove("key_3");
    }

    @Test
    public void getUnavailableIndex() {
        table.put("key_1", makeStoreable(1));
        table.put("key_2", makeStoreable(2));
        table.put("key_3", makeStoreable(3));
        DatabaseIndex testIndex = provider.createIndex(table, 0, "testIndex");
        Assert.assertEquals(testIndex.getName(), "testIndex");
        Assert.assertEquals(testIndex.column, 0);
        Assert.assertEquals(testIndex.get("2"), null);
        table.remove("key_1");
        table.remove("key_2");
        table.remove("key_3");
    }

    @Test
    public void multiColumnTableIndex() {
        multiColumnTable.put("key_1", makeMultiStoreable(1, "First", 1.0));
        multiColumnTable.put("key_2", makeMultiStoreable(2, "Second", 2.0));
        multiColumnTable.put("key_3", makeMultiStoreable(3, "Third", 3.0));
        try {
            multiColumnTable.commit();
        } catch (IOException e) {
            //
        }

        DatabaseIndex testIndexOne = provider.createIndex(multiColumnTable, 1, "testIndexOne");
        DatabaseIndex testIndexTwo = provider.createIndex(multiColumnTable, 2, "testIndexTwo");

        Assert.assertEquals(testIndexOne.get("First"), multiColumnTable.get("key_1"));
        Assert.assertEquals(testIndexTwo.get("3.0"), makeMultiStoreable(3, "Third", 3.0));
        multiColumnTable.remove("key_1");
        multiColumnTable.remove("key_2");
        multiColumnTable.remove("key_3");
    }

    @Test
    public void testGetName() {
        DatabaseIndex testIndexOne = provider.createIndex(multiColumnTable, 1, "testIndexOne");
        Assert.assertEquals(testIndexOne.getName(), "testIndexOne");
    }

    @Test
    public void testGetColumn() {
        DatabaseIndex testIndexOne = provider.createIndex(multiColumnTable, 1, "testIndexOne");
        Assert.assertEquals(testIndexOne.column, 1);
    }
}