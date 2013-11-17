package ru.fizteh.fivt.students.chernigovsky.junit;

import org.junit.*;

public class MyTableProviderFactoryTest {
    private MyTableProviderFactory tableProviderFactory;

    @Before
    public void setUp() {
        tableProviderFactory = new MyTableProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNlShouldFail() {
        tableProviderFactory.create(null);
    }
}