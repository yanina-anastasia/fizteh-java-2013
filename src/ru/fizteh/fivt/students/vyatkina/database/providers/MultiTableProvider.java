package ru.fizteh.fivt.students.vyatkina.database.providers;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vyatkina.State;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseUtils;
import ru.fizteh.fivt.students.vyatkina.database.tables.MultiTable;
import ru.fizteh.fivt.students.vyatkina.database.tables.SingleTable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class MultiTableProvider extends AbstractTableProvider {

    protected Map<String, Table> tables = new HashMap<> ();
    protected Set <String> droppedTables = new HashSet <> ();

    private final int NUMBER_OF_FILES = 16;
    private final int NUMBER_OF_DIRECTORIES = 16;
    private final String DOT_DIR = ".dir";
    private final String DOT_DAT = ".dat";
    public static final int MAX_SUPPORTED_NAME_LENGTH = 1024;

    public MultiTableProvider (DatabaseState state) throws IOException {
        super (state);
        state.setTableProvider (this);
        getDatabaseFromDisk ();
    }

    protected void validTableNameCheck (String tableName) throws IllegalArgumentException {
        if ((tableName == null) || (tableName.length () > MAX_SUPPORTED_NAME_LENGTH)) {
            throw new IllegalArgumentException ("Unsupported table name");
        }
    }

    private boolean isValidDatabaseFileName (String name) {
        return Pattern.matches ("([0-9]|(1[0-5]))\\.dat", name);
    }

    private boolean isValidDatabaseDirectoryName (String name) {
        return Pattern.matches ("([0-9]|(1[0-5]))\\.dir", name);
    }

    private Path createFileForKey (String key, Path currentPath) throws IOException {
        byte keyByte = (byte) Math.abs (key.getBytes (StandardCharsets.UTF_8)[0]);
        int ndirectory = keyByte % NUMBER_OF_DIRECTORIES;
        int nfile = (keyByte / NUMBER_OF_DIRECTORIES) % NUMBER_OF_FILES;

        Path directory = currentPath.resolve (Paths.get (ndirectory + DOT_DIR));
        if (Files.notExists (directory)) {
            Files.createDirectory (directory);
        }
        Path file = directory.resolve (Paths.get (nfile + DOT_DAT));
        if (Files.notExists (file)) {
            Files.createFile (file);
        }
        return file;

    }

    protected Table createNewTable (String tableName) {
        return new MultiTable (tableName, new HashMap<String, String> (), this);
    }

    @Override
    public Table createTable (String tableName) throws IllegalArgumentException, IllegalStateException {
        validTableNameCheck (tableName);
        if (tables.containsKey (tableName)) {
            return null;
        } else {
            Table newTable = createNewTable (tableName);
            tables.put (newTable.getName (), newTable);
            return newTable;
        }
    }

    @Override
    public void removeTable (String tableName) throws IllegalArgumentException, IllegalStateException {
        validTableNameCheck (tableName);
        Table table = tables.remove (tableName);
        droppedTables.add (table.getName ());
        if (table == null) {
            throw new IllegalStateException ("Try to delete unknown table");
        }
    }

    @Override
    public Table getTable (String tableName) throws IllegalArgumentException {
        validTableNameCheck (tableName);
        return tables.get (tableName);
    }

    @Override
    public void writeDatabaseOnDisk () throws IOException, IllegalArgumentException {
        Set<String> tableNames = tables.keySet ();

        for (String oldtableName : tableNames) {
            Path oldDirectory = state.getFileManager ().getCurrentDirectory ().resolve (oldtableName);
            if (Files.exists (oldDirectory)) {
                state.getFileManager ().deleteFile (oldDirectory);
            }
        }

        for (String dpopped: droppedTables) {
            Path tableToDrop = Paths.get (dpopped);
            Path oldDirectory = state.getFileManager ().getCurrentDirectory ().resolve (tableToDrop);
            if (Files.exists (oldDirectory)) {
                state.getFileManager ().deleteFile (oldDirectory);
            }
        }

        droppedTables.clear ();

        for (String tableName : tableNames) {
            Path tableDirectory = state.getFileManager ().getCurrentDirectory ().resolve (tableName);

            state.getFileManager ().makeDirectory (tableDirectory);

            MultiTable currentTable = (MultiTable) tables.get (tableName);
            Set<String> keys = currentTable.getKeys ();

            for (String key : keys) {

                Path file = createFileForKey (key, tableDirectory);

                try (DataOutputStream out = new DataOutputStream (new BufferedOutputStream
                        (new FileOutputStream (file.toFile (),true)))) {

                    String value = currentTable.get (key);
                    DatabaseUtils.writeKeyValue (new DatabaseUtils.KeyValue (key, value), out);
                }
                catch (IOException e) {
                    throw new IOException ("Unable to write to file: " + e.getMessage ());
                }
            }
        }
    }

    @Override
    protected void getDatabaseFromDisk () throws IOException, IllegalArgumentException {
        File[] tableDirectories = state.getFileManager ().getCurrentDirectoryFiles ();

        for (File tableDirectory : tableDirectories) {
            if (!Files.isDirectory (tableDirectory.toPath ())) {
                continue;
            }

            Table table = createNewTable (tableDirectory.getName ());
            File[] directories = tableDirectory.listFiles ();

            for (File directory : directories) {

                if (!isValidDatabaseDirectoryName (directory.getName ())) {
                    continue;
                }
                File[] files = directory.listFiles ();

                for (File file : files) {
                    isFileCheck (file.toPath ());

                    if (!isValidDatabaseFileName (file.getName ())) {
                        continue;
                    }

                    try (DataInputStream in = new DataInputStream (new BufferedInputStream
                            (new FileInputStream (file)))) {
                        while (in.available () != 0) {
                            DatabaseUtils.KeyValue pair = DatabaseUtils.readKeyValue (in);
                            table.put (pair.key, pair.value);
                        }
                    }
                    catch (IOException e) {
                        throw new IOException ("Unable to write to file: " + e.getMessage ());
                    }
                }
            }

            tables.put (table.getName (), table);
        }
    }


}
