package ru.fizteh.fivt.students.vyatkina.database;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MultiTableProviderTest {

    private MultiTableProvider tableProvider;
    String workingDirectory = System.getProperty ("fizteh.db.dir");
    private final String STANDART_TABLE_NAME = "MultiTableProviderTestTable";
    @Rule
    public ExpectedException thrown = ExpectedException.none ();

    @Before
    public void setTableProvider () {
        MultiTableProviderFactory factory = new MultiTableProviderFactory ();
        tableProvider = (MultiTableProvider) factory.create (workingDirectory);
    }

    @After
    public void deleteAllWork () throws IOException {
        tableProvider.state.getFileManager ().deleteAllFilesInCurrentDirectory ();
    }

    @Test
    public void tableProviderShouldHaveWorkingDirectoryFromProperty () {
        Path currentDirectory = tableProvider.state.getFileManager ().getCurrentDirectory ();
        Assert.assertEquals ("Working directory should be from property", workingDirectory, currentDirectory.toString ());
    }

    @Test
    public void UsingTableShouldBeNull () {
        Assert.assertEquals ("Using table should be null", null, tableProvider.state.getTable ());
    }

    @Test
    public void tableProviderShouldBeThis () {
        Assert.assertEquals ("Table provider should be this", tableProvider, tableProvider.state.getTableProvider ());
    }

    @Test
    public void getTableWhatNotExistShouldBeNull () {
        Assert.assertEquals ("Get table " + STANDART_TABLE_NAME + "should be null", null,
                tableProvider.getTable (STANDART_TABLE_NAME));
    }

    @Test
    public void createTableWithNullNameShouldFail () {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTableProvider.UNSUPPORTED_TABLE_NAME);
        tableProvider.createTable (null);
    }

    @Test
    public void createTableWithNameContainingPathSeparatorShouldFail () {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTableProvider.UNSUPPORTED_TABLE_NAME);
        tableProvider.createTable (STANDART_TABLE_NAME + File.pathSeparator);
    }

    @Test
    public void createTableWithNameContainingSeparatorShouldFail () {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTableProvider.UNSUPPORTED_TABLE_NAME);
        tableProvider.createTable (STANDART_TABLE_NAME + File.separator);
    }

    @Test
    public void createTableWithWhiteSpaceNameShouldFail () {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTableProvider.UNSUPPORTED_TABLE_NAME);
        tableProvider.createTable ("    ");
    }

    @Test
    public void createTableWithTestName () {
        Table table = tableProvider.createTable (STANDART_TABLE_NAME);
        Assert.assertTrue (tableProvider.tables.containsKey (STANDART_TABLE_NAME));
    }

    @Test
    public void getTableThatWasJustCreated () {
        Table table = tableProvider.createTable (STANDART_TABLE_NAME);
        Assert.assertEquals ("Created table should be reachable", table, tableProvider.getTable (STANDART_TABLE_NAME));
    }

    @Test
    public void removeTableShouldMakeItUnreachable () {
        Table table = tableProvider.createTable (STANDART_TABLE_NAME);
        Assert.assertEquals ("Created table should be reachable", table, tableProvider.getTable (STANDART_TABLE_NAME));
        tableProvider.removeTable (STANDART_TABLE_NAME);
        Assert.assertEquals ("Dropped table should not be reachable", null, tableProvider.getTable (STANDART_TABLE_NAME));
    }

    @Test
    public void removeTableThatNotExistShouldThrowIllegalStateException () {
        thrown.expect (IllegalStateException.class);
        thrown.expectMessage (MultiTableProvider.TABLE_NOT_EXIST);
        tableProvider.removeTable (STANDART_TABLE_NAME);
    }


    @Test
    public void getTableWithNullNameShouldFail () {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTableProvider.UNSUPPORTED_TABLE_NAME);
        tableProvider.getTable (null);
    }

    @Test
    public void getTableWithNameContainingPathSeparatorShouldFail () {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTableProvider.UNSUPPORTED_TABLE_NAME);
        tableProvider.getTable (STANDART_TABLE_NAME + File.pathSeparator);
    }

    @Test
    public void getTableWithNameContainingSeparatorShouldFail () {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTableProvider.UNSUPPORTED_TABLE_NAME);
        tableProvider.getTable (STANDART_TABLE_NAME + File.separator);
    }

    @Test
    public void getTableWithWhiteSpaceNameShouldFail () {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTableProvider.UNSUPPORTED_TABLE_NAME);
        tableProvider.getTable ("    ");
    }


}
