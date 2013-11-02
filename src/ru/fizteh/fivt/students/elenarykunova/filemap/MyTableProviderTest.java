package ru.fizteh.fivt.students.elenarykunova.filemap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.strings.Table;

public class MyTableProviderTest {

    private File notExistingFile;
    private File existingFile;
    private File existingDir;
    
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
        notExistingFile = new File(existingDir.getParent() + File.separator + "notExistingPath");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testInizializeNull() {
        MyTableProvider prov = new MyTableProvider(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInizializeEmpty() {
        MyTableProvider prov = new MyTableProvider("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInizializeNl() {
        MyTableProvider prov = new MyTableProvider("                       ");
    }
        
    @Test
    public void testInizialize() {
        MyTableProvider prov;
        try {
            prov = new MyTableProvider(existingDir.getAbsolutePath());
        } catch (Exception e1) {
            fail("RootDir is correct, shouldn't fail");
        }
        
        try {
            prov = new MyTableProvider(notExistingFile.getAbsolutePath());
            assertTrue(notExistingFile.exists() && notExistingFile.isDirectory());
        } catch (IllegalArgumentException e1) {
            //ok
        } catch (Exception e2) {
            fail("inizialize notExistingDir: expected IllegalArgumentException");
        }
                
        if (existingFile.isFile()) {
            try {
                prov = new MyTableProvider(existingFile.getAbsolutePath());
                fail("inizialize TableProvider with file: expected IllegalArgumentException");
            } catch (IllegalArgumentException e1) {
                //ok
            } catch (Exception e2) {
                fail("inizialize TableProvider with file: expected IllegalArgumentException");
            }
        }
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
    public void testCreateTableNull() {
        MyTableProvider prov = new MyTableProvider();
        prov.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableEmpty() {
        MyTableProvider prov = new MyTableProvider();
        prov.createTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNl() {
        MyTableProvider prov = new MyTableProvider();
        prov.createTable("                  ");
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
        prov.createTable("                   ");
    }
    
    @Test(expected = RuntimeException.class)
    public void testGetTableBadSymbol() {
        MyTableProvider prov = new MyTableProvider();
        prov.getTable(".aa.aa.");
    }

    @Test(expected = RuntimeException.class)
    public void testCreateTableBadSymbol() {
        MyTableProvider prov = new MyTableProvider();
        prov.createTable("/aaaa");
    }

    @Test(expected = RuntimeException.class)
    public void testRemoveTableBadSymbol() {
        MyTableProvider prov = new MyTableProvider();
        prov.getTable("\\aa\\aa");
    }

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
        try {
            prov = new MyTableProvider(existingDir.getParent());
        } catch (Exception e1) {
            fail("RootDir is correct, shouldn't fail");
        }
        try {
            Table res = prov.getTable(notExistingFile.getName());
            assertNull(res);
        } catch (RuntimeException e) {
            //ok, shit happens, fileMap could throw exception
        }
                
        if (existingFile.isFile()) {
            try {
                Table res2 = prov.getTable("existingPath");
                assertNull(res2);
            } catch (RuntimeException e) {
                //ok, shit happens, fileMap could throw exception
            }
        }
        if (existingDir.isDirectory()) {
            try {
                Table res2 = prov.getTable("existingDirPath");
                assertNotNull(res2);            
            } catch (RuntimeException e) {
                //ok, shit happens, fileMap could throw exception
            }
        }
    }

    @Test
    public void testCreateTable() {
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
            Table res = prov.createTable("notExistingPath");
            assertNotNull(res);
            assertTrue(notExistingFile.exists() && notExistingFile.isDirectory());
        } catch (RuntimeException e1) {
            // what a pity: couldn't create this file
        }
                
        if (existingFile.isFile()) {
            try {
                prov.createTable("existingPath");
                fail("file with this name exists: shouldn't be able to create table");
            } catch (RuntimeException e1) {
                //ok
            } catch (Exception e2) {
                fail("file with this name exists: expected RuntimeException");
            }
        }
        if (existingDir.isDirectory()) {
            Table res2 = prov.createTable("existingDirPath");
            assertNull(res2);            
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
        try {
            prov = new MyTableProvider(existingDir.getParent());
        } catch (Exception e1) {
            fail("RootDir is correct, shouldn't fail");
        }
                        
        try {
            prov.removeTable(notExistingFile.getName());
            fail("remove notExistingTable: expected IllegalStateException");
        } catch (IllegalStateException e1) {
            //ok
        } catch (Exception e2) {
            fail("remove notExistingTable: expected IllegalStateException");
        }
                
        if (existingFile.isFile()) {
            try {
                prov.removeTable("existingPath");
                fail("remove not table: expected IllegalStateException");
            } catch (IllegalStateException e1) {
                //ok
            } catch (Exception e2) {
                fail("remove not table: expected IllegalStateException");
            }
        }
        if (existingDir.isDirectory()) {
            try {
                prov.removeTable("existingDirPath");
                assertTrue(!existingDir.exists());
            } catch (RuntimeException e1) {
                //ok, can't delete, shit happens.
            }   
        }
    }
    
    @Test
    public void testCreateGetRemove() {
        MyTableProvider prov = new MyTableProvider(existingDir.getParent());
        String name = "newTable";
        Table createRes = prov.createTable(name);
        Table getRes = prov.getTable(name);
        if (createRes != getRes) {
            fail("table from get should be the same as from create");
        }
        Table createAgain = prov.createTable(name);
        assertNull(createAgain);
        prov.removeTable(name);
        assertNull(prov.getTable(name));
    }
}
