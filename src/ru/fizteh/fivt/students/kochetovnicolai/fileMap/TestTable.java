package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import org.junit.*;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.kochetovnicolai.shell.FileManager;

@RunWith(Theories.class)
public class TestTable extends FileManager {

    DistributedTableProviderFactory factory;
    TableProvider provider;
    Table table;
    protected String validTableName = "default";
    protected String validString = " just simple valid key \n or \t value   ";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void createWorkingDirectoryAndTable() {
        factory = new DistributedTableProviderFactory();
        provider = factory.create(folder.getRoot().getPath());
        table = provider.createTable(validTableName);
    }

    @After
    public void removeWorkingDirectoryAndProvider() {
        factory = null;
        provider = null;
        table = null;
    }

    @DataPoints
    public static String[] argumentsWithBadSymbols = new String [] {
            null,
            "",
            " ",
            "   ",
            "\t",
            "\t \t",
            "\n\t    ",
            " \t \t ",
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Theory
    public void putKeyWithBadSymbolShouldFail(String key) {
        thrown.expect(IllegalArgumentException.class);
        table.put(key, validString);
    }

    @Theory
    public void putValueWithBadSymbolShouldFail(String value) {
        thrown.expect(IllegalArgumentException.class);
        table.put(validString, value);
    }

    @Theory
    public void getWithBadSymbolShouldFail(String key) {
        thrown.expect(IllegalArgumentException.class);
        table.get(key);
    }

    @Theory
    public void removeWithBadSymbolShouldFail(String key) {
        thrown.expect(IllegalArgumentException.class);
        table.remove(key);
    }

    @Test
    public void basicTableMethodsShouldWork() {
        Assert.assertTrue("empty table size should be null", table.size() == 0);
        Assert.assertTrue("remove from empty table should be null", table.remove(validString) == null);
        Assert.assertTrue("rollback from empty table should return 0", table.rollback() == 0);
        Assert.assertTrue("put new key should be null", table.put(validString, validString) == null);
        Assert.assertTrue("table size should equals 1", table.size() == 1);
        Assert.assertTrue("commit should return 1", table.commit() == 1);
        Assert.assertTrue("put new key should be null", table.put("key", "value") == null);
        Assert.assertEquals("remove should return old value", table.remove(validString), validString);
        Assert.assertEquals("rollback should return 2", table.rollback(), 2);
        Assert.assertEquals("key should exists after rollback", table.get(validString), validString);
        Assert.assertEquals("key shouldn't exists after rollback", table.get("key"), null);
    }

    @Test
    public void tableShouldBeConsistent() {
        Assert.assertTrue("put new key should be null", table.put("key1", "value1") == null);
        Assert.assertTrue("put new key should be null", table.put("key2", "value2") == null);
        Assert.assertTrue("put new key should be null", table.put("key3", "value3") == null);
        Assert.assertTrue("commit should return 3", table.commit() == 3);
        Assert.assertTrue("put new key should be null", table.put("key5", "value5") == null);
        Assert.assertTrue("put new key should be null", table.put("key4", "value4") == null);

        table = null;
        provider = null;
        factory = null;

        factory = new DistributedTableProviderFactory();
        provider = factory.create(folder.getRoot().getPath());
        table = provider.createTable(validTableName);

        Assert.assertEquals("table size should be equals 3", table.size(), 3);
        Assert.assertEquals("key should exists in file", table.get("key1"), "value1");
        Assert.assertEquals("key should exists in file", table.get("key2"), "value2");
        Assert.assertEquals("key should exists in file", table.get("key3"), "value3");
        Assert.assertEquals("key should not exists in file", table.get("key4"), null);
        Assert.assertEquals("key should not exists in file", table.get("key5"), null);
    }
}
