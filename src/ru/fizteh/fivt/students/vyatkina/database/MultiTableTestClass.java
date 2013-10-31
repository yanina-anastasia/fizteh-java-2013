package ru.fizteh.fivt.students.vyatkina.database;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vyatkina.FileManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MultiTableTestClass {
    private static MultiTableProvider tableProvider;
    private final static String SAMPLE_TABLE_NAME1 = "MultiTableTestClassTable1";
    private final static String SAMPLE_TABLE_NAME2 = "MultiTableTestClassTable2";
    private final static String SAMPLE_KEY1 = "house";
    private final static String SAMPLE_VALUE1 = "ThisIsTheHouseThatJackBuilt";
    private Table table;

    @BeforeClass
    public static void setTableProvider () throws IOException, IllegalStateException {
        Path workingDirectory = Paths.get (System.getProperty ("fizteh.db.dir"));
        if (workingDirectory == null) {
            throw new IllegalStateException ("Property fizteh.db.dir is not given");
        }

        tableProvider = new MultiTableProvider (new DatabaseState ( new FileManager (workingDirectory)));
    }

    @Before
    public void setUseTableSampleTable1 () {
        table = tableProvider.createTable (SAMPLE_TABLE_NAME1);
        if (table == null) {
            table = tableProvider.getTable (SAMPLE_TABLE_NAME1);
        }
        tableProvider.state.setTable (table);
        Assert.assertNotEquals ("Current table should not be null", null,table);

    }

    @After
    public void unsetUseTable () throws IOException {
        tableProvider.state.setTable (null);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none ();

    @Test
    public void tableNameShouldBeSampleTableName1 () {
        Assert.assertEquals ("Table name should be " + SAMPLE_TABLE_NAME1,SAMPLE_TABLE_NAME1,table.getName ());
    }

    @Test
    public void putNullKeyShouldFail () {
       thrown.expect (IllegalArgumentException.class);
       thrown.expectMessage (MultiTable.KEY_SHOULD_NOT_BE_NULL);
       table.put (null, SAMPLE_VALUE1);

    }

    @Test
    public void putNullValueShouldFail () {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTable.VALUE_SHOULD_NOT_BE_NULL);
        table.put (SAMPLE_KEY1, null);

    }

    @AfterClass
    public static void deleteAllWork () throws IOException {
        tableProvider.state.getFileManager ().deleteAllFilesInCurrentDirectory ();
    }

}
