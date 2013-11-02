package ru.fizteh.fivt.students.anastasyev.filemap.tests;

import org.junit.*;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.anastasyev.filemap.FileMapTableProviderFactory;

public class FileMapTableProviderFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() {
        TableProviderFactory factory = new FileMapTableProviderFactory();
        factory.create(null);
    }
}
