package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import org.junit.*;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

@RunWith(Theories.class)
public class TestTableProvider {
    protected DistributedTableProviderFactory factory;
    protected TableProvider provider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void createWorkingDirectoryAndProvider() throws IOException {
        factory = new DistributedTableProviderFactory();
        provider = factory.create(folder.getRoot().getPath());
    }

    @After
    public void removeWorkingDirectoryAndProvider() {
        provider = null;
        factory = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableEmptyShouldFail() throws IOException {
       provider.removeTable(null);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @DataPoints
    public static String[] argumentsWithBadSymbols = new String [] {
            "",
            ".",
            "..",
            "....",
            "...dir",
            "\\",
            "dir/17.dir",
    };

    @Theory
    public void removeTableBadSymbolShouldFail(String name) throws IOException {
        thrown.expect(IllegalArgumentException.class);
        provider.removeTable(name);
    }

    @Test
    public void removeNotExistingTableShouldFail() throws IOException {
        thrown.expect(IllegalStateException.class);
        provider.removeTable("test");
    }

    @Theory
    public void createTableBadSymbolShouldFail(String name) throws IOException {
        thrown.expect(IllegalArgumentException.class);
        ArrayList<Class<?>> type = new ArrayList<>();
        type.add(String.class);
        provider.createTable(name, type);
    }

    @Theory
    public void getTableBadSymbolShouldFail(String name) {
        thrown.expect(IllegalArgumentException.class);
        provider.getTable(name);
    }

    @Test
    public void getTableShouldGetNullIfTableDoesNotExists() {
        Assert.assertEquals("getTable should return null", provider.getTable("abcd"), null);
    }

    @Test
    public void createTableShouldBeOK() throws IOException {
        ArrayList<Class<?>> type = new ArrayList<>();
        type.add(String.class);
        Table table = provider.createTable("abcd", type);
        Assert.assertTrue("table shouldn't be null", table != null);
        Table table2 = provider.createTable("abcd", type);
        /***/
        Assert.assertEquals("createTable should return null on the same names", null, table2);
        //
        table2 = provider.getTable("abcd");
        /***/
        Assert.assertEquals("getTable should return same objects on the same names", table, table2);
        //
        provider.removeTable("abcd");
        Assert.assertEquals("getTable should return null after remove", provider.getTable("abcd"), null);
        table = provider.createTable("abcd", type);
        Assert.assertTrue("createTable should return table after remove", table != null);
    }

    @Test
    public void serializeAndDeserializeShouldWork() throws IOException, ParseException {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(Long.class);
        types.add(Double.class);
        types.add(Float.class);
        types.add(Byte.class);
        types.add(Boolean.class);
        types.add(String.class);

        ArrayList<Object> values = new ArrayList<>();
        values.add(42);
        values.add(null);
        values.add(3.1415926535897);
        values.add(0.1234f);
        values.add(Byte.parseByte("12"));
        values.add(true);
        values.add("abracadabra");

        Table table = provider.createTable("AllStars", types);
        StringBuilder builder = new StringBuilder("<row>");
        Storeable storeable = provider.createFor(table);
        for (int i = 0; i < values.size(); i++) {
            storeable.setColumnAt(i, values.get(i));
            if (values.get(i) != null) {
                builder.append("<col>");
                builder.append(values.get(i));
                builder.append("</col>");
            } else {
                builder.append("<null/>");
            }
        }
        builder.append("</row>");
        String pattern = builder.toString();
        String value = provider.serialize(table, storeable);
        Assert.assertEquals("serialize should return '" + pattern + "' but it return '" + value + "'", value, pattern);
        Assert.assertEquals("deserialize should return equals object", storeable, provider.deserialize(table, pattern));
    }

    @Test(expected = ParseException.class)
    public void deserializeShouldFailWithLackOfArguments() throws IOException, ParseException {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        Table table = provider.createTable("table", types);
        provider.deserialize(table, "<row><col>abcd></col></row>");
    }

    @Test(expected = ParseException.class)
    public void deserializeShouldFailWithIncorrectValues() throws IOException, ParseException {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        Table table = provider.createTable("table", types);
        provider.deserialize(table, "<row><col>3.14</col><col>abcd></col></row>");
    }

    @Test(expected = ParseException.class)
    public void deserializeShouldFailWithExcessOfArguments() throws IOException, ParseException {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        Table table = provider.createTable("table", types);
        provider.deserialize(table, "<row><col>42</col><col>abcd></col><null/></row>");
    }
}
