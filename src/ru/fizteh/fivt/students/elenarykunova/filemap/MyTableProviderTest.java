package ru.fizteh.fivt.students.elenarykunova.filemap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.elenarykunova.shell.Shell;

public class MyTableProviderTest {

    private File notExistingFile;
    private File existingFile;
    private File existingDir;

    @Before
    public void prepare() {
        notExistingFile = new File(System.getProperty("user.dir") + File.separator + "notExistingPath");
        if (notExistingFile.exists()) {
            notExistingFile.delete();
        }
        existingFile = new File(System.getProperty("user.dir") + File.separator + "existingPath");
        if (!existingFile.exists()) {
            try {
                existingFile.createNewFile();
            } catch (IOException e) {
                //uups
            }
        }
        existingDir = new File(System.getProperty("user.dir") + File.separator + "existingDirPath");
        if (!existingDir.exists()) {
            existingDir.mkdir();
        }
    }
    
    @Test
    public void testMyTableProviderString() {
        MyTableProvider prov;
        try {
            prov = new MyTableProvider(null);
            fail("null: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("null: expected IllegalArgumentException");
        }
        try {
            prov = new MyTableProvider("");
            fail("empty: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("empty: expected IllegalArgumentException");
        }
        try {
            prov = new MyTableProvider("                ");
            fail("whitespaces: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("whitespaces: expected IllegalArgumentException");
        }
        
        try {
            prov = new MyTableProvider(System.getProperty("user.dir"));
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

    @Test
    public void testGetTable() {
        MyTableProvider prov = new MyTableProvider();
        try {
            prov.getTable(null);
            fail("null: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("null: expected IllegalArgumentException");
        }
        try {
            prov.getTable("");
            fail("empty: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("empty: expected IllegalArgumentException");
        }
        try {
            prov.getTable("                     ");
            fail("whitespaces: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("whitespaces: expected IllegalArgumentException");
        }
        try {
            prov.getTable(".aaa");
            fail("badsymbol '.' : expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("badsymbol '.' : expected RuntimeException");
        }
        try {
            prov.getTable("/aaa");
            fail("badsymbol '/' :expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("badsymbol '/' :expected RuntimeException");
        }
        try {
            prov.getTable("a\\aa");
            fail("badsymbol '\\' :expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("badsymbol '\\' :expected RuntimeException");
        }
        try {
            prov.getTable("anyPath");
            fail("null RootDir :expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("null RootDir :expected RuntimeException");
        }
        try {
            prov = new MyTableProvider(System.getProperty("user.dir"));
        } catch (Exception e1) {
            fail("RootDir is correct, shouldn't fail");
        }
        try {
            Table res = prov.getTable("notExistingPath");
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
            prov.createTable(null);
            fail("null: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("null: expected IllegalArgumentException");
        }
        try {
            prov.createTable("");
            fail("empty: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("empty: expected IllegalArgumentException");
        }
        try {
            prov.createTable("                     ");
            fail("whitespaces: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("whitespaces: expected IllegalArgumentException");
        }
        try {
            prov.createTable(".aaa");
            fail("badsymbol '.' : expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("badsymbol '.' : expected RuntimeException");
        }
        try {
            prov.createTable("/aaa");
            fail("badsymbol '/' :expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("badsymbol '/' :expected RuntimeException");
        }
        try {
            prov.createTable("a\\aa");
            fail("badsymbol '\\' :expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("badsymbol '\\' :expected RuntimeException");
        }
        try {
            prov.createTable("anyPath");
            fail("null RootDir :expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("null RootDir :expected RuntimeException");
        }
        
        try {
            prov = new MyTableProvider(System.getProperty("user.dir"));
        } catch (Exception e1) {
            fail("RootDir is correct, shouldn't fail");
        }
        
        Table res = prov.createTable("notExistingPath");
        assertNotNull(res);
        assertTrue(notExistingFile.exists() && notExistingFile.isDirectory());
                
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
            prov.removeTable(null);
            fail("null: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("null: expected IllegalArgumentException");
        }
        try {
            prov.removeTable("");
            fail("empty: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("empty: expected IllegalArgumentException");
        }
        try {
            prov.removeTable("                     ");
            fail("whitespaces: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e1) {
            fail("whitespaces: expected IllegalArgumentException");
        }
        try {
            prov.removeTable(".aaa");
            fail("badsymbol '.' : expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("badsymbol '.' : expected RuntimeException");
        }
        try {
            prov.removeTable("/aaa");
            fail("badsymbol '/' :expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("badsymbol '/' :expected RuntimeException");
        }
        try {
            prov.removeTable("a\\aa");
            fail("badsymbol '\\' :expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("badsymbol '\\' :expected RuntimeException");
        }
        try {
            prov.removeTable("anyPath");
            fail("null RootDir :expected RuntimeException");
        } catch (RuntimeException e) {
            // ok
        } catch (Exception e1) {
            fail("null RootDir :expected RuntimeException");
        }
        
        try {
            prov = new MyTableProvider(System.getProperty("user.dir"));
        } catch (Exception e1) {
            fail("RootDir is correct, shouldn't fail");
        }
        
        try {
            prov.removeTable("notExistingPath");
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
    
    @After
    public void clean() {
        existingFile.delete();
        if (existingDir.exists()) {
            Shell sh = new Shell();
            sh.rm(existingDir.getAbsolutePath());
        }
        notExistingFile.delete();
    }
}
