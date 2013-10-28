package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import org.junit.*;

import java.io.File;
import java.io.IOException;

/**
 * User: Alexander
 * Date: 28.10.13
 * Time: 0:11
 */
public class TableManagerUnitTest {
    TableManager testManager;

    @Before
    public void setUpTestObject() throws IOException {
        testManager = new TableManager(new File("/Users/Alexander/Documents/JavaDataBase/Tests"));
    }

    /**
     * TEST BLOCK
     * CREATE TABLE TESTS
     */

    @Test
    public void createTableTest() {
        Assert.assertNotNull(testManager.createTable("testTable#1"));
        Assert.assertNull(testManager.createTable("testTable#1"));

        Assert.assertNotNull(testManager.createTable("тестоваяТаблица#2"));
        Assert.assertNull(testManager.createTable("тестоваяТаблица#2"));

        testManager.removeTable("testTable#1");
        testManager.removeTable("тестоваяТаблица#2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableTest() {
        testManager.createTable(null);
    }

    /**
     * TEST BLOCK
     * GET TABLE TESTS
     */

    @Test
    public void getTableTest() {
        Assert.assertNull(testManager.getTable("testNonExictingTable#3"));
        Assert.assertNull(testManager.getTable("тестоваяНесуществующаяТаблица#4"));

        testManager.createTable("testTable#5");
        Assert.assertNotNull(testManager.getTable("testTable#5"));

        testManager.createTable("тестоваяТаблица#6");
        Assert.assertNotNull(testManager.getTable("тестоваяТаблица#6"));

        testManager.removeTable("testTable#5");
        testManager.removeTable("тестоваяТаблица#6");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTableTest() {
        testManager.getTable(null);
    }

    /**
     * TEST BLOCK
     * REMOVE TABLE TESTS
     */

    @Test
    public void removeTableTest() {
        testManager.createTable("testTable#7");
        testManager.createTable("тестоваяТаблица#8");

        testManager.removeTable("testTable#7");
        Assert.assertNull(testManager.getTable("testTable#7"));

        testManager.removeTable("тестоваяТаблица#8");
        Assert.assertNull(testManager.getTable("тестоваяТаблица#8"));

    }

}
