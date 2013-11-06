package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;
import ru.fizteh.fivt.students.adanilyak.tools.WorkWithStoreableDataBase;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * User: Alexander
 * Date: 05.11.13
 * Time: 1:42
 */
public class StoreableTableProviderUnitTest {
    StoreableTableProvider testProvider;
    File sandBoxDirectory = new File("/Users/Alexander/Documents/JavaDataBase/Tests");

    @Before
    public void setUpTestObject() throws IOException {
        testProvider = new StoreableTableProvider(sandBoxDirectory);
    }

    @After
    public void tearDownTestObject() throws IOException{
        DeleteDirectory.rm(sandBoxDirectory);
    }

    /**
     * TEST BLOCK
     * CREATE TABLE TESTS
     */

    @Test
    public void createTableTest() throws IOException {
        List<Class<?>> typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        List<Class<?>> typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");

        Assert.assertNotNull(testProvider.createTable("testTable1", typesTestListOne));
        Assert.assertNull(testProvider.createTable("testTable1", typesTestListOne));

        Assert.assertNotNull(testProvider.createTable("тестоваяТаблица2", typesTestListTwo));
        Assert.assertNull(testProvider.createTable("тестоваяТаблица2", typesTestListTwo));

        testProvider.removeTable("testTable1");
        testProvider.removeTable("тестоваяТаблица2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableNotNullListTest() throws IOException {
        List<Class<?>> typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        testProvider.createTable(null, typesTestListOne);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNotNullTableNullListTest() throws IOException {
        testProvider.createTable("testTable9", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableNullListTest() throws IOException{
        testProvider.createTable(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmptyTableTest() throws IOException {
        List<Class<?>> typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");
        testProvider.createTable("", typesTestListTwo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNlTableTest() throws  IOException {
        List<Class<?>> typesTestListThree = WorkWithStoreableDataBase.createListOfTypesFromString("String boolean");
        testProvider.createTable("    ", typesTestListThree);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBadNameTableTest() throws IOException {
        List<Class<?>> typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        testProvider.createTable("not_normal-name@for$table^!", typesTestListOne);
    }

    /**
     * TEST BLOCK
     * GET TABLE TESTS
     */

    @Test
    public void getTableTest() throws IOException {
        Assert.assertNull(testProvider.getTable("testNonExictingTable3"));
        Assert.assertNull(testProvider.getTable("тестоваяНесуществующаяТаблица4"));

        List<Class<?>> typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        List<Class<?>> typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");

        testProvider.createTable("testTable5", typesTestListOne);
        Assert.assertNotNull(testProvider.getTable("testTable5"));

        testProvider.createTable("тестоваяТаблица6", typesTestListTwo);
        Assert.assertNotNull(testProvider.getTable("тестоваяТаблица6"));

        testProvider.removeTable("testTable5");
        testProvider.removeTable("тестоваяТаблица6");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTableTest() {
        testProvider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptyTableTest() {
        testProvider.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNlTableTest() {
        testProvider.getTable("    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBadNameTableTest() {
        testProvider.getTable("not_normal-name@for$table^!");
    }

    /**
     * TEST BLOCK
     * REMOVE TABLE TESTS
     */

    @Test
    public void removeTableTest() throws  IOException {
        List<Class<?>> typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        List<Class<?>> typesTestListTwo = WorkWithStoreableDataBase.createListOfTypesFromString("int double boolean String");

        testProvider.createTable("testTable7", typesTestListOne);
        testProvider.createTable("тестоваяТаблица8",typesTestListTwo);

        testProvider.removeTable("testTable7");
        Assert.assertNull(testProvider.getTable("testTable7"));

        testProvider.removeTable("тестоваяТаблица8");
        Assert.assertNull(testProvider.getTable("тестоваяТаблица8"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTableTest() {
        testProvider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeEmptyTableTest() {
        testProvider.removeTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNlTableTest() {
        testProvider.removeTable("    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeBadNameTableTest() {
        testProvider.removeTable("not_normal-name@for$table^!");
    }

    @Test(expected = IllegalStateException.class)
    public void removeNonExcitingTable() {
        testProvider.removeTable("testNonExcitingTable11");
    }

    /**
     * TEST BLOCK
     * DESERIALIZE TESTS
     */
    /*
    @Test(expected = ParseException.class)
    public void deserializeNullStringTest() throws IOException {
        List<Class<?>> typesTestListOne = WorkWithStoreableDataBase.createListOfTypesFromString("int int int");
        testProvider.createTable("testTable10", typesTestListOne);
        testProvider.deserialize(testProvider.getTable("testTable10"), null);
    }
    */
    /**
     * TEST BLOCK
     * SERIALIZE TESTS
     */

    /**
     * TEST BLOCK
     * CREATE FOR (1) TESTS
     */

    /**
     * TEST BLOCK
     * CREATE FOR (2) TESTS
     */
}
