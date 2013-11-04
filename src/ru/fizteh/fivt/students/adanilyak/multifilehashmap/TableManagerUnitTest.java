package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        Assert.assertNotNull(testManager.createTable("testTable1"));
        Assert.assertNull(testManager.createTable("testTable1"));

        Assert.assertNotNull(testManager.createTable("тестоваяТаблица2"));
        Assert.assertNull(testManager.createTable("тестоваяТаблица2"));

        testManager.removeTable("testTable1");
        testManager.removeTable("тестоваяТаблица2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableTest() {
        testManager.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmptyTableTest() {
        testManager.createTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNlTableTest() {
        testManager.createTable("    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBadNameTableTest() {
        testManager.createTable("not_normal-name@for$table^!");
    }

    /**
     * TEST BLOCK
     * GET TABLE TESTS
     */

    @Test
    public void getTableTest() {
        Assert.assertNull(testManager.getTable("testNonExictingTable3"));
        Assert.assertNull(testManager.getTable("тестоваяНесуществующаяТаблица4"));

        testManager.createTable("testTable5");
        Assert.assertNotNull(testManager.getTable("testTable5"));

        testManager.createTable("тестоваяТаблица6");
        Assert.assertNotNull(testManager.getTable("тестоваяТаблица6"));

        testManager.removeTable("testTable5");
        testManager.removeTable("тестоваяТаблица6");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTableTest() {
        testManager.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptyTableTest() {
        testManager.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNlTableTest() {
        testManager.getTable("    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBadNameTableTest() {
        testManager.getTable("not_normal-name@for$table^!");
    }

    /**
     * TEST BLOCK
     * REMOVE TABLE TESTS
     */

    @Test
    public void removeTableTest() {
        testManager.createTable("testTable7");
        testManager.createTable("тестоваяТаблица8");

        testManager.removeTable("testTable7");
        Assert.assertNull(testManager.getTable("testTable7"));

        testManager.removeTable("тестоваяТаблица8");
        Assert.assertNull(testManager.getTable("тестоваяТаблица8"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTableTest() {
        testManager.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeEmptyTableTest() {
        testManager.removeTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNlTableTest() {
        testManager.removeTable("    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeBadNameTableTest() {
        testManager.removeTable("not_normal-name@for$table^!");
    }

    @Test(expected = IllegalStateException.class)
    public void removeNonExictingTable() {
        testManager.removeTable("testNonExictingTable11");
    }
}
