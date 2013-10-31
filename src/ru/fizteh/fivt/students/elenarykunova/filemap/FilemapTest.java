package ru.fizteh.fivt.students.elenarykunova.filemap;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.students.elenarykunova.shell.Shell;

public class FilemapTest {

    private String root;
    private Filemap table;
    
    @Before
    public void prepare() {
        root = System.getProperty("user.dir");
        FileMapMain factory = new FileMapMain();
        MyTableProvider prov = (MyTableProvider) factory.create(root);
        File newFile = new File(root + File.separator + "newTable");
        if (newFile.exists()) {
            Shell sh = new Shell();
            sh.rm(newFile.getAbsolutePath());
        }
        table = (Filemap) prov.createTable("newTable");
    }
    
    @Test
    public void testGetName() {
        assertNotNull(table.getName());
        assertEquals(table.getName(), "newTable");
    }

    @Test
    public void testGet() {
        fail("Not yet implemented");
    }

    @Test
    public void testPut() {
        fail("Not yet implemented");
    }

    @Test
    public void testRemove() {
        fail("Not yet implemented");
    }

    @Test
    public void testSize() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetUncommitedChangesAndTrack() {
        fail("Not yet implemented");
    }

    @Test
    public void testCommit() {
        fail("Not yet implemented");
    }

    @Test
    public void testRollback() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetNameToNull() {
        fail("Not yet implemented");
    }

    @Test
    public void testSaveChanges() {
        fail("Not yet implemented");
    }

    @Test
    public void testLoad() {
        fail("Not yet implemented");
    }

    @Test
    public void testFilemap() {
        fail("Not yet implemented");
    }

    @Test
    public void testFilemapStringString() {
        fail("Not yet implemented");
    }

}
