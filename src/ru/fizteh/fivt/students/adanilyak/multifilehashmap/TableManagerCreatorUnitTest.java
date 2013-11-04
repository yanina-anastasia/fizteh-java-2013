package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import org.junit.*;

/**
 * User: Alexander
 * Date: 28.10.13
 * Time: 0:11
 */
public class TableManagerCreatorUnitTest {
    TableManagerCreator testManagerCreator;

    @Before
    public void setUpTestObject() {
        testManagerCreator = new TableManagerCreator();
    }

    /**
     * TEST BLOCK
     * CREATE TABLE MANAGER TESTS
     */

    @Test(expected = IllegalArgumentException.class)
    public void createNullTableManagerTest() {
        testManagerCreator.create(null);
    }
}
