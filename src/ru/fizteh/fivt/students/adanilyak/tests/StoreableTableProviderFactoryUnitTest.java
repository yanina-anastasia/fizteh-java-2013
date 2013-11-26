package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTable;
import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProviderFactory;

import java.io.File;
import java.io.IOException;

/**
 * User: Alexander
 * Date: 05.11.13
 * Time: 1:43
 */
public class StoreableTableProviderFactoryUnitTest {
    StoreableTableProviderFactory testProviderFactory;
    String sandBoxDirectory = "/Users/Alexander/Documents/JavaDataBase/SandBox";

    @Before
    public void setUpTestObject() {
        testProviderFactory = new StoreableTableProviderFactory();
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

    @Test(expected = IllegalStateException.class)
    public void createAfterClose() throws IOException {
        testProviderFactory.close();
        testProviderFactory.create(sandBoxDirectory);
    }
}
