package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.*;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.FileMap;
import ru.fizteh.fivt.students.sterzhanovVladislav.fileMap.FileMapProvider;
import static org.junit.Assert.*;

public class ParallelTester {
    static FileMapProvider provider;
    static List<Class<?>> sampleSignature;
    static FileMap staticTable;
    static ExecutorService executor;
    static Storeable sampleValue;
    
    @Before
    public void init() throws IllegalArgumentException, IOException {
        provider = new FileMapProvider(System.getProperty("user.dir"));
        sampleSignature = new ArrayList<Class<?>>();
        sampleSignature.add(String.class);
        staticTable = provider.createTable("static", sampleSignature);
        executor = Executors.newCachedThreadPool();
        sampleValue = provider.createFor(staticTable);
        sampleValue.setColumnAt(0, "value");
    }
    
    @Test
    public void basicStaticAccessabilityTest() throws InterruptedException, ExecutionException {
        Callable<FileMap> task = new Callable<FileMap>() {
            @Override
            public FileMap call() {
                return provider.getTable("static");
            }
        };
        assertEquals(staticTable, executor.submit(task).get());
    }
    
    @Test
    public void threadSharingTablesTest() throws InterruptedException, ExecutionException {
        Runnable taskA = new Runnable() {
            @Override
            public void run() {
                FileMap table;
                try {
                    table = provider.createTable("table", sampleSignature);
                    table.put("key", sampleValue);
                    table.commit();
                } catch (Exception e) {
                    // Ignore, will fail anyway
                    return;
                }
            }
        };
        Callable<Storeable> taskB = new Callable<Storeable>() {
            @Override
            public Storeable call() {
                return provider.getTable("table").get("key");
            }
        };
        executor.submit(taskA).get();
        assertEquals(sampleValue, executor.submit(taskB).get());
        provider.removeTable("table");
    }
    
    @Test(expected = IllegalStateException.class)
    public void destroyedTableShouldFail() throws InterruptedException, ExecutionException, IOException {
        final Table table = provider.createTable("table", sampleSignature);
        table.put("key", sampleValue);
        table.commit();
        Runnable taskA = new Runnable() {
            @Override
            public void run() {
                provider.removeTable("table");
            }
        };
        Callable<Storeable> taskB = new Callable<Storeable>() {
            @Override
            public Storeable call() {
                return table.get("key");
            }
        };
        executor.submit(taskA).get();
        try {
            executor.submit(taskB).get();
        } catch (ExecutionException e) {
            if (!e.getCause().getClass().equals(IllegalStateException.class)) {
                throw new RuntimeException();
            }
            IllegalStateException ise = (IllegalStateException) e.getCause();
            assertEquals(ise.getMessage(), "Table no longer exists");
            throw ise;
        }
    }
    
    @Test
    public void commitCountTest2() throws InterruptedException, ExecutionException {
        commitCountTest(2);
    }
    
    private void commitCountTest(int threadCount) throws InterruptedException, ExecutionException {
        final AtomicLong counter = new AtomicLong();
        Callable<Integer> task = new Callable<Integer>() {
            @Override
            public Integer call() throws IOException {
                for (int i = 0; i < 5; ++i) {
                    staticTable.put("key" + counter.incrementAndGet(), sampleValue);
                }
                return staticTable.commit();
            }
        };
        List<Callable<Integer>> tasks = Collections.nCopies(threadCount, task);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<Integer>> futures = executorService.invokeAll(tasks);
        // Check for exceptions
        for (Future<Integer> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            assertEquals(future.get(), (Integer) 5);
        }
    }

    @After
    public void wipe() {
        provider.removeTable("static");
    }
}
