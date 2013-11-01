package ru.fizteh.fivt.students.vyatkina.database;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MultiTableProviderFactoryTest {

    private MultiTableProviderFactory factory = new MultiTableProviderFactory ();
    private static final String NOT_EXISTING_DIRECTORY = "MultiTableProviderFactoryTestNotExistingDirectory";
    private static final String FILE_SAMPLE_NAME = "MultiTableProviderFactoryTestSampleFile";

    @Rule
    public ExpectedException thrown = ExpectedException.none ();

    @Test
    public void createMultiTableProviderWithNullDirectoryShouldFail () {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTableProviderFactory.NULL_DIRECTORY);
        factory.create (null);
    }

    @Test
    public void createMultiTableProviderWithEmptyDirectoryShouldFail () {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTableProviderFactory.EMPTY_DIRECTORY);
        factory.create ("   ");
    }

    @Test
    public void createMultiTableProviderWithDirectoryNotExistingShouldFail () {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTableProviderFactory.FILE_DOES_NOT_EXIST_OR_IS_NOT_A_DIRECTORY);
        factory.create (NOT_EXISTING_DIRECTORY);
    }

    @Test
    public void createMultiTableProviderWithFileNotDirectoryShouldFail () throws IOException {
        thrown.expect (IllegalArgumentException.class);
        thrown.expectMessage (MultiTableProviderFactory.FILE_DOES_NOT_EXIST_OR_IS_NOT_A_DIRECTORY);
        Path filePath = Paths.get (FILE_SAMPLE_NAME);
        try {
            if (Files.notExists (filePath)) {
                Files.createFile (filePath);
            }
            factory.create (FILE_SAMPLE_NAME);
        }
        finally {
            Files.deleteIfExists (filePath);
        }
    }

}
