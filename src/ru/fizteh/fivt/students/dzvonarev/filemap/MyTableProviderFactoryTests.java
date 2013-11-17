package ru.fizteh.fivt.students.dzvonarev.filemap;

import org.junit.Test;

public class MyTableProviderFactoryTests {

    @Test(expected = IllegalArgumentException.class)
    public void createTableProviderWithNullParameter() {
        MyTableProviderFactory factory = new MyTableProviderFactory();
        factory.create(null);
    }

}
