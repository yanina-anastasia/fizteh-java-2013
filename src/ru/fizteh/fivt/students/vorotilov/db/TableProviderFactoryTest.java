package ru.fizteh.fivt.students.vorotilov.db;

import org.junit.Test;

public class TableProviderFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void createWithIncorrectRootDir() {
        VorotilovTableProviderFactory test = new VorotilovTableProviderFactory();
        test.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithEmptyRootDir() {
        VorotilovTableProviderFactory test = new VorotilovTableProviderFactory();
        test.create("");
    }
}
