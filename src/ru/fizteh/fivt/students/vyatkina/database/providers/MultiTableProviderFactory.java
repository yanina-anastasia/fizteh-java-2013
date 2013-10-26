package ru.fizteh.fivt.students.vyatkina.database.providers;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.vyatkina.FileManager;
import ru.fizteh.fivt.students.vyatkina.State;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;
import ru.fizteh.fivt.students.vyatkina.database.providers.MultiTableProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MultiTableProviderFactory implements TableProviderFactory {

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
