package ru.fizteh.fivt.students.vlmazlov.parallel.tests;

import org.junit.*;
import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTable;
import ru.fizteh.fivt.students.vlmazlov.storeable.StoreableTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vlmazlov.shell.FileUtils;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.storeable.Main;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class StoreableTableParallelTest {
	private volatile StoreableTable table;
	private StoreableTableProvider provider;
	private Storeable val1, val2, val3, val4;
	private final String root = "StoreableTableTest";

	@Before
	public void setUp() {
		try {
			File tempDir = FileUtils.createTempDir(root, null);
			provider = new StoreableTableProvider(tempDir.getPath(), false);

			List<Object> values1 = new ArrayList<Object>() {{
				add(null);
				add(new String("val1"));
				add(Byte.valueOf("-3"));
			}};

			List<Object> values2 = new ArrayList<Object>() {{
				add(125);
				add(new String("val2"));
				add(Byte.valueOf("-50"));
			}};
			
			List<Object> values3 = new ArrayList<Object>() {{
				add(1255);
				add(new String("val3"));
				add(Byte.valueOf("50"));
			}};
			
			List<Object> values4 = new ArrayList<Object>() {{
				add(12555);
				add(new String("val4"));
				add(null);
			}};

			List<Class<?>> valueTypes = new ArrayList<Class<?>>() {{
				add(Integer.class);
				add(String.class);
				add(Byte.class);
			}};

			table = provider.createTable("testTable", valueTypes);
			val1 = provider.createFor(table, values1);
			val2  = provider.createFor(table, values2);
			val3  = provider.createFor(table, values3);
			val4  = provider.createFor(table, values4);

		} catch (ValidityCheckFailedException ex) {
			Assert.fail("validity check failed: " + ex.getMessage());
		} catch (IOException ex) {
			Assert.fail("Input/output error: check failed: " + ex.getMessage());
		}
	}

	@After
	public void tearDown() {
		provider.removeTable("testTable");
	}	

	@Test 
	public void getCommitFromAnotherThread() {
		Thread testThread = new Thread() {
            @Override
            public void run() {
                table.put("key2", val1);
 	
                table.commit();
            }
        };

        testThread.start();	
        try {
            testThread.join();
  		} catch (InterruptedException ex) {}
  		
        Assert.assertEquals("value commited from another thread is not visible", val1, table.get("key2"));
	}

	@Test 
	public void sizeCommitFromAnotherThread() {
		int tableSize = 0;

		final Thread testThread1 = new Thread() {
            @Override
            public void run() {
                table.put("key1", val1);
 				
                table.commit();
            }
        };

        Thread testThread2 = new Thread() {
            @Override
            public void run() {
            	try {
            	    testThread1.join();
            	} catch (InterruptedException ex) {}

                table.put("key2", val2);
 				table.put("key1", val3);
                
                Assert.assertEquals("size incorrect after commit in another thread", table.size(), 2);
            }
        };

        testThread1.start();
        testThread2.start();
	}

	@Test 
	public void diffsShouldBeLocal() {

		final Thread testThread1 = new Thread() {
            @Override
            public void run() {
                table.put("key1", val2);
 				table.put("key2", val2);
                table.rollback();
            }
        };

        Thread testThread2 = new Thread() {
            @Override
            public void run() {
            	try {
            	    testThread1.join();
            	} catch (InterruptedException ex) {}

                Assert.assertEquals("local change reflected in another thread", table.get("key1"), null);
 				Assert.assertNull("local change reflected in another thread", table.get("key2"));
            }
        };

        testThread1.start();
        testThread2.start();
    }

    @Test 
	public void concurrentPutSize() {

		Thread testThread1 = new Thread() {
            @Override
            public void run() {
                table.put("key1", val1);
                table.commit();
            }
        };

        Thread testThread2 = new Thread() {
            @Override
            public void run() {
            	table.put("key1", val2);

                Assert.assertEquals("two parallel puts not merged", 1, table.size());
            }
        };

        testThread1.start();
        testThread2.start();
    }

     @Test 
	public void concurrentPutCommit() {

		table.put("key1", val1);
		table.commit();

		final Thread testThread1 = new Thread() {
            @Override
            public void run() {
                table.put("key1", val1);
                Assert.assertEquals("put shouldn't change value", 0, table.commit());
            }
        };

        Thread testThread2 = new Thread() {
            @Override
            public void run() {
            	table.put("key1", val2);

                Assert.assertEquals("diff incorrect", 1, table.commit());
            }
        };

        testThread1.start();
        testThread2.start();
    }
}