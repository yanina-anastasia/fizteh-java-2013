package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.vyatkina.FileManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SingleTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create (String dir) throws IllegalArgumentException {
        Path directory = Paths.get (dir);
        if (directory == null) {
            throw new IllegalArgumentException ("Directory is null");
        }
        if (Files.notExists (directory)) {
            throw new IllegalArgumentException ("This directory [" + directory + "] doesn't exist");
        }
        if (!Files.isDirectory (directory)) {
            throw new IllegalArgumentException ("The file [" + directory + "] is not a directory");
        }
        SingleTableProvider singleTableProvider;
        try {
            singleTableProvider = new SingleTableProvider (new DatabaseState (new FileManager (directory)));
            singleTableProvider.state.setTable (singleTableProvider.getTable ());
        }
        catch (IOException | IllegalArgumentException e) {
            throw new IllegalArgumentException (e.getMessage ());
        }
        return singleTableProvider;
    }
}
