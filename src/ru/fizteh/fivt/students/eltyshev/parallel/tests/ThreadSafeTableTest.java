package ru.fizteh.fivt.students.eltyshev.parallel.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.eltyshev.parallel.database.ThreadSafeDatabaseTableProviderFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ThreadSafeTableTest {
    private static final int THREAD_COUNT = 5;
    private static final int KEYS_COUNT = 20;
    private static String DATABASE_DIRECTORY = "C:\\temp\\storeable_test";
    private static String TABLE_NAME = "ThreadSafeTable";

    private TableProvider provider;
    private Table currentTable;

    @Before
    public void setUp() throws Exception {
        TableProviderFactory factory = new ThreadSafeDatabaseTableProviderFactory();
        provider = factory.create(DATABASE_DIRECTORY);
        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(String.class);
        currentTable = provider.createTable(TABLE_NAME, columnTypes);
    }

    @After
    public void tearDown() throws Exception {
        provider.removeTable(TABLE_NAME);
    }

    @Test
    public void testCurrentOnlyThreadDiff() {
        for (int index = 0; index < THREAD_COUNT; ++index) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    checkThreadOnlyDiff();
                }
            });
        }
    }

    private void checkThreadOnlyDiff() {
        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            currentTable.put(key, makeStoreable(index));
        }

        try {
            currentTable.commit();
        } catch (IOException e) {
            //
        }

        for (int index = 0; index < KEYS_COUNT; ++index) {
            String key = String.format("key%d", index);
            Assert.assertEquals(makeStoreable(index), currentTable.get(key));
        }
    }

    private Storeable makeStoreable(int value1) {
        String xml = String.format("<row><col>value%d%d</col></row>", Thread.currentThread().getId(), value1);
        try {
            return provider.deserialize(currentTable, xml);
        } catch (ParseException e) {
            return null;
        }
    }
}
