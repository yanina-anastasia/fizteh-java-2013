package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class SingleTableProvider extends AbstractTableProvider {

    public final String DB_DAT = "db.dat";
    private final Path fileName;
    private final SingleTable table;

    public SingleTableProvider (DatabaseState state) throws IOException {
        super (state);
        Path currentDirectory = state.getFileManager ().getCurrentDirectory ();
        isDirectoryCheck (currentDirectory);
        fileName = currentDirectory.resolve (Paths.get (DB_DAT));

        table = new SingleTable (new HashMap<String, String> (), this);
        state.setTable (table);
        getDatabaseFromDisk ();
    }

    public SingleTable getTable () {
        return table;
    }


    @Override
    public void getDatabaseFromDisk () throws IOException {

        if (Files.notExists (fileName)) {
            return;
        }

        try (DataInputStream in = new DataInputStream (new BufferedInputStream
                (new FileInputStream (fileName.toFile ())))) {

            while (in.available () != 0) {
                DatabaseUtils.KeyValue pair = DatabaseUtils.readKeyValue (in);
                table.put (pair.key, pair.value);
            }
        }

        catch (IllegalArgumentException | IOException e) {
            throw new IOException ("Unable to read from file: " + e.getMessage ());
        }
    }

    public void writeDatabaseOnDisk () throws IOException {
        Files.deleteIfExists (fileName);

        Files.createFile (fileName);

        try (DataOutputStream out = new DataOutputStream (new BufferedOutputStream
                (new FileOutputStream (fileName.toFile (), true)))) {

            for (String key : table.getKeys ()) {

                String value = table.get (key);
                DatabaseUtils.writeKeyValue (new DatabaseUtils.KeyValue (key, value), out);
            }
        }
        catch (IOException e) {
            throw new IOException ("Unable to write to file: " + e.getMessage ());
        }
    }


    @Override
    public Table getTable (String name) {
        throw new UnsupportedOperationException ("Get operation is not supported");
    }

    @Override
    public Table createTable (String name) {
        throw new UnsupportedOperationException ("Create operation is not supported");
    }

    @Override
    public void removeTable (String name) {
        throw new UnsupportedOperationException ("Remove operation is not supported");
    }
}
