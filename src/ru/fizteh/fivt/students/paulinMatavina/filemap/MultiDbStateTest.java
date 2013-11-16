package ru.fizteh.fivt.students.paulinMatavina.filemap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.storage.structured.*;

public class MultiDbStateTest {
    @Rule
    public TemporaryFolder rootFolder = new TemporaryFolder();  
    Table table;
    TableProvider provider;
    MyTableProviderFactory factory;
    File root;
    ArrayList<Class<?>> list;
    ArrayList<Object> correct;
    ArrayList<Object> wrong;
    Storeable correctValues;
    Storeable wrongValues;
    
    @Before
    @Test
    public void initialization() throws IOException {
        rootFolder.create();
        root = rootFolder.newFolder("root");
        factory = new MyTableProviderFactory();
        provider = factory.create(root.getAbsolutePath());
        list = new ArrayList<Class<?>>();
        list.add(String.class);
        list.add(Integer.class);
        list.add(Long.class);
        list.add(Boolean.class);
        list.add(Float.class);
        
        table = provider.createTable("default", list);
        assertNotNull(table);
        wrongValues = provider.createFor(table);
        correct = new ArrayList<Object>();
        correct.add("String");
        correct.add(1000000000);
        correct.add(1000000000);
        correct.add(true);
        correct.add(1.001);
        correctValues = provider.createFor(table, correct);
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
        try {
            provider.createTable(null, list);
        } catch (IOException e) {
            fail();
        }
    } 
    
    @Test
    public void testProviderCreateTableExisting() {
        try {
            table = provider.createTable("default", list);
        } catch (IOException e) {
            fail();
        }
        assertNull(table);
    } 
    
    @Test
    public void testProviderCreateRemoveTableOk() {
        try {
            provider.createTable("myLittlePony", list);
        } catch (IOException e) {
            fail();
        }
        assertNotNull(table);
        try {
            provider.removeTable("myLittlePony");
        } catch (IOException e) {
            fail();
        }
    } 
    
    @Test(expected = IllegalStateException.class)
    public void testProviderRemoveTableNotExisting() {
        try {
            provider.removeTable("myLittlePony");
        } catch (IOException e) {
            fail();
        }
    } 
    
    @Test(expected = IllegalStateException.class)
    public void testProviderCreateRemovePut() {
        try {
            table = provider.createTable("myLittleTable", list);
        } catch (IOException e) {
            fail();
        }
        assertNotNull(table);
        try {
            provider.removeTable("myLittleTable");
        } catch (IOException e) {
            fail();
        }
        table.put("put", provider.createFor(table, correct));
    } 
    
    @Test(expected = RuntimeException.class)
    public void testProviderNewNameWrong() {
        try {
            table = provider.createTable("nam//e", list);
        } catch (IOException e) {
            fail();
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testProviderNewNameEmpty() {
        try {
            provider.createTable("      ", list);
        } catch (IOException e) {
            fail();
        }
    }
    //end of tests for TableProvider
    
    //tests for Table            
    @Test(expected = IllegalArgumentException.class)
    public void testTablePutNullKey() {
        table.put(null, correctValues);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTablePutNullValue() {
        table.put("abcd", null);
    }
    
    @Test
    public void testTablePutNotNullValue() {
        table.put("abcd", correctValues);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTableGetNullValue() {
        table.put("abcd", null);
    } 
    
    @Test
    public void testTableGetRemoveRollback() {
        table.put("a", correctValues);
        try {
            table.commit();
        } catch (IOException e) {
            fail();
        }
        table.remove("a");
        assertEquals(1, table.rollback());
        assertEquals(1, table.size());
    }
    
    @Test
    public void testTablePutRollbackGet() {
        table.put("newK", correctValues);
        table.rollback();
        assertNull(table.get("newK"));
    }
    
    @Test
    public void testTablePutCommitGet() {
        table.put("newK", correctValues);
        try {
            table.commit();
        } catch (IOException e) {
            fail();
        }
        assertEquals(table.get("newK"), correctValues);
        table.remove("newK");
        int result = 0;
        try {
            result = table.commit();
        } catch (IOException e) {
            fail();
        }
        assertEquals(1, result);
    }
    
    @Test
    public void testTableNewRollback() {
        try {
            table = provider.createTable("newT", list);
        } catch (IOException e) {
            fail();
        }
        assertNotNull(table);
        assertEquals(0, table.rollback());
        try {
            provider.removeTable("newT");
        } catch (IOException e) {
            fail();
        }
    }
    
    @Test
    public void testTableNewGet() {
        try {
            table = provider.createTable("newT", list);
        } catch (IOException e) {
            fail();
        }
        
        assertNull(table.get("a"));
        try {
            provider.removeTable("newT");
        } catch (IOException e) {
            fail();
        }
    }  
    
    @After
    public void after() {
        try {
            provider.removeTable("default");
        } catch (IOException e) {
            fail();
        }       
    }
}
