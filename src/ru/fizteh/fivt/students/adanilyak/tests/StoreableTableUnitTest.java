package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;
import ru.fizteh.fivt.students.adanilyak.tools.StoreableCmdParseAndExecute;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithStoreableDataBase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Alexander
 * Date: 05.11.13
 * Time: 1:42
 */
public class StoreableTableUnitTest {
    TableProvider tableProvider;
    Table testTableEng;
    Table testTableRus;
    File sandBoxDirectory = new File("/Users/Alexander/Documents/JavaDataBase/Tests");

    @Before
    public void setUpTestObject() throws IOException {
        tableProvider = new StoreableTableProvider(sandBoxDirectory);

        List<Class<?>> typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        List<Class<?>> typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");

        testTableEng = tableProvider.createTable("testTable20", typesTestListOne);
        testTableRus = tableProvider.createTable("тестоваяТаблица21", typesTestListTwo);
    }

    @After
    public void tearDownTestObject() throws IOException {
        tableProvider.removeTable("testTable20");
        tableProvider.removeTable("тестоваяТаблица21");
        DeleteDirectory.rm(sandBoxDirectory);
    }

    /**
     * TEST BLOCK
     * GET NAME TESTS
     */

    @Test
    public void getNameTest() {
        Assert.assertEquals("testTable20", testTableEng.getName());
        Assert.assertEquals("тестоваяТаблица21", testTableRus.getName());
    }

    /**
     * TEST BLOCK
     * GET TESTS
     */

