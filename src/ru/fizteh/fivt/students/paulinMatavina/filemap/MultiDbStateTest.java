package ru.fizteh.fivt.students.paulinMatavina.filemap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.strings.*;

public class MultiDbStateTest {
    public static TemporaryFolder rootFolder = new TemporaryFolder();  
    @Rule
    public ExpectedException thrown = ExpectedException.none();  
    static Table table;
    static TableProvider provider;
    static TableProviderFactory factory;
    static File root;
    
    @BeforeClass
    public static void tempFolder() {
        try {
            rootFolder.create();
            root = rootFolder.newFolder("root");
        } catch (IOException e) {
            fail("unable to create temporary folder");
        }
    }  
    
    @Before
    public void initialization() {
        factory = new MyTableProviderFactory();
        provider = factory.create(root.getAbsolutePath());
        table = provider.createTable("default");
    }  
    
    //tests for TableProviderFactory 
    @Test
    public void testFactoryCreateWrong() {
        thrown.expect(IllegalArgumentException.class);
        provider = (MyTableProvider) factory.create("wrong-path");
    }   
    @Test
    public void testFactoryCreateNull() {
        thrown.expect(IllegalArgumentException.class);
        provider = (MyTableProvider) factory.create(null);
    }   
    //end of tests for TableProviderFactory
    
    //tests for TableProvider
    @Test
    public void testProviderGetTableNull() {
        thrown.expect(IllegalArgumentException.class);
        provider.getTable(null);
    } 
    @Test
    public void testProviderGetTableNotExisting() {
        table = (MultiDbState) provider.getTable("not-existing-table");
        assertNull("not null on not existing table", table);
    }  
    @Test
    public void testProviderGetTableIncorrect() {
        thrown.expect(RuntimeException.class);
        table = (MultiDbState) provider.getTable("..");
    } 
    @Test
    public void testProviderCreateTableNull() {
        thrown.expect(IllegalArgumentException.class);
        provider.createTable(null);
    } 
    
    @Test
    public void testProviderCreateTableExisting() {
        table = (MultiDbState) provider.createTable("default");
        assertNull(table);
    } 
    
    @Test
    public void testProviderCreateRemoveTableOk() {
        table = (MultiDbState) provider.createTable("myLittlePony");
        assertNotNull(table);
        provider.removeTable("myLittlePony");
    } 
    
    @Test
    public void testProviderRemoveTableNotExisting() {
        thrown.expect(IllegalStateException.class);
        provider.removeTable("myLittlePony");
    } 
    
    @Test
    public void testProviderCreateRemovePut() {
        table = (MultiDbState) provider.createTable("myLittleTable");
        assertNotNull(table);
        provider.removeTable("myLittleTable");
        table.put("put", "to dropped");
    } 
    
    @Test
    public void testProviderNewNameWrong() {
        thrown.expect(RuntimeException.class);
        provider.createTable("nam/e");
    }
    
    @Test
    public void testProviderNewNameNull() {
        thrown.expect(IllegalArgumentException.class);
        provider.createTable(null);
    }
    
    @Test
    public void testProviderNewNameEmpty() {
        thrown.expect(IllegalArgumentException.class);
        provider.createTable("      ");
    }
    //end of tests for TableProvider
    
    //tests for Table            
    @Test
    public void testTablePutNullKey() {
        thrown.expect(IllegalArgumentException.class);
        table.put(null, "1");
    }
    
    @Test
    public void testTablePutNullValue() {
        thrown.expect(IllegalArgumentException.class);
        table.put("abcd", null);
    }
    
    @Test
    public void testTablePutNotNullValue() {
        table.put("abcd", "1");
    }

    @Test
    public void testTableGetNullValue() {
        thrown.expect(IllegalArgumentException.class);
        table.put("abcd", null);
    } 
    
    @Test
    public void testTableGetRemoveRollback() {
        table.put("a", "b");
        table.commit();
        table.remove("a");
        assertEquals(1, table.rollback());
        assertEquals(1, table.size());
    }
    
    @Test
    public void testTablePutRollbackGet() {
        table.put("newK", "value");
        table.rollback();
        assertNull(table.get("newK"));
    }
    
    @Test
    public void testTablePutCommitGet() {
        table.put("newK", "value");
        table.commit();
        assertEquals(table.get("newK"), "value");
        table.remove("newK");
        assertEquals(1, table.commit());
    }
    
    @Test
    public void testTableNewCommit() {
        Table t = provider.createTable("newT");
        assertNotNull(t);
        assertEquals(0, t.commit());
        provider.removeTable("newT");
    }
    
    @Test
    public void testTableNewRollback() {
        Table t = provider.createTable("newT");
        assertNotNull(t);
        assertEquals(0, t.rollback());
        provider.removeTable("newT");
    }
    
    @Test
    public void testTableNewGet() {
        Table t = provider.createTable("newT");
        assertNull(t.get("a"));
        provider.removeTable("newT");
    }
    
    @Test
    public void testTableNewNameOk() {
        Table t = provider.createTable("name");
        assertEquals("name", t.getName());
        provider.removeTable("name");
    }    
    
    @After
    public void after() {
        provider.removeTable("default");
    }
}
