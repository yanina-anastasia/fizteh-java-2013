package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProviderFactory;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;

import java.io.File;
import java.io.IOException;

/**
 * User: Alexander
 * Date: 05.11.13
 * Time: 1:43
 */
public class StoreableTableProviderFactoryUnitTest {
    StoreableTableProviderFactory testProviderFactory;
    String sandBoxDirectory = System.getProperty("fizteh.db.dir");

    @Before
    public void setUpTestObject() {
        File sandBoxFile = new File(sandBoxDirectory);
        sandBoxFile.mkdirs();
        testProviderFactory = new StoreableTableProviderFactory();
    }

    @After
    public void tearDownTestObject() throws IOException {
        DeleteDirectory.rm(new File(sandBoxDirectory));
    }

    /**
     * TEST BLOCK
     * CREATE TABLE PROVIDER TESTS
     */

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableProviderTest() {
        try {
            testProviderFactory.create(null);
        } catch (IOException exc) {
            System.err.println("storeable table provider factory unit test: create: something went wrong");
        }
    }

    /**
     * TEST BLOCK
     * CLOSE TESTS
     */

    @Test(expected = IllegalStateException.class)
    public void createAfterClose() throws IOException {
        testProviderFactory.close();
        testProviderFactory.create(sandBoxDirectory);
    }

    @Test
    public void openExistingProviderUsingCreateAfterClose() throws IOException {
        StoreableTableProvider testProvider = (StoreableTableProvider) testProviderFactory.create(sandBoxDirectory);
        testProvider.close();
        StoreableTableProvider testProviderNewReference =
                (StoreableTableProvider) testProviderFactory.create(sandBoxDirectory);
        Assert.assertNotEquals(testProvider, testProviderNewReference);
    }
}
