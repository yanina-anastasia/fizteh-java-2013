package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;


public class TestsForParallel {
    
    TableProviderFactory tableProviderFactory;
    TableProvider tableProvider;
    Table table;
    
    Storeable value1;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void createTable() throws IOException {
        File databaseDirectory = folder.newFolder("database");
        tableProviderFactory = new TableProviderFactoryImplementation();
        tableProvider = tableProviderFactory.create(databaseDirectory.toString());
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Boolean.class);
        columnTypes.add(String.class);
        columnTypes.add(Integer.class);
        table = tableProvider.createTable("tableName", columnTypes);
        
        value1 = new StoreableImplementation(columnTypes);
        value1.setColumnAt(0, true);
        value1.setColumnAt(1, "AA");
        value1.setColumnAt(2, 5);
    }
    
    @Test
    public void PutSamePutCommitRollback() throws InterruptedException, ExecutionException {
        ExecutorService executor1 = Executors.newSingleThreadExecutor();
        ExecutorService executor2 = Executors.newSingleThreadExecutor();
        Future<Storeable> future1 = executor1.submit(new Callable<Storeable>() {
            @Override
            public Storeable call() {
                return table.put("1", value1);
            }
        });
        assertNull(future1.get());
        
        Future<Storeable> future2 = executor2.submit(new Callable<Storeable>() {
            @Override
            public Storeable call() {
                return table.put("1", value1);
            }
        });
        assertNull(future2.get());
        
        Future<Integer> future3 = executor1.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws IOException {
                return table.commit();
            }
        });
        assertEquals(future3.get(), Integer.valueOf(1));
        
        Future<Integer> future4 = executor2.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws IOException {
                return table.rollback();
            }
        });
        assertEquals(future4.get(), Integer.valueOf(0));
        
        executor1.shutdown();
        executor2.shutdown();
    }
    
}