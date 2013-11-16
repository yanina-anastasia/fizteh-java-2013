package ru.fizteh.fivt.students.elenarykunova.filemap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.Table;

public class MyTableProviderTest {

    private File notExistingFile;
    private File existingFile;
    private File existingDir;
    private List<Class<?>> justList;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void prepare() {
        try {
            existingDir = folder.newFolder("existingDirPath");
        } catch (IOException e1) {
            System.err.println("can't make tests");
        }
        try {
            existingFile = folder.newFile("existingPath");
        } catch (IOException e1) {
            System.err.println("can't make tests");
        }
        notExistingFile = new File(existingDir.getParent() + File.separator
                + "notExistingPath");
        justList = new ArrayList<Class<?>>(3);
        justList.add(Integer.class);
        justList.add(String.class);
        justList.add(Long.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() {
        MyTableProvider prov = new MyTableProvider();
        prov.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableEmpty() {
        MyTableProvider prov = new MyTableProvider();
        prov.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNl() {
        MyTableProvider prov = new MyTableProvider();
        prov.getTable("     ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() throws IllegalArgumentException,
            RuntimeException, IOException {
        MyTableProvider prov = new MyTableProvider();
        prov.createTable(null, justList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableEmpty() throws IllegalArgumentException,
            RuntimeException, IOException {
        MyTableProvider prov = new MyTableProvider();
        prov.createTable("", justList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNl() throws IllegalArgumentException,
            RuntimeException, IOException {
        MyTableProvider prov = new MyTableProvider();
        prov.createTable("                  ", justList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() {
        MyTableProvider prov = new MyTableProvider();
        prov.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableEmpty() {
        MyTableProvider prov = new MyTableProvider();
        prov.removeTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNl() {
        MyTableProvider prov = new MyTableProvider();
        prov.removeTable("                   ");
    }

    @Test(expected = RuntimeException.class)
    public void testGetTableBadSymbol() {
        MyTableProvider prov = new MyTableProvider();
        prov.getTable(".aa.aa.");
    }

    @Test(expected = RuntimeException.class)
    public void testCreateTableBadSymbol() throws IllegalArgumentException,
            RuntimeException, IOException {
        MyTableProvider prov = new MyTableProvider();
        prov.createTable("/aaaa", justList);
    }

    @Test(expected = RuntimeException.class)
    public void testRemoveTableBadSymbol() {
        MyTableProvider prov = new MyTableProvider();
        prov.getTable("\\aa\\aa");
    }

    @Test
    public void testGetTable() {
        MyTableProvider prov = new MyTableProvider();
        try {
            prov.getTable("anyPath");
            fail("null RootDir :expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("null RootDir :expected RuntimeException");
        }
        prov = new MyTableProvider(existingDir.getParent());
        try {
            Table res = prov.getTable(notExistingFile.getName());
            assertNull(res);
        } catch (IllegalArgumentException e1) {
            fail("Everything is Ok!");
        } catch (RuntimeException e) {
            // ok, shit happens, fileMap could throw exception
        }

        if (existingFile.isFile()) {
            try {
                Table res2 = prov.getTable("existingPath");
                assertNull(res2);
            } catch (RuntimeException e) {
                // ok, shit happens, fileMap could throw exception
            }
        }
        if (existingDir.isDirectory()) {
            try {
                Table res2 = prov.getTable("existingDirPath");
                assertNotNull(res2);
            } catch (RuntimeException e) {
                // ok, shit happens, fileMap could throw exception
            }
        }
    }

    @Test
    public void testCreateTable() throws IllegalArgumentException,
            RuntimeException, IOException {
        MyTableProvider prov = new MyTableProvider();
        try {
            prov.getTable("anyPath");
            fail("null RootDir :expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("null RootDir :expected RuntimeException");
        }
        try {
            prov = new MyTableProvider(existingDir.getParent());
        } catch (Exception e1) {
            fail("RootDir is correct, shouldn't fail");
        }

        try {
            Table res = prov.createTable("notExistingPath", justList);
            assertNotNull(res);
            assertTrue(notExistingFile.exists()
                    && notExistingFile.isDirectory());
        } catch (RuntimeException e1) {
            // what a pity: couldn't create this file
        }

        if (existingFile.isFile()) {
            try {
                prov.createTable("existingPath", justList);
                fail("file with this name exists: shouldn't be able to create table");
            } catch (RuntimeException e1) {
                // ok
            } catch (Exception e2) {
                fail("file with this name exists: expected RuntimeException");
            }
        }
        if (existingDir.isDirectory()) {
            File infoFile = new File(existingDir, "signature.tsv");
            if (!infoFile.exists()) {
                try {
                    Table res2 = prov.createTable("existingDirPath", justList);
                    fail("expected IllegalArgumentException");
                } catch (IllegalArgumentException e) {
                    //
                }
            }
        }
    }

    @Test
    public void testRemoveTable() {
        MyTableProvider prov = new MyTableProvider();
        try {
            prov.getTable("anyPath");
            fail("null RootDir :expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("null RootDir :expected RuntimeException");
        }

        prov = new MyTableProvider(existingDir.getParent());

        try {
            prov.removeTable(notExistingFile.getName());
            fail("remove notExistingTable: expected IllegalStateException");
        } catch (IllegalStateException e1) {
            // ok
        } catch (Exception e2) {
            fail("remove notExistingTable: expected IllegalStateException");
        }

        if (existingFile.isFile()) {
            try {
                prov.removeTable("existingPath");
                fail("remove not table: expected IllegalStateException");
            } catch (IllegalStateException e1) {
                // ok
            } catch (Exception e2) {
                fail("remove not table: expected IllegalStateException");
            }
        }
        if (existingDir.isDirectory()) {
            try {
                prov.removeTable("existingDirPath");
                assertTrue(!existingDir.exists());
            } catch (RuntimeException e1) {
                // ok, can't delete, shit happens.
            }
        }
    }

    @Test
    public void testCreateGetRemove() throws IllegalArgumentException,
            RuntimeException, IOException {
        MyTableProvider prov = new MyTableProvider(existingDir.getParent());
        String name = "newTable";
        Table createRes = prov.createTable(name, justList);
        Table getRes = prov.getTable(name);
        if (createRes != getRes) {
            fail("table from get should be the same as from create");
        }
        Table createAgain = prov.createTable(name, justList);
        assertNull(createAgain);
        prov.removeTable(name);
        assertNull(prov.getTable(name));
    }
}
