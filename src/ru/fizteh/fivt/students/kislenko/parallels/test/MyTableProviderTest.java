package ru.fizteh.fivt.students.kislenko.parallels.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.kislenko.junit.test.Cleaner;
import ru.fizteh.fivt.students.kislenko.parallels.MyTable;
import ru.fizteh.fivt.students.kislenko.parallels.MyTableProvider;
import ru.fizteh.fivt.students.kislenko.parallels.MyTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyTableProviderTest {
    private static MyTableProvider provider;
    private static File databaseDir = new File("database");
    private static ArrayList<Class<?>> typeList = new ArrayList<Class<?>>();
    private static boolean multiThreadCorrectFlagFirst = false;
    private static boolean multiThreadCorrectFlagSecond = false;
    private static MyTable table1;
    private static MyTable table2;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        databaseDir.mkdir();
        provider = factory.create("database");
        typeList.add(Integer.class);
        typeList.add(Integer.class);
        typeList.add(Integer.class);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Cleaner.clean(databaseDir);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyNameTable() throws Exception {
        provider.createTable("", typeList);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateTableWithDot() throws Exception {
        provider.createTable(".", typeList);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateTableWithDots() throws Exception {
        provider.createTable("..", typeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullNameTable() throws Exception {
        provider.createTable(null, typeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullTypeListTable() throws Exception {
        provider.createTable("goodTable", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithBadTypeListTable() throws Exception {
        ArrayList<Class<?>> badTypeList = new ArrayList<Class<?>>();
        badTypeList.add(StringBuilder.class);
        provider.createTable("tableBetterThanTheLastOne", badTypeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyTypeListTable() throws Exception {
        ArrayList<Class<?>> empty = new ArrayList<Class<?>>();
        provider.createTable("theBestTable", empty);
    }

    @Test
    public void testCreateNormalTable() throws Exception {
        provider.createTable("definitelyTheBestTable", typeList);
    }

    @Test
    public void testCreateExistingTable() throws Exception {
        provider.createTable("definitelyTheBestTable", typeList);
    }

    @Test
    public void testMultiThreadCreateSameTables() throws Exception {
        multiThreadCorrectFlagFirst = false;
        multiThreadCorrectFlagSecond = false;
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    multiThreadCorrectFlagFirst = (provider.createTable("myFirstParallelTable", typeList) == null);
                } catch (IOException ignored) {
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    multiThreadCorrectFlagSecond = (provider.createTable("myFirstParallelTable", typeList) == null);
                } catch (IOException ignored) {
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        provider.removeTable("myFirstParallelTable");
        Assert.assertTrue(multiThreadCorrectFlagFirst ^ multiThreadCorrectFlagSecond);
    }

    @Test
    public void testMultiThreadCreateDifferentTables() throws Exception {
        multiThreadCorrectFlagFirst = false;
        multiThreadCorrectFlagSecond = false;
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    multiThreadCorrectFlagFirst = (provider.createTable("myFirstParallelTable", typeList) != null);
                } catch (IOException ignored) {
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    multiThreadCorrectFlagSecond = (provider.createTable("mySecondParallelTable", typeList) != null);
                } catch (IOException ignored) {
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertTrue(multiThreadCorrectFlagFirst && multiThreadCorrectFlagSecond);
        provider.removeTable("myFirstParallelTable");
        provider.removeTable("mySecondParallelTable");
    }

    @Test
    public void testMultiThreadCreateRemoveSameTables() throws Exception {
        multiThreadCorrectFlagFirst = false;
        multiThreadCorrectFlagSecond = false;
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    multiThreadCorrectFlagFirst = (provider.createTable("parallel", typeList) != null);
                } catch (IOException ignored) {
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    provider.removeTable("parallel");
                } catch (IOException ignored) {
                }
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertNull(provider.getTable("parallel"));
    }

    @Test
    public void testMultiThreadCreateGetSameTables() throws Exception {
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    table1 = provider.createTable("KRESLO", typeList);
                } catch (IOException ignored) {
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                table2 = provider.getTable("KRESLO");
            }
        });
        first.start();
        second.start();
        first.join();
        second.join();
        Assert.assertEquals(table1, table2);
        provider.removeTable("KRESLO");
    }

    @Test
    public void testMultiThreadCreateRemoveGetSameTables() throws Exception {
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    provider.createTable("KRESLO", typeList);
                } catch (IOException ignored) {
                }
            }
        });
        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    provider.removeTable("KRESLO");
                } catch (IOException ignored) {
                }
            }
        });
        Thread third = new Thread(new Runnable() {
            @Override
            public void run() {
                table1 = provider.getTable("KRESLO");
            }
        });
        first.start();
        second.start();
        third.start();
        first.join();
        second.join();
        third.join();
        Assert.assertNull(table1);
    }
}
