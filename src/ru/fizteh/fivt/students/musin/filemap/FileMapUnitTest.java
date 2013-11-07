package ru.fizteh.fivt.students.musin.filemap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileMapUnitTest {

    private ArrayList<Class<?>> getColumnTypeList() {
        ArrayList<Class<?>> columnTypes = new ArrayList<>();
        columnTypes.add(Integer.class);
        columnTypes.add(String.class);
        columnTypes.add(Byte.class);
        return columnTypes;
    }

    private Storeable getSampleStoreable() {
        Storeable sample = new FixedList(getColumnTypeList());
        sample.setColumnAt(0, 1);
        sample.setColumnAt(1, "hello");
        sample.setColumnAt(2, Byte.valueOf((byte) 2));
        return sample;
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void providerFromNullShouldFail() throws IOException {
        try {
            FileMapProviderFactory factory = new FileMapProviderFactory();
            factory.create(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null location");
        }
    }

    @Test(expected = RuntimeException.class)
    public void notAFolderCheckTest() throws IOException {
        File file = new File(folder.getRoot(), "test");
        file.createNewFile();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        factory.create(file.getCanonicalPath());
    }

    @Test(expected = IOException.class)
    public void notExistingDirectoryPassedShouldFail() throws IOException {
        File file = new File(folder.getRoot(), "test");
        FileMapProviderFactory factory = new FileMapProviderFactory();
        factory.create(file.getCanonicalPath());
    }

    @Test(expected = RuntimeException.class)
    public void filesInsideDirectoryShouldFail() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        File newFile = new File(testFolder, "file");
        newFile.createNewFile();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        factory.create(testFolder.getCanonicalPath());
    }

    @Test
    public void newlyCreatedTableIsEmptyWithNoUncommittedChanges() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Assert.assertTrue(table.size() == 0);
        Assert.assertTrue(table.uncommittedChanges() == 0);
    }

    @Test
    public void createTableCreatesFolder() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        File tableFolder = new File(testFolder, "new");
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Assert.assertTrue(tableFolder.exists() && tableFolder.isDirectory());
    }

    @Test
    public void tableNullStringsCheckTest() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Storeable sample = getSampleStoreable();
        try {
            table.put(null, sample);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null pointer instead of string");
        }
        try {
            table.put("test", null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null value");
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
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Assert.assertTrue(table == provider.getTable("new"));
        Assert.assertTrue(provider.getTable("new") == provider.getTable("new"));
    }

    @Test
    public void createTableReturnsNullIfTableExists() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        table = provider.createTable("new", getColumnTypeList());
        Assert.assertTrue(table == null);
    }

    @Test
    public void getTableReturnsNullIfTableDoesntExist() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.getTable("new");
        Assert.assertTrue(table == null);
    }

    @Test
    public void providerNullArgumentCheckTest() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        try {
            provider.createTable(null, getColumnTypeList());
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null name");
        }
        try {
            provider.createTable("new", null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Null columnTypes");
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
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        provider.createTable("new", getColumnTypeList());
        provider.removeTable("new");
        File tableFolder = new File(testFolder, "new");
        Assert.assertFalse(tableFolder.exists());
    }

    @Test
    public void getTableForRemovedTableReturnsNull() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        provider.createTable("new", getColumnTypeList());
        provider.removeTable("new");
        Assert.assertTrue(provider.getTable("new") == null);
    }

    @Test(expected = IllegalStateException.class)
    public void removingNonExistingTableShouldFail() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        provider.removeTable("new");
    }

    @Test
    public void getNameReturnCorrectTableName() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Assert.assertEquals(table.getName(), "new");
    }

    @Test
    public void ifPutValueGetReturnsSameValue() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        table.put("test1", getSampleStoreable());
        Assert.assertEquals(table.get("test1"), getSampleStoreable());
    }

    @Test
    public void putReturnsPreviouslyStoredValue() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        table.put("test1", getSampleStoreable());
        Storeable sample2 = getSampleStoreable();
        sample2.setColumnAt(2, null);
        Assert.assertEquals(table.put("test1", sample2), getSampleStoreable());
    }

    @Test
    public void getAndRemoveReturnNullIfKeyDoesntExist() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Assert.assertTrue(table.get("test1") == null);
        Assert.assertTrue(table.remove("test1") == null);
    }

    @Test
    public void numberOfCommittedChangesShouldBe3then1() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        Storeable sample1 = getSampleStoreable();
        Storeable sample2 = getSampleStoreable();
        sample2.setColumnAt(1, null);
        Storeable sample3 = getSampleStoreable();
        sample3.setColumnAt(0, null);
        Storeable sample4 = getSampleStoreable();
        sample4.setColumnAt(2, null);
        Storeable sample5 = getSampleStoreable();
        sample5.setColumnAt(0, 10000);
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        table.put("test1", sample1);
        table.put("test1", sample1);
        table.put("test2", sample2);
        table.put("test3", sample3);
        table.put("test3", sample4);
        Assert.assertTrue(table.commit() == 3);
        table.put("test1", sample5);
        table.remove("test1");
        Assert.assertTrue(table.commit() == 1);
    }

    @Test
    public void rollbackSetsTableBackToLastCommit() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Storeable sample1 = getSampleStoreable();
        Storeable sample2 = getSampleStoreable();
        sample2.setColumnAt(1, null);
        table.put("a", sample1);
        table.rollback();
        Assert.assertTrue(table.get("a") == null);
        table.put("a", sample1);
        table.commit();
        table.put("a", sample2);
        table.rollback();
        Assert.assertEquals(table.get("a"), sample1);
    }

    @Test
    public void after3NewKeysAddedSizeShouldBe3() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Storeable sample1 = getSampleStoreable();
        Storeable sample2 = getSampleStoreable();
        sample2.setColumnAt(1, null);
        Storeable sample3 = getSampleStoreable();
        sample3.setColumnAt(0, null);
        Storeable sample4 = getSampleStoreable();
        sample4.setColumnAt(2, null);
        table.put("a", sample1);
        table.put("a", sample2);
        table.put("b", sample1);
        table.put("c", sample3);
        table.remove("b");
        table.put("d", sample4);
        Assert.assertTrue(table.size() == 3);
    }

    @Test
    public void removeDeletesKeyFromTable() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Storeable sample1 = getSampleStoreable();
        table.put("test1", sample1);
        table.remove("test1");
        Assert.assertTrue(table.get("test1") == null);
    }

    @Test
    public void twoTypesOfStoreable() throws  IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Storeable sample1 = getSampleStoreable();
        Storeable sample = new FixedListTwo(getColumnTypeList());
        sample.setColumnAt(0, 1);
        sample.setColumnAt(1, "hello");
        sample.setColumnAt(2, Byte.valueOf((byte) 2));
        table.put("a", sample1);
        table.put("b", sample);
        Assert.assertTrue(table.commit() == 2);
        table.put("a", sample);
        table.put("b", sample1);
        Assert.assertTrue(table.commit() == 2);
        table.remove("b");
        Assert.assertEquals(table.get("a"), sample);
        table.remove("a");
        Assert.assertTrue(table.commit() == 2);
        table.put("a", sample);
        table.commit();
        table.put("a", sample);
        Assert.assertTrue(table.commit() == 0);
    }

    @Test
    public void storeablePuttingWrongTypesShouldFail() {
        Storeable sample = new FixedList(getColumnTypeList());
        sample.setColumnAt(0, new Integer(3));
        sample.setColumnAt(1, "test");
        sample.setColumnAt(2, new Byte((byte) 4));
        sample.setColumnAt(2, null);
        boolean ok = false;
        try {
            sample.setColumnAt(0, new Float(1.2));
        } catch (ColumnFormatException e) {
            ok = true;
        }
        if (!ok) {
            Assert.fail("Exception expected none found");
        }
        ok = false;
        try {
            sample.setColumnAt(1, new HashMap<String, String>());
        } catch (ColumnFormatException e) {
            ok = true;
        }
        if (!ok) {
            Assert.fail("Exception expected none found");
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void tooBidIndexesInStoreableShouldFail() {
        Storeable sample = getSampleStoreable();
        sample.getColumnAt(10);
    }

    @Test(expected = ColumnFormatException.class)
    public void putWrongColumnsStoreableShouldFail() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        ArrayList<Class<?>> columnTypes = getColumnTypeList();
        columnTypes.set(0, Float.class);
        Storeable sample = new FixedList(columnTypes);
        sample.setColumnAt(0, new Float(1.1));
        table.put("a", sample);
    }

    @Test
    public void serializeDeserializeReturnsSameObject() throws IOException, ParseException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Storeable sample = getSampleStoreable();
        Assert.assertEquals(provider.deserialize(table, provider.serialize(table, sample)), sample);
    }

    @Test(expected = RuntimeException.class)
    public void deserializeInvalidJSONShouldFail() throws IOException, ParseException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Storeable sample = provider.deserialize(table, "abracadabra");
    }

    @Test(expected = ParseException.class)
    public void deserializeWrongTypesShouldFail() throws IOException, ParseException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Storeable sample = provider.deserialize(table, "[1.1, 1, 3]");
    }

    @Test
    public void serializeReturnsJSON() throws IOException, ParseException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        Storeable sample = getSampleStoreable();
        Assert.assertEquals(provider.serialize(table, sample), "[1,\"hello\",2]");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void createForArrayMisMatchShouldFail() throws IOException {
        File testFolder = new File(folder.getRoot(), "test");
        testFolder.mkdir();
        FileMapProviderFactory factory = new FileMapProviderFactory();
        FileMapProvider provider = factory.create(testFolder.getCanonicalPath());
        MultiFileMap table = provider.createTable("new", getColumnTypeList());
        provider.createFor(table, new ArrayList<Object>(4));
    }
}


