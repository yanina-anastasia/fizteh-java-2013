package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TestsForProxy {
    TableProviderFactory tableProviderFactory;
    TableProvider tableProvider;
    Table table;
    
    LoggingProxyFactory loggingProxyFactory;
    
    Storeable value1;
    
    StringWriter writer;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void createTable() throws IOException {
        writer = new StringWriter();
        loggingProxyFactory = new LoggingProxyFactoryImplementation();
        
        /*value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, true);
        value1.setColumnAt(1, "AA");
        value1.setColumnAt(2, 5);*/
    }
    
    @Test
    public void arrayListCyclic() {
        ArrayList array = new ArrayList();
        
        array.add(array);
        
        List wrappedList = (List) loggingProxyFactory.wrap(writer, array, List.class); 
        
        wrappedList.addAll(array);
        //ArrayList newList = new ArrayList(wrappedList);
        
        System.out.println(writer.toString());
    }
    
    /*@Test
    public void getName() throws IOException {
        
        File databaseDirectory = folder.newFolder("database");
        TableProviderFactoryImplementation tableProviderFactoryImplementation = new TableProviderFactoryImplementation();
        tableProviderFactory = (TableProviderFactory) loggingProxyFactory.wrap(writer, tableProviderFactoryImplementation, TableProviderFactory.class);
        TableProvider tableProviderImplementation = tableProviderFactory.create(databaseDirectory.toString());
        TableProvider tableProvider = (TableProvider) loggingProxyFactory.wrap(writer, tableProviderImplementation, TableProvider.class);;
        
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Boolean.class);
        columnTypes.add(String.class);
        columnTypes.add(Integer.class);
        
        
        table = tableProvider.createTable("tableName", columnTypes);
        
        Assert.assertEquals("tableName", table.getName());
        
        System.out.println(writer.toString());
        
        
    }*/
}
