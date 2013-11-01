package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.vyatkina.FileManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MultiTableProviderFactory implements TableProviderFactory {

    public static final String NULL_DIRECTORY = "Directory is null";
    public static final String FILE_DOES_NOT_EXIST_OR_IS_NOT_A_DIRECTORY = "File does not exist or is not a directory";
    public static final String EMPTY_DIRECTORY = "Directory is empty";

    @Override
    public TableProvider create (String dir) throws IllegalArgumentException {

        if (dir == null) {
            throw new IllegalArgumentException (NULL_DIRECTORY);
        }

        if (dir.trim ().isEmpty ()) {
            throw new IllegalArgumentException (EMPTY_DIRECTORY);
        }

        Path directory = Paths.get (dir);

        if (Files.notExists (directory) || !Files.isDirectory (directory)) {
            throw new IllegalArgumentException (FILE_DOES_NOT_EXIST_OR_IS_NOT_A_DIRECTORY);
        }

        MultiTableProvider multiTableProvider;

        try {
            multiTableProvider = new MultiTableProvider (new DatabaseState (new FileManager (directory)));
            multiTableProvider.state.setTableProvider (multiTableProvider);
        }
        catch (IOException | IllegalArgumentException e) {
            throw new IllegalArgumentException (e.getMessage ());
        }

        return multiTableProvider;
    }
}
