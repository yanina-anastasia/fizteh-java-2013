package ru.fizteh.fivt.students.piakovenko.tests;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.11.13
 * Time: 22:07
 * To change this template use File | Settings | File Templates.
 */

import junit.framework.Assert;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.piakovenko.filemap.DataBasesCommander;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 27.10.13
 * Time: 20:01
 * To change this template use File | Settings | File Templates.
 */
public class TestForBasicInterface {



    @Test
    public void sizeTest ()  {
        DataBasesCommander tableProvider = new DataBasesCommander();
        Table testTable = tableProvider.createTable("test1");
        Assert.assertEquals("size should be zero", 0, testTable.size());
        testTable.put("one", "One");
        testTable.put("two", "Two");
        Assert.assertEquals("size should be equal 2", 2, testTable.size());
        testTable.remove("one");
        testTable.remove("two");
        Assert.assertEquals("size again should be zero", 0, testTable.size());
    }

    @Test
    public void persistenceTest() {
        DataBasesCommander tableProvider = new DataBasesCommander();
        Table testTable = tableProvider.createTable("test2");
        String one = "One";
        String two = "Two";
        String three = "Three";
        testTable.put("one", "One");
        testTable.put("two", "Two");
        org.junit.Assert.assertEquals("should return One", one, testTable.get("one"));
        org.junit.Assert.assertEquals("should return Two", two, testTable.get("two"));
        Assert.assertEquals("size should be two", 2, testTable.size());
        testTable.commit();
        testTable.put("three", "Three");
        org.junit.Assert.assertEquals("should return Three", three, testTable.get("three"));
        Assert.assertEquals("size should be three", 3, testTable.size());
        Assert.assertEquals("doing rollback. should return 1", 1, testTable.rollback());
        Assert.assertEquals("now size should be 2",2, testTable.size());
        Assert.assertNull("should return null", testTable.get("three"));
        org.junit.Assert.assertEquals("should return One", one, testTable.get("one"));
        org.junit.Assert.assertEquals("should return Two", two, testTable.get("two"));
    }

    @Test
    public void persistenceTest2 () {
        DataBasesCommander dbc  = new DataBasesCommander();
        Table temp = dbc.createTable("test3");
        String one = "One";
        String two = "Two";
        String three = "Three";
        temp.put("one", "One");
        temp.put("two", "Two");
        org.junit.Assert.assertEquals("should return One", one, temp.get("one"));
        org.junit.Assert.assertEquals("should return Two", two, temp.get("two"));
        Assert.assertEquals("size should be two", 2, temp.size());
        temp.put("three", "Three");
        dbc.createTable("test3");
        try {
            dbc.use("test4");
            dbc.use("test3");
        } catch (IOException e) {
            System.err.println("IOException was thrown!");
            System.exit(1);
        } catch (MyException e) {
            System.err.println("MyException thrown!");
            System.exit(1);
        }
        org.junit.Assert.assertEquals("should return Three", three, temp.get("three"));
        org.junit.Assert.assertEquals("should return One", one, temp.get("one"));
        org.junit.Assert.assertEquals("should return Two", two, temp.get("two"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void exceptionTest () {
        DataBasesCommander tableProvider = new DataBasesCommander();
        tableProvider.createTable(null);
    }


}

