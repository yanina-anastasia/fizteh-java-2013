package ru.fizteh.fivt.students.nlevashov.junit;

import org.junit.*;
import static org.junit.Assert.*;

import ru.fizteh.fivt.students.nlevashov.factory.*;
import ru.fizteh.fivt.students.nlevashov.storable.Storable;
import ru.fizteh.fivt.storage.structured.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class JUnit {
    TableProviderFactory factory;
    TableProvider provider;
    Table table;

    @Before
    public void providerCreatingPlatformMaking() {
        factory = new MyTableProviderFactory();
        assertNotNull(factory);

        try {
            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory1"));
            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory1").resolve("emptyDirectory"));

            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory2"));
            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory2").resolve("directoryWithFile"));
            Files.createFile(Paths.get(System.getProperty("user.dir")).resolve("factory2").resolve("directoryWithFile").resolve("someFile"));
            Files.createFile(Paths.get(System.getProperty("user.dir")).resolve("factory2").resolve("directoryWithFile").resolve("signature.tsv"));

            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory3"));
            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory3").resolve("directoryWithWrongNameDirectory"));
            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory3").resolve("directoryWithWrongNameDirectory").resolve("12.dat"));
            Files.createFile(Paths.get(System.getProperty("user.dir")).resolve("factory3").resolve("directoryWithWrongNameDirectory").resolve("signature.tsv"));

            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory4"));
            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile"));
            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile").resolve("12.dir"));
            Files.createFile(Paths.get(System.getProperty("user.dir")).resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile").resolve("12.dir").resolve("1.d"));
            Files.createFile(Paths.get(System.getProperty("user.dir")).resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile").resolve("signature.tsv"));

            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory5"));
            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory5").resolve("directoryWithoutSignature"));
            Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("factory5").resolve("directoryWithoutSignature").resolve("12.dir"));
            Files.createFile(Paths.get(System.getProperty("user.dir")).resolve("factory5").resolve("directoryWithoutSignature").resolve("12.dir").resolve("1.dat"));
        } catch (IOException e) {
        }
    }

    @After
    public void providerCreatingPlatformCleaning() {
        try {
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory1").resolve("emptyDirectory"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory1"));

            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory4").resolve("directoryWithFile").resolve("signature.tsv"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory2").resolve("directoryWithFile").resolve("someFile"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory2").resolve("directoryWithFile"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory2"));

            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory4").resolve("directoryWithWrongNameDirectory").resolve("signature.tsv"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory3").resolve("directoryWithWrongNameDirectory").resolve("12.dat"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory3").resolve("directoryWithWrongNameDirectory"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory3"));

            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile").resolve("signature.tsv"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile").resolve("12.dir").resolve("1.d"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile").resolve("12.dir"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory4").resolve("directoryWithDirectoryWithWrongNameFile"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory4"));

            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory5").resolve("directoryWithoutSignature").resolve("12.dir").resolve("1.dat"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory5").resolve("directoryWithoutSignature").resolve("12.dir"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory5").resolve("directoryWithoutSignature"));
            Files.delete(Paths.get(System.getProperty("user.dir")).resolve("factory5"));
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullProviderCreating() {
        try {
            factory.create(null);
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyProviderCreating() {
        try {
            factory.create("");
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void wrongNameProviderCreating() {
        try {
            factory.create("!@#$%^&*()_+-=qwerty|\\\\/<>?';lkjhgfdsazxcvbnm`~");
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalStateException.class)
    public void directoryWithFileProviderCreating() {
        try {
            factory.create("factory2");
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalStateException.class)
    public void directoryWithWrongNameDirectoryProviderCreating() {
        try {
            factory.create("factory3");
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalStateException.class)
    public void directoryWithDirectoryWithWrongNameFileProviderCreating() {
        try {
            factory.create("factory4");
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalStateException.class)
    public void directoryWithoutSignatureProviderCreating() {
        try {
            factory.create("factory5");
        } catch (IOException e) {
        }
    }

    //------------------------------------------------------------------------

    @Before
    public void tableGettingCreatingRemovingPlatformMaking() {
        try {
            provider = factory.create("providerName");
        } catch (IOException e) {
        }
        assertNotNull(provider);
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullTableCreating() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        try {
            provider.createTable(null, types);
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyTableCreating() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        try {
            provider.createTable("", types);
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void wrongNameTableCreating() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        try {
            provider.createTable("!@#$%^&*()_+-=qwerty|\\\\/<>?';lkjhgfdsazxcvbnm`~", types);
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullTypesCreating() {
        try {
            provider.createTable("car", null);
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyTypesCreating() {
        ArrayList<Class<?>> types = new ArrayList<>();
        try {
            provider.createTable("child", types);
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void typesWithNullCreating() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(null);
        types.add(String.class);
        try {
            provider.createTable("child", types);
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void typesWithIncorrectTypeCreating() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(Character.class);
        types.add(String.class);
        try {
            provider.createTable("child", types);
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullTableRemoving() {
        try {
            provider.removeTable(null);
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyTableRemoving() {
        try {
            provider.removeTable("");
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void wrongNameTableRemoving() {
        try {
            provider.removeTable("!@#$%^&*()_+-=qwerty|\\\\/<>?';lkjhgfdsazxcvbnm`~");
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalStateException.class)
    public void nonexistingTableRemoving() {
        try {
            provider.removeTable("notExistingTableName");
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullTableGetting() {
        provider.getTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyTableGetting() {
        provider.getTable("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void wrongNameTableGetting() {
        provider.getTable("!@#$%^&*()_+-=qwerty|\\\\/<>?';lkjhgfdsazxcvbnm`~");
    }

    @Test
    public void nonexistingTableGetting() {
        assertNull(provider.getTable("notExistingTableName"));
    }

    @Test
    public void existingTableCreating() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        try {
            table = provider.createTable("tableName", types);
            assertNotNull(table);
            assertNull(provider.createTable("tableName", types));
            provider.removeTable("tableName");
        } catch (IOException e) {
        }
    }

    @Test
    public void manyTableGettingCreatingRemoving() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        try {
            Table table1 = provider.createTable("tableName1", types);
            Table table2 = provider.createTable("tableName2", types);
            Table table3 = provider.createTable("tableName3", types);
            Table table4 = provider.createTable("русскоеНазвание", types);
            assertNotNull(table1);
            assertNotNull(table2);
            assertNotNull(table3);
            assertNotNull(table4);

            assertNotNull(provider.getTable("tableName1"));
            assertNotNull(provider.getTable("tableName2"));
            assertNotNull(provider.getTable("tableName3"));
            assertNotNull(provider.getTable("русскоеНазвание"));

            assertNull(provider.createTable("tableName1", types));
            assertNull(provider.createTable("tableName2", types));
            assertNull(provider.createTable("tableName3", types));
            assertNull(provider.createTable("русскоеНазвание", types));

            assertNotNull(provider.getTable("tableName1"));
            assertNotNull(provider.getTable("tableName2"));
            assertNotNull(provider.getTable("tableName3"));
            assertNotNull(provider.getTable("русскоеНазвание"));

            provider.removeTable("tableName2");
            provider.removeTable("tableName3");

            assertNotNull(provider.getTable("tableName1"));
            assertNull(provider.getTable("tableName2"));
            assertNull(provider.getTable("tableName3"));
            assertNotNull(provider.getTable("русскоеНазвание"));

            Table table5 = provider.createTable("tableName2", types);
            Table table6 = provider.createTable("tableName3", types);
            assertNotNull(table5);
            assertNotNull(table6);

            assertNotNull(provider.getTable("tableName1"));
            assertNotNull(provider.getTable("tableName2"));
            assertNotNull(provider.getTable("tableName3"));
            assertNotNull(provider.getTable("русскоеНазвание"));

            provider.removeTable("tableName1");
            provider.removeTable("tableName2");
            provider.removeTable("tableName3");
            provider.removeTable("русскоеНазвание");

            assertNull(provider.getTable("tableName1"));
            assertNull(provider.getTable("tableName2"));
            assertNull(provider.getTable("tableName3"));
            assertNull(provider.getTable("русскоеНазвание"));
        } catch (IOException e) {
        }
    }

    //------------------------------------------------------------------------

    @Before
    public void tablePlatformMaking() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        try {
            table = provider.createTable("table", types);
        } catch (IOException e) {
        }
    }

    @After
    public void tablePlatformCleaning() {
        try {
            provider.removeTable("table");
        } catch (IOException e) {
        }
    }

    @Test
    public void getName() {
        assertEquals("table", table.getName());
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullKeyPut() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        ArrayList<Object> values = new ArrayList<>();
        values.add(666);
        values.add("I'm sexy & i know it!");
        Storeable value = new Storable(types, values);
        table.put(null, value);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyKeyPut() {
        ArrayList<Class<?>> types = new ArrayList<>();
        types.add(Integer.class);
        types.add(String.class);
        ArrayList<Object> values = new ArrayList<>();
        values.add(666);
        values.add("I'm sexy & i know it!");
        Storeable value = new Storable(types, values);
        table.put("", value);
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullValuePut() {
        table.put("key", null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyValuePut() {
        ArrayList<Class<?>> types = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        Storeable value = new Storable(types, values);
        table.put("key", value);
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullKeyGet() {
        table.get(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyKeyGet() {
        table.get("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullKeyRemove() {
        table.remove(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyKeyRemove() {
        table.remove("");
    }

    @Test
    public void getPutRemoveSizeTest() {
        ArrayList<Class<?>> types1 = new ArrayList<>();
        types1.add(Integer.class);
        types1.add(String.class);
        ArrayList<Object> values1 = new ArrayList<>();
        values1.add(666);
        values1.add("I'm sexy & i know it!");
        Storeable value1 = new Storable(types1, values1);

        ArrayList<Class<?>> types2 = new ArrayList<>();
        types2.add(Integer.class);
        types2.add(String.class);
        ArrayList<Object> values2 = new ArrayList<>();
        values2.add(666);
        values2.add("I'm sexy & i know it!");
        Storeable value2 = new Storable(types2, values2);

        ArrayList<Class<?>> types3 = new ArrayList<>();
        types3.add(Integer.class);
        types3.add(String.class);
        ArrayList<Object> values3 = new ArrayList<>();
        values3.add(666);
        values3.add("I'm sexy & i know it!");
        Storeable value3 = new Storable(types3, values3);

        assertEquals(0, table.size());

        assertNull(table.get("key1"));
        assertNull(table.get("key2"));
        assertNull(table.get("русскийКлюч"));

        assertEquals(0, table.size());

        assertNull(table.put("key1", value1));
        assertNull(table.put("key2", value2));
        assertNull(table.put("русскийКлюч", value3));

        assertEquals(3, table.size());

        assertEquals(value1, table.get("key1"));
        assertEquals(value2, table.get("key2"));
        assertEquals(value3, table.get("русскийКлюч"));

        assertEquals(3, table.size());

        assertEquals(value2, table.remove("key2"));
        assertNull(table.remove("key2"));

        assertEquals(2, table.size());

        assertEquals(value1, table.get("key1"));
        assertNull(table.get("key2"));
        assertEquals(value3, table.get("русскийКлюч"));
    }

    @Test
    public void commitRollbackTest() {
        ArrayList<Class<?>> types1 = new ArrayList<>();
        types1.add(Integer.class);
        types1.add(String.class);
        ArrayList<Object> values1 = new ArrayList<>();
        values1.add(111);
        values1.add("I'm sexy & i know it!");
        Storeable value1 = new Storable(types1, values1);

        ArrayList<Class<?>> types2 = new ArrayList<>();
        types2.add(Integer.class);
        types2.add(String.class);
        ArrayList<Object> values2 = new ArrayList<>();
        values2.add(222);
        values2.add("I'm sexy & i know it!");
        Storeable value2 = new Storable(types2, values2);

        ArrayList<Class<?>> types3 = new ArrayList<>();
        types3.add(Integer.class);
        types3.add(String.class);
        ArrayList<Object> values3 = new ArrayList<>();
        values3.add(333);
        values3.add("I'm sexy & i know it!");
        Storeable value3 = new Storable(types3, values3);

        ArrayList<Class<?>> types4 = new ArrayList<>();
        types4.add(Integer.class);
        types4.add(String.class);
        ArrayList<Object> values4 = new ArrayList<>();
        values4.add(444);
        values4.add("I'm sexy & i know it!");
        Storeable value4 = new Storable(types4, values4);

        try {
            assertEquals(0, table.commit());
            table.put("key1", value1);
            table.put("key2", value2);
            assertEquals(2, table.commit());
            table.remove("key1");
            assertEquals(1, table.commit());
            assertEquals(0, table.commit());
            table.put("key2", value4);
            assertEquals(1, table.commit());

            table.put("key3", value3);
            table.remove("key3");
            assertEquals(0, table.commit());

            table.put("key4", value4);
            table.get("key4");
            table.put("key4", value2);
            assertEquals(1, table.commit());
        } catch (IOException e) {
        }
    }
}