class FixedListTwo implements Storeable {
    public static final Class<?>[] CLASSES = new Class<?>[] {
            Integer.class,
            Long.class,
            Byte.class,
            Float.class,
            Double.class,
            Boolean.class,
            String.class
    };

    final ArrayList<Class<?>> columnTypes;
    Object[] objects;

    public FixedListTwo(List<Class<?>> columnTypes) {
        this.columnTypes = new ArrayList<Class<?>>(columnTypes);
        objects = new Object[columnTypes.size()];
    }

    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d", objects.length, columnIndex));
        }
        if (value != null && value.getClass() != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found", columnTypes.get(columnIndex).toString(), value.getClass().toString()));
        }
        objects[columnIndex] = value;
    }

    public ArrayList<Class<?>> getColumnTypes() {
        return new ArrayList<>(columnTypes);
    }

    public int size() {
        return objects.length;
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d", objects.length, columnIndex));
        }
        return objects[columnIndex];
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d", objects.length, columnIndex));
        }
        if (Integer.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found", columnTypes.get(columnIndex).toString(), Integer.class.toString()));
        }
        return (Integer) objects[columnIndex];
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d", objects.length, columnIndex));
        }
        if (Long.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found", columnTypes.get(columnIndex).toString(), Long.class.toString()));
        }
        return (Long) objects[columnIndex];
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d", objects.length, columnIndex));
        }
        if (Byte.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found", columnTypes.get(columnIndex).toString(), Byte.class.toString()));
        }
        return (Byte) objects[columnIndex];
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d", objects.length, columnIndex));
        }
        if (Float.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found", columnTypes.get(columnIndex).toString(), Float.class.toString()));
        }
        return (Float) objects[columnIndex];
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d", objects.length, columnIndex));
        }
        if (Double.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found", columnTypes.get(columnIndex).toString(), Double.class.toString()));
        }
        return (Double) objects[columnIndex];
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d", objects.length, columnIndex));
        }
        if (Boolean.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found", columnTypes.get(columnIndex).toString(), Boolean.class.toString()));
        }
        return (Boolean) objects[columnIndex];
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex >= objects.length || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d", objects.length, columnIndex));
        }
        if (String.class != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found", columnTypes.get(columnIndex).toString(), String.class.toString()));
        }
        return (String) objects[columnIndex];
    }
}