    @Test
    public void getTest() throws IOException {
        testTableEng.put("key", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2, 3", testTableEng, tableProvider));
        Assert.assertEquals("[1, 2, 3]", StoreableCmdParseAndExecute.outPutToUser(testTableEng.get("key"), testTableEng, tableProvider));
        Assert.assertNull(testTableEng.get("nonExictingKey"));
        testTableEng.remove("key");

        testTableRus.put("ключ", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2.043, true, \"Hello World!\"", testTableRus, tableProvider));
        Assert.assertEquals("[1, 2.043, true, \"Hello World!\"]", StoreableCmdParseAndExecute.outPutToUser(testTableRus.get("ключ"), testTableRus, tableProvider));
        Assert.assertNull(testTableRus.get("несуществующийКлюч"));
        testTableRus.remove("ключ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTest() {
        testTableEng.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptyTest() {
        testTableEng.get("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNlTest() {
        testTableEng.get("    ");
    }

    /**
     * TEST BLOCK
     * PUT TESTS
     */

    @Test
    public void putTest() throws IOException {
        Assert.assertNull(testTableEng.put("key", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2, 3", testTableEng, tableProvider)));
        Assert.assertEquals("[1, 2, 3]", StoreableCmdParseAndExecute.outPutToUser(testTableEng.put("key", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2, 3", testTableEng, tableProvider)), testTableEng, tableProvider));
        Assert.assertEquals("[1, 2, 3]", StoreableCmdParseAndExecute.outPutToUser(testTableEng.put("key", StoreableCmdParseAndExecute.putStringIntoStoreable("-1, -2, -3", testTableEng, tableProvider)), testTableEng, tableProvider));
        testTableEng.remove("key");

        Assert.assertNull(testTableRus.put("ключ", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2.043, true, \"Hello World!\"", testTableRus, tableProvider)));
        Assert.assertEquals("[1, 2.043, true, \"Hello World!\"]", StoreableCmdParseAndExecute.outPutToUser(testTableRus.put("ключ", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2.043, true, \"Hello World!\"", testTableRus, tableProvider)), testTableRus, tableProvider));
        Assert.assertEquals("[1, 2.043, true, \"Hello World!\"]", StoreableCmdParseAndExecute.outPutToUser(testTableRus.put("ключ", StoreableCmdParseAndExecute.putStringIntoStoreable("-1, -2.043, false, \"Bye Bye World!\"", testTableRus, tableProvider)), testTableRus, tableProvider));
        testTableRus.remove("ключ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKeyTest() throws IOException {
        testTableEng.put(null, testTableEng.put("key", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2, 3", testTableEng, tableProvider)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullValueTest() {
        testTableEng.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullBothTest() {
        testTableEng.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putKeyWithWhitespaces1Test() {
        List<Object> valuesToPut = new ArrayList<>();
        valuesToPut.add(1);
        valuesToPut.add(2);
        valuesToPut.add(3);
        Storeable needToPut = tableProvider.createFor(testTableEng, valuesToPut);
        testTableEng.put("key with whitespaces", needToPut);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putKeyWithWhitespaces2Test() {
        List<Object> valuesToPut = new ArrayList<>();
        valuesToPut.add(1);
        valuesToPut.add(2);
        valuesToPut.add(3);
        Storeable needToPut = tableProvider.createFor(testTableEng, valuesToPut);
        testTableEng.put(" key ", needToPut);
    }

    /**
     * TEST BLOCK
     * REMOVE TESTS
     */

    @Test
    public void removeTest() throws IOException {
        testTableEng.put("key", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2, 3", testTableEng, tableProvider));
        Assert.assertNull(testTableEng.remove("nonExictingKey"));
        Assert.assertEquals("[1, 2, 3]", StoreableCmdParseAndExecute.outPutToUser(testTableEng.remove("key"), testTableEng, tableProvider));

        testTableRus.put("ключ", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2.043, true, \"Hello World!\"", testTableRus, tableProvider));
        Assert.assertNull(testTableRus.remove("несуществующийКлюч"));
        Assert.assertEquals("[1, 2.043, true, \"Hello World!\"]", StoreableCmdParseAndExecute.outPutToUser(testTableRus.remove("ключ"), testTableRus, tableProvider));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTest() {
        testTableEng.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeEmptyTest() {
        testTableEng.remove("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNlTest() {
        testTableEng.remove("    ");
    }

    /**
     * TEST BLOCK
     * SIZE TESTS
     */

    @Test
    public void sizeTest() throws IOException {
        Assert.assertEquals(0, testTableEng.size());
        testTableEng.put("key1", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2, 3", testTableEng, tableProvider));
        Assert.assertEquals(1, testTableEng.size());
        testTableEng.put("key2", StoreableCmdParseAndExecute.putStringIntoStoreable("4, 5, 6", testTableEng, tableProvider));
        testTableEng.put("key3", StoreableCmdParseAndExecute.putStringIntoStoreable("7, 8, 9", testTableEng, tableProvider));
        Assert.assertEquals(3, testTableEng.size());
        testTableEng.put("key4", StoreableCmdParseAndExecute.putStringIntoStoreable("10, 11, 12", testTableEng, tableProvider));
        testTableEng.put("key5", StoreableCmdParseAndExecute.putStringIntoStoreable("13, 14, 15", testTableEng, tableProvider));
        Assert.assertEquals(5, testTableEng.size());
        testTableEng.commit();
        Assert.assertEquals(5, testTableEng.size());

        for (int i = 1; i <= 5; ++i) {
            testTableEng.remove("key" + i);
        }
    }

    /**
     * TEST BLOCK
     * COMMIT TESTS
     */

    @Test
    public void commitTest() throws IOException {
        Assert.assertEquals(0, testTableEng.commit());
        for (int i = 1; i <= 5; ++i) {
            testTableEng.put("key" + i, StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2, 3" + i, testTableEng, tableProvider));
        }
        Assert.assertEquals(5, testTableEng.commit());
        for (int i = 1; i <= 5; ++i) {
            testTableEng.remove("key" + i);
        }
    }

    @Test
    public void commitHardTest() throws IOException {
        testTableEng.put("key1", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2, 3", testTableEng, tableProvider));
        testTableEng.put("key2", StoreableCmdParseAndExecute.putStringIntoStoreable("4, 5, 6", testTableEng, tableProvider));
        Assert.assertEquals(2, testTableEng.commit());
        testTableEng.put("key1", StoreableCmdParseAndExecute.putStringIntoStoreable("7, 8, 9", testTableEng, tableProvider));
        testTableEng.put("key3", StoreableCmdParseAndExecute.putStringIntoStoreable("10, 11, 12", testTableEng, tableProvider));
        testTableEng.remove("key2");
        Assert.assertEquals(3, testTableEng.commit());
        testTableEng.remove("key1");
        testTableEng.remove("key3");
    }

    /**
     * TEST BLOCK
     * ROLLBACK TESTS
     */

    @Test
    public void rollbackTest() throws IOException {
        Assert.assertEquals(0, testTableEng.rollback());
        for (int i = 1; i <= 5; ++i) {
            testTableEng.put("key" + i, StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2, 3" + i, testTableEng, tableProvider));
        }
        testTableEng.commit();
        testTableEng.put("key2", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2, 0", testTableEng, tableProvider));
        testTableEng.put("key4", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2, 1", testTableEng, tableProvider));
        Assert.assertEquals(2, testTableEng.rollback());
        Assert.assertEquals("[1, 2, 32]", StoreableCmdParseAndExecute.outPutToUser(testTableEng.get("key2"), testTableEng, tableProvider));
        Assert.assertEquals("[1, 2, 34]", StoreableCmdParseAndExecute.outPutToUser(testTableEng.get("key4"), testTableEng, tableProvider));
        for (int i = 1; i <= 5; ++i) {
            testTableEng.remove("key" + i);
        }
    }

    @Test
    public void rollbackHardTest() throws IOException {
        testTableEng.put("key1", StoreableCmdParseAndExecute.putStringIntoStoreable("1, 2, 3", testTableEng, tableProvider));
        testTableEng.put("key2", StoreableCmdParseAndExecute.putStringIntoStoreable("4, 5, 6" , testTableEng, tableProvider));
        Assert.assertEquals(2, testTableEng.commit());
        testTableEng.remove("key2");
        testTableEng.put("key2", StoreableCmdParseAndExecute.putStringIntoStoreable("4, 5, 6" , testTableEng, tableProvider));
        Assert.assertEquals(0, testTableEng.rollback());
        testTableEng.remove("key1");
        testTableEng.remove("key2");
    }

    /**
     * TEST BLOCK
     * GET COLUMNS COUNT TESTS
     */

    @Test
    public void getColumnsCountTest() {
        Assert.assertEquals(3, testTableEng.getColumnsCount());
        Assert.assertEquals(4, testTableRus.getColumnsCount());
    }

    /**
     * TEST BLOCK
     * GET COLUMNS TYPE TESTS
     */

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnsTypeBadIndex1Test() {
        testTableEng.getColumnType(4);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnsTypeBadIndex2Test() {
        testTableRus.getColumnType(5);
    }

    @Test
    public void getColumnsType1Test() {
        Assert.assertEquals(Integer.class, testTableEng.getColumnType(0));
    }

    @Test
    public void getColumnsType2Test() {
        Assert.assertEquals(Boolean.class, testTableRus.getColumnType(2));
    }
}
