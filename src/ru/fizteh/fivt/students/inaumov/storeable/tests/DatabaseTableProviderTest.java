package ru.fizteh.fivt.students.inaumov.storeable.tests;

import org.junit.Test;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTableProviderFactory;

import java.io.IOException;

public class DatabaseTableProviderTest {
    TableProviderFactory tableProviderFactory = new DatabaseTableProviderFactory();

    @Test(expected = IOException.class)
    public void createDatabaseProviderIncorrectDirShouldFail() throws IOException {
        tableProviderFactory.create("./unexisting_outer_dir/unexisting_inner_dir");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDatabaseProviderEmptyDirShouldFail() {
        try {
            tableProviderFactory.create("");
        } catch (IOException e) {
            //
        }
    }
}
