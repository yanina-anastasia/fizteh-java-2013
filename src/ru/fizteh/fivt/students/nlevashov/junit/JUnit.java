package ru.fizteh.fivt.students.nlevashov.junit;

import org.junit.*;
import static org.junit.Assert.*;

import ru.fizteh.fivt.students.nlevashov.factory.*;
import ru.fizteh.fivt.storage.strings.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JUnit {
    TableProviderFactory factory;
    TableProvider provider;
    Table table;

    @Before
    public void providerCreatingPlatformMaking() {
        factory = new MyTableProviderFactory();
        assertNotNull(factory);

        try {
            Files.createDirectory(Paths.get("factory1").resolve("emptyDirectory"));

            Files.createDirectory(Paths.get("factory1").resolve("directoryWithFile"));
            Files.createFile(Paths.get("factory1").resolve("directoryWithFile").resolve("someFile"));

            Files.createDirectory(Paths.get("factory1").resolve("directoryWithWrongNameDirectory"));
            Files.createDirectory(Paths.get("factory1").resolve("directoryWithWrongNameDirectory").resolve("12.d"));

            Files.createDirectory(Paths.get("factory1").resolve("directoryWithDirectoryWithWrongNameFile"));
            Files.createDirectory(Paths.get("factory1").resolve("directoryWithDirectoryWithWrongNameFile").resolve("12.dat"));
            Files.createFile(Paths.get("factory1").resolve("directoryWithDirectoryWithWrongNameFile").resolve("12.dat").resolve("1.d"));
        } catch (IOException e) {
        }
    }

    @After
    public void providerCreatingPlatformCleaning() {
        try {
            Files.delete(Paths.get("factory1").resolve("emptyDirectory"));

            Files.delete(Paths.get("factory1").resolve("directoryWithFile").resolve("someFile"));
            Files.delete(Paths.get("factory1").resolve("directoryWithFile"));

            Files.delete(Paths.get("factory1").resolve("directoryWithWrongNameDirectory").resolve("12.d"));
            Files.delete(Paths.get("factory1").resolve("directoryWithWrongNameDirectory"));

            Files.delete(Paths.get("factory1").resolve("directoryWithDirectoryWithWrongNameFile").resolve("12.dat").resolve("1.d"));
            Files.delete(Paths.get("factory1").resolve("directoryWithDirectoryWithWrongNameFile").resolve("12.dat"));
            Files.delete(Paths.get("factory1").resolve("directoryWithDirectoryWithWrongNameFile"));
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullProviderCreating() {
        factory.create(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyProviderCreating() {
        factory.create("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void wrongNameProviderCreating() {
        factory.create("!@#$%^&*()_+-=qwerty|\\\\/<>?';lkjhgfdsazxcvbnm`~");
    }

    @Test (expected = RuntimeException.class)
    public void directoryWithFileProviderCreating() {
        factory.create("factory1" + File.separator + "directoryWithFile");
    }

    @Test (expected = RuntimeException.class)
    public void directoryWithWrongNameDirectoryProviderCreating() {
        factory.create("factory1" + File.separator + "directoryWithWrongNameDirectory");
    }

    @Test (expected = RuntimeException.class)
    public void directoryWithDirectoryWithWrongNameFileProviderCreating() {
        factory.create("factory1" + File.separator + "directoryWithDirectoryWithWrongNameFile");
    }

    //------------------------------------------------------------------------

    @Before
    public void tableGettingCreatingRemovingPlatformMaking() {
        provider = factory.create("providerName");
        assertNotNull(provider);
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullTableCreating() {
        provider.createTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyTableCreating() {
        provider.createTable("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void wrongNameTableCreating() {
        provider.createTable("!@#$%^&*()_+-=qwerty|\\\\/<>?';lkjhgfdsazxcvbnm`~");
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullTableRemoving() {
        provider.removeTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyTableRemoving() {
        provider.removeTable("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void wrongNameTableRemoving() {
        provider.removeTable("!@#$%^&*()_+-=qwerty|\\\\/<>?';lkjhgfdsazxcvbnm`~");
    }

    @Test (expected = IllegalStateException.class)
    public void nonexistingTableRemoving() {
        provider.removeTable("notExistingTableName");
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
        table = provider.createTable("tableName");
        assertNotNull(table);
        assertNull(provider.createTable("tableName"));
        provider.removeTable("tableName");
    }

    @Test
    public void manyTableGettingCreatingRemoving() {
        Table table1 = provider.createTable("tableName1");
        Table table2 = provider.createTable("tableName2");
        Table table3 = provider.createTable("tableName3");
        Table table4 = provider.createTable("русскоеНазвание");
        assertNotNull(table1);
        assertNotNull(table2);
        assertNotNull(table3);
        assertNotNull(table4);

        assertNotNull(provider.getTable("tableName1"));
        assertNotNull(provider.getTable("tableName2"));
        assertNotNull(provider.getTable("tableName3"));
        assertNotNull(provider.getTable("русскоеНазвание"));

        assertNull(provider.createTable("tableName1"));
        assertNull(provider.createTable("tableName2"));
        assertNull(provider.createTable("tableName3"));
        assertNull(provider.createTable("русскоеНазвание"));

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

        Table table5 = provider.createTable("tableName2");
        Table table6 = provider.createTable("tableName3");
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
    }

    //------------------------------------------------------------------------

    @Before
    public void tablePlatformMaking() {
        table = provider.createTable("table");
    }

    @After
    public void tablePlatformCleaning() {
        provider.removeTable("table");
    }

    @Test
    public void getName() {
        assertEquals("table", table.getName());
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullKeyPut() {
        table.put(null, "value");
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyKeyPut() {
        table.put("", "value");
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullValuePut() {
        table.put("key", null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyValuePut() {
        table.put("key", "");
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
        assertEquals(0, table.size());

        assertNull(table.get("key1"));
        assertNull(table.get("key2"));
        assertNull(table.get("русский ключ с пробелами"));

        assertEquals(0, table.size());

        assertNull(table.put("key1", "value1"));
        assertNull(table.put("key2", "value2"));
        assertNull(table.put("русский ключ с пробелами", "русское значение с пробелами"));

        assertEquals(3, table.size());

        assertEquals("value1", table.get("key1"));
        assertEquals("value2", table.get("key2"));
        assertEquals("русское значение с пробелами", table.get("русский ключ с пробелами"));

        assertEquals(3, table.size());

        assertEquals("value2", table.remove("key2"));
        assertNull(table.remove("key2"));

        assertEquals(2, table.size());

        assertEquals("value1", table.get("key1"));
        assertNull(table.get("key2"));
        assertEquals("русское значение с пробелами", table.get("русский ключ с пробелами"));
    }

    @Test
    public void commitRollbackTest() {
        assertEquals(0, table.commit());
        table.put("key1", "value1");
        table.put("key2", "value2");
        assertEquals(2, table.commit());
        table.remove("key1");
        assertEquals(1, table.commit());
        assertEquals(0, table.commit());
        table.put("key2", "anotherValue2");
        assertEquals(1, table.commit());

        table.put("key3", "value3");
        table.remove("key3");
        assertEquals(0, table.commit());

        table.put("key4", "value4");
        table.get("key4");
        table.put("key4", "anotherValue4");
        assertEquals(1, table.commit());
    }
}
