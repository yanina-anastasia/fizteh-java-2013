package ru.fizteh.fivt.students.adanilyak.tests;

import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.students.adanilyak.storeable.StoreableTableProviderFactory;

import java.io.IOException;

/**
 * User: Alexander
 * Date: 05.11.13
 * Time: 1:43
 */
public class StoreableTableProviderFactoryUnitTest {
    StoreableTableProviderFactory testProviderFactory;

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
}
