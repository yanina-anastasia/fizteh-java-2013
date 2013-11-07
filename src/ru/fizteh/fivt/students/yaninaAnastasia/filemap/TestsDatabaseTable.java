package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TestsDatabaseTable {
    Table table;
    Table multiColumnTable;
    TableProviderFactory factory;
    TableProvider provider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void beforeTest() {
        List<Class<?>> columnTypes = new ArrayList<Class<?>>() {{
            add(Integer.class);
        }};
        List<Class<?>> columnMultiTypes = new ArrayList<Class<?>>() {{
            add(Integer.class);
            add(String.class);
            add(String.class);
        }};
        factory = new DatabaseTableProviderFactory();
        try {
            provider = factory.create(folder.getRoot().getPath());
            table = provider.createTable("testTable", columnTypes);
            multiColumnTable = provider.createTable("MultiColumnTable", columnMultiTypes);
        } catch (IOException e) {
            //
        }
    }

    @After
    public void afterTest() {
        try {
            provider.removeTable("testTable");
            provider.removeTable("MultiColumnTable");
        } catch (IOException e) {
            //
        }
    }

    public Storeable makeStoreable(int value) {
        try {
            return provider.deserialize(table, String.format("<row><col>%d</col></row>", value));
        } catch (ParseException e) {
            return null;
        }
    }

    public Storeable makeMultiStoreable(int value, String valueString, String valueDouble) {
        try {
            return provider.deserialize(multiColumnTable, "<row><col>" + value + "</col><col>" + valueString + "</col><col>"
                    + valueDouble + "</col></row>");
        } catch (ParseException e) {
            return null;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testKeyNull() {
        table.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyKey() {
        table.put("", makeStoreable(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testKeyWithWhiteSpaces() {
        table.put("key key key", makeStoreable(5));
    }

    // alien falls with it?
    /*@Test(expected = IllegalArgumentException.class)
    public void testValueWrongStoreable() {
        try {
            table.put("key", provider.deserialize(table, "<row><col>Five</col></row>"));
        } catch (ParseException e) {
            //
        }
    }    */

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() {
        table.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullName() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullName() {
        table.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEmptyName() {
        table.get("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmptyName() {
        table.remove("");
    }

    @Test
    public void testPutGet() {
        table.put("key", makeStoreable(1));
        Assert.assertNotNull(table.put("key", makeStoreable(2)));
        table.put("key", makeStoreable(3));
        Assert.assertEquals(table.get("key"), makeStoreable(3));
        table.remove("key");
    }

    @Test
    public void testPutGetRemove() {
        Assert.assertNull(table.put("key", makeStoreable(1)));
        table.remove("key");
        Assert.assertNull(table.put("key", makeStoreable(1)));
        Assert.assertEquals(table.get("key"), makeStoreable(1));
        table.remove("key");
        Assert.assertEquals(table.get("key"), null);
    }

    @Test
    public void testMultiPutGetRemove() {
        multiColumnTable.put("ключ", makeMultiStoreable(1, "значение1", "1.1"));
        multiColumnTable.remove("ключ");
        Assert.assertNull(multiColumnTable.put("ключ", makeMultiStoreable(1, "значение1", "1.1")));
        multiColumnTable.remove("ключ");
        Assert.assertNull(multiColumnTable.get("ключ"));
    }

    @Test
    public void testMultiWork() {
        multiColumnTable.put("key", makeMultiStoreable(1, "value", "extra value"));
        multiColumnTable.put("key", makeMultiStoreable(2, "value2", "extra value 2"));
        multiColumnTable.put("key_extra", makeMultiStoreable(3, "value3", "extra value 3"));
        try {
            Assert.assertEquals(multiColumnTable.commit(), 2);
        } catch (IOException e) {
            //
        }
    }

    @Test
    public void testMultiRollback() {
        multiColumnTable.put("key", makeMultiStoreable(1, "value", "extra value"));
        multiColumnTable.remove("key");
        multiColumnTable.put("key_extra", makeMultiStoreable(3, "value3", "extra value 3"));
        multiColumnTable.remove("key_extra");
        Assert.assertEquals(multiColumnTable.rollback(), 0);
    }

    @Test
    public void firstTestCommit() {
        Assert.assertEquals(table.size(), 0);
        table.put("key1", makeStoreable(1));
        table.put("key2", makeStoreable(2));
        table.put("key3", makeStoreable(3));
        table.remove("key3");
        Assert.assertEquals(table.size(), 2);
        try {
            Assert.assertEquals(table.commit(), 2);
        } catch (IOException e) {
            //
        }
    }

    @Test
    public void secondTestCommit() {
        Assert.assertEquals(table.size(), 0);
        table.put("key", makeStoreable(1));
        table.put("key", makeStoreable(2));
        table.put("key", makeStoreable(3));
        Assert.assertEquals(table.size(), 1);
        try {
            Assert.assertEquals(table.commit(), 1);
            table.remove("key");
            Assert.assertEquals(table.size(), 0);
            Assert.assertEquals(table.commit(), 1);
        } catch (IOException e) {
            //
        }
    }

    @Test
    public void thirdTestCommit() {
        Assert.assertEquals(table.size(), 0);
        table.put("key", makeStoreable(1));
        table.remove("key");
        table.put("key2", makeStoreable(2));
        table.remove("key2");
        Assert.assertNull(table.get("key2"));
        Assert.assertEquals(table.size(), 0);
        try {
            Assert.assertEquals(table.commit(), 0);
        } catch (IOException e) {
            //
        }
    }

    @Test
    public void firstTestRollback() {
        Assert.assertEquals(table.size(), 0);
        table.put("key", makeStoreable(1));
        table.put("key2", makeStoreable(2));
        table.put("key3", makeStoreable(3));
        Assert.assertEquals(table.rollback(), 3);
    }

    @Test
    public void secondTestRollback() {
        Assert.assertEquals(table.size(), 0);
        table.put("key", makeStoreable(1));
        table.remove("key");
        Assert.assertEquals(table.size(), 0);
        Assert.assertEquals(table.rollback(), 0);
        table.put("key2", makeStoreable(2));
        try {
            Assert.assertEquals(table.commit(), 1);
        } catch (IOException e) {
            //
        }
        table.put("key2", makeStoreable(3));
        table.put("key2", makeStoreable(2));
        Assert.assertEquals(table.size(), 1);
        Assert.assertEquals(table.rollback(), 0);
    }

    @Test
    public void commonTest() {
        Assert.assertEquals(table.size(), 0);
        Assert.assertNull(table.put("1", makeStoreable(1)));
        Assert.assertNull(table.put("2", makeStoreable(2)));
        Assert.assertNull(table.put("3", makeStoreable(4)));
        Assert.assertNotNull(table.put("3", makeStoreable(3)));
        Assert.assertEquals(table.size(), 3);
        try {
            Assert.assertEquals(table.commit(), 3);
        } catch (IOException e) {
            //
        }
        table.remove("1");
        table.remove("2");
        table.remove("3");
        Assert.assertEquals(table.size(), 0);
        Assert.assertEquals(table.rollback(), 3);
        Assert.assertEquals(table.size(), 3);
    }

    @Test
    public void goodTest() {
        Assert.assertNull(table.put("111", makeStoreable(1)));
        table.remove("111");
        Assert.assertEquals(table.rollback(), 0);
    }
}
