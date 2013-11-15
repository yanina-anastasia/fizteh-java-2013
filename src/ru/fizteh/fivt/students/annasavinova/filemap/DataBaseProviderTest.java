package ru.fizteh.fivt.students.annasavinova.filemap;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DataBaseProviderTest {
    DataBaseProvider test;
    ArrayList<Class<?>> list;

    @Rule
    public TemporaryFolder root = new TemporaryFolder();

    @Before
    public void initialize() {
        try {
            DBaseProviderFactory fact = new DBaseProviderFactory();
            test = (DataBaseProvider) fact.create(root.newFolder().toString());
            list = new ArrayList<>();
            list.add(int.class);
            list.add(String.class);
            test.createTable("testBase1", list);
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            // not OK
        }
    }

    @Test
    public void testCheckTableName() {
        try {
            assertFalse(test.checkTableName(".qwer"));
            assertFalse(test.checkTableName("..qwer"));
            assertFalse(test.checkTableName("/qwer"));
            assertFalse(test.checkTableName("/"));
            assertFalse(test.checkTableName("\\qw"));
            assertFalse(test.checkTableName(";qwer"));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() {
        test.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableEmpty() {
        test.getTable("");
    }

    @Test
    public void testGetTable() {
        try {
            assertNull(test.getTable("not_existing_table"));
            assertNotNull(test.getTable("testBase1"));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() {
        try {
            test.createTable(null, list);
        } catch (IOException e) {
            fail("Unexpected exception");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableEmpty() {
        try {
            test.createTable("", list);
        } catch (IOException e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testCreateTable() {
        try {
            assertNotNull(test.createTable("tmpTable", list));
            assertNull(test.createTable("tmpTable", list));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() {
        try {
            test.removeTable(null);
        } catch (IOException e) {
            fail("Unexpected exception");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableEmpty() {
        try {
            test.removeTable("");
        } catch (IOException e) {
            fail("Unexpected exception");
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableNotExists() {
        try {
            test.removeTable("not_existing_table");
        } catch (IOException e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testRemoveTable() {
        try {
            test.createTable("table_for_removing", list);
            test.removeTable("table_for_removing");
            assertNull(test.getTable("table_for_removing"));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }
}
