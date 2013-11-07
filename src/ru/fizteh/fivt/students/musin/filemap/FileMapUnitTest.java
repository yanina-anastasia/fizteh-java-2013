package ru.fizteh.fivt.students.musin.filemap;

import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import ru.fizteh.fivt.students.musin.shell.FileSystemRoutine;

import java.io.File;
import java.io.IOException;

public class FileMapUnitTest {

    /*@Test
    public void providerFromNullShouldFail() {
        try {
            FileMapProviderFactory factory = new FileMapProviderFactory();
            factory.create(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null location");
        }
    }

    @Test(expected = RuntimeException.class)
    public void notAFolderCheckTest() throws IOException {
        File file = new File(System.getProperty("user.dir"), "test");
        file.createNewFile();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        factory.create(file.getCanonicalPath());
    }

    @Test(expected = RuntimeException.class)
    public void notExistingDirectoryPassedShouldFail() throws IOException {
        File file = new File(System.getProperty("user.dir"), "test");
        FileMapProviderFactory factory = new FileMapProviderFactory();
        factory.create(file.getCanonicalPath());
    }

    @Test(expected = RuntimeException.class)
    public void filesInsideDirectoryShouldFail() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        File newFile = new File(testFolder, "file");
        newFile.createNewFile();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        factory.create(testFolder.getCanonicalPath());
    }

    @Test
    public void newlyCreatedTableIsEmptyWithNoUncommittedChanges() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertTrue(table.size() == 0);
        Assert.assertTrue(table.uncommittedChanges() == 0);
    }

    @Test
    public void createTableCreatesFolder() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        File tableFolder = new File(testFolder, "new");
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertTrue(tableFolder.exists() && tableFolder.isDirectory());
    }

    @Test
    public void tableNullStringsCheckTest() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        try {
            table.put(null, "test");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null pointer instead of string");
        }
        try {
            table.put("test", null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null pointer instead of string");
        }
        try {
            table.get(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null pointer instead of string");
        }
        try {
            table.remove(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null pointer instead of string");
        }
    }

    @Test
    public void getTableReturnsSameInstanceEveryTime() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertTrue(table == provider.getTable("new"));
        Assert.assertTrue(provider.getTable("new") == provider.getTable("new"));
    }

    @Test
    public void createTableReturnsNullIfTableExists() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table = provider.createTable("new");
        Assert.assertTrue(table == null);
    }

    @Test
    public void getTableReturnsNullIfTableDoesntExist() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.getTable("new");
        Assert.assertTrue(table == null);
    }

    @Test
    public void providerNullArgumentCheckTest() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        try {
            provider.createTable(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null name");
        }
        try {
            provider.getTable(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null name");
        }
        try {
            provider.removeTable(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null name");
        }
    }

    @Test
    public void removeTableRemovesDirectory() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        provider.createTable("new");
        provider.removeTable("new");
        File tableFolder = new File(testFolder, "new");
        Assert.assertFalse(tableFolder.exists());
    }

    @Test
    public void getTableForRemovedTableReturnsNull() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        provider.createTable("new");
        provider.removeTable("new");
        Assert.assertTrue(provider.getTable("new") == null);
    }

    @Test(expected = IllegalStateException.class)
    public void removingNonExistingTableShouldFail() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        provider.removeTable("new");
    }

    @Test
    public void getNameReturnCorrectTableName() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertEquals(table.getName(), "new");
    }

    @Test
    public void ifPutValueGetReturnsSameValue() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("test1", "test2");
        Assert.assertEquals(table.get("test1"), "test2");
    }

    @Test
    public void putReturnsPreviouslyStoredValue() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("test1", "test2");
        Assert.assertEquals(table.put("test1", "test3"), "test2");
    }

    @Test
    public void getAndRemoveReturnNullIfKeyDoesntExist() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        Assert.assertTrue(table.get("test1") == null);
        Assert.assertTrue(table.remove("test1") == null);
    }

    @Test
    public void numberOfCommittedChangesShouldBe3then1() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("test1", "test2");
        table.put("test1", "test2");
        table.put("test2", "test3");
        table.put("test3", "test4");
        table.put("test3", "test5");
        Assert.assertTrue(table.commit() == 3);
        table.put("test1", "test7");
        table.remove("test1");
        Assert.assertTrue(table.commit() == 1);
    }

    @Test
    public void rollbackSetsTableBackToLastCommit() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("a", "b");
        table.rollback();
        Assert.assertTrue(table.get("a") == null);
        table.put("a", "b");
        table.commit();
        table.put("a", "c");
        table.rollback();
        Assert.assertEquals(table.get("a"), "b");
    }

    @Test
    public void after3NewKeysAddedSizeShouldBe3() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("a", "c");
        table.put("a", "d");
        table.put("b", "c");
        table.put("c", "e");
        table.remove("b");
        table.put("d", "f");
        Assert.assertTrue(table.size() == 3);
    }

    @Test
    public void removeDeletesKeyFromTable() throws IOException {
        File testFolder = new File(System.getProperty("user.dir"), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new");
        table.put("test1", "test2");
        table.remove("test1");
        Assert.assertTrue(table.get("test1") == null);
    }

    @After
    public void cleanUpFolders() {
        File file = new File(System.getProperty("user.dir"), "test");
        if (file.exists()) {
            FileSystemRoutine.deleteDirectoryOrFile(file);
        }
    }  */

}
