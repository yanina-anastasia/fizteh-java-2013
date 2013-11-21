package ru.fizteh.fivt.students.paulinMatavina.filemap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.strings.*;

public class MultiDbStateTest {
    @Rule
    public TemporaryFolder rootFolder = new TemporaryFolder();  
    Table table;
    TableProvider provider;
    TableProviderFactory factory;
    File root;
    
    @Before
    public void initialization() throws IOException {
        rootFolder.create();
        root = rootFolder.newFolder("root");
        factory = new MyTableProviderFactory();
        provider = factory.create(root.getAbsolutePath());
        table = provider.createTable("default");
    }  
    
    //tests for TableProviderFactory 
    @Test(expected = IllegalArgumentException.class)
    public void testFactoryCreateWrong() {
        provider = factory.create("wrong-path");
    }  
    
    @Test(expected = IllegalArgumentException.class)
    public void testFactoryCreateNull() {
        provider = factory.create(null);
    }   
    //end of tests for TableProviderFactory
    
    //tests for TableProvider
    @Test(expected = IllegalArgumentException.class)
    public void testProviderGetTableNull() {
        provider.getTable(null);
    } 
    
    @Test
    public void testProviderGetTableNotExisting() {
        table = provider.getTable("not-existing-table");
        assertNull("not null on not existing table", table);
    }  
    
    @Test(expected = RuntimeException.class)
    public void testProviderGetTableIncorrect() {
        table = provider.getTable("..");
    } 
    
    @Test(expected = IllegalArgumentException.class)
    public void testProviderCreateTableNull() {
        provider.createTable(null);
    } 
    
    @Test
    public void testProviderCreateTableExisting() {
        table = provider.createTable("default");
        assertNull(table);
    } 
    
    @Test
    public void testProviderCreateRemoveTableOk() {
        table = provider.createTable("myLittlePony");
        assertNotNull(table);
        provider.removeTable("myLittlePony");
    } 
    
    @Test(expected = IllegalStateException.class)
    public void testProviderRemoveTableNotExisting() {
        provider.removeTable("myLittlePony");
    } 
    
    @Test
    public void testProviderCreateRemovePut() {
        table = provider.createTable("myLittleTable");
        assertNotNull(table);
        provider.removeTable("myLittleTable");
        table.put("put", "to dropped");
    } 
    
    @Test(expected = RuntimeException.class)
    public void testProviderNewNameWrong() {
        provider.createTable("nam/e");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testProviderNewNameNull() {
        provider.createTable(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testProviderNewNameEmpty() {
        provider.createTable("      ");
    }
    //end of tests for TableProvider
    
    //tests for Table            
    @Test(expected = IllegalArgumentException.class)
    public void testTablePutNullKey() {
        table.put(null, "1");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTablePutNullValue() {
        table.put("abcd", null);
    }
    
    @Test
    public void testTablePutNotNullValue() {
        table.put("abcd", "1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableGetNullValue() {
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
