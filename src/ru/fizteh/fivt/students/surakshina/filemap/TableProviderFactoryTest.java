package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.IOException;

import org.junit.*;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TableProviderFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws IOException {
        TableProviderFactory factory = new NewTableProviderFactory();
        factory.create(null);
    }

}
