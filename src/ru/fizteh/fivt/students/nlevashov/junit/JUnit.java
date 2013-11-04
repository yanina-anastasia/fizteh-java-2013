package ru.fizteh.fivt.students.nlevashov.junit;

import org.junit.*;
import static org.junit.Assert.*;

import ru.fizteh.fivt.students.nlevashov.factory.*;
import ru.fizteh.fivt.storage.strings.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JUnit {
    TableProviderFactory factory;
    TableProvider provider;
    Table table;

    @Before
    public void providerCreatingPlatformCreating() {
        factory = new MyTableProviderFactory();
        assertNotNull(factory);

        try {
            Files.createDirectory(Paths.get("factory1").resolve("emptyDirectory"));

            Files.createDirectory(Paths.get("factory1").resolve("directoryWithFile"));
            Files.createFile(Paths.get("factory1").resolve("directoryWithFile").resolve("someFile"));

            Files.createDirectory(Paths.get("factory1").resolve("directoryWithWrongNameDirectory"));
            Files.createDirectory(Paths.get("factory1").resolve("directoryWithWrongNameDirectory").resolve("12.d"));

            Files.createDirectory(Paths.get("factory1").resolve("directoryWithDirectoryWithWrongNameFile"));
            Files.createDirectory(Paths.get("factory1").resolve("directoryWithDirectoryWithWrongNameFile").resolve("12.dat"));
            Files.createFile(Paths.get("factory1").resolve("directoryWithDirectoryWithWrongNameFile").resolve("12.dat").resolve("1.d"));
        } catch (IOException e) {
        }
    }

    @After
    public void providerCreatingPlatform() {
        try {
            Files.delete(Paths.get("factory1").resolve("emptyDirectory"));

            Files.delete(Paths.get("factory1").resolve("directoryWithFile").resolve("someFile"));
            Files.delete(Paths.get("factory1").resolve("directoryWithFile"));

            Files.delete(Paths.get("factory1").resolve("directoryWithWrongNameDirectory").resolve("12.d"));
            Files.delete(Paths.get("factory1").resolve("directoryWithWrongNameDirectory"));

            Files.delete(Paths.get("factory1").resolve("directoryWithDirectoryWithWrongNameFile").resolve("12.dat").resolve("1.d"));
            Files.delete(Paths.get("factory1").resolve("directoryWithDirectoryWithWrongNameFile").resolve("12.dat"));
            Files.delete(Paths.get("factory1").resolve("directoryWithDirectoryWithWrongNameFile"));
        } catch (IOException e) {
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void nullProviderCreating() {
        factory.create(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyProviderCreating() {
        factory.create("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void wrongNameProviderCreating() {
        factory.create("!@#$%^&*()_+-=qwerty|\\\\/<>?';lkjhgfdsazxcvbnm`~");
    }

    @Test (expected = RuntimeException.class)
    public void directoryWithFileProviderCreating() {
        factory.create("factory1" + File.separator + "directoryWithFile");
    }

    @Test (expected = RuntimeException.class)
    public void directoryWithWrongNameDirectoryProviderCreating() {
        factory.create("factory1" + File.separator + "directoryWithWrongNameDirectory");
    }

    @Test (expected = RuntimeException.class)
    public void directoryWithDirectoryWithWrongNameFileProviderCreating() {
        factory.create("factory1" + File.separator + "directoryWithDirectoryWithWrongNameFile");
    }

    //------------------------------------------------------------------------

    @Before
    public void platformCreating() {
        provider = factory.create("providerName");
        assertNotNull(provider);
        table = provider.createTable("tableName");
        assertNotNull(table);
    }

    @After
    public void cleaning() {
        provider.removeTable("tableName");
    }
}
