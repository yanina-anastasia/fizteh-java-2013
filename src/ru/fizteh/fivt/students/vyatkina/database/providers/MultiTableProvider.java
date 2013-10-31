package ru.fizteh.fivt.students.vyatkina.database.providers;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseUtils;
import ru.fizteh.fivt.students.vyatkina.database.Diff;
import ru.fizteh.fivt.students.vyatkina.database.tables.MultiTable;

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

    protected Map<String, MultiTable> tables = new HashMap<> ();

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


    private Path createFileForKeyIfNotExists (String key, Path tablePath) throws IOException {
        byte keyByte = (byte) Math.abs (key.getBytes (StandardCharsets.UTF_8)[0]);
        int ndirectory = keyByte % NUMBER_OF_DIRECTORIES;
        int nfile = (keyByte / NUMBER_OF_DIRECTORIES) % NUMBER_OF_FILES;

        Path directory = tablePath.resolve (Paths.get (ndirectory + DOT_DIR));
        if (Files.notExists (directory)) {
            Files.createDirectory (directory);
        }
        Path file = directory.resolve (Paths.get (nfile + DOT_DAT));
        if (Files.notExists (file)) {
            Files.createFile (file);
        }
        return file;

    }

    private Path deleteFileForKey (String key, Path tablePath) throws IOException {
        byte keyByte = (byte) Math.abs (key.getBytes (StandardCharsets.UTF_8)[0]);
        int ndirectory = keyByte % NUMBER_OF_DIRECTORIES;
        int nfile = (keyByte / NUMBER_OF_DIRECTORIES) % NUMBER_OF_FILES;

        Path directory = tablePath.resolve (Paths.get (ndirectory + DOT_DIR));
        Path file = directory.resolve (Paths.get (nfile + DOT_DAT));
        Files.deleteIfExists (file);
        return file;
    }

    protected MultiTable createNewTable (String tableName) {
        return new MultiTable (tableName, new HashMap<String, Diff<String>> (), this);
    }

    @Override
    public Table createTable (String tableName) {
        validTableNameCheck (tableName);
        if (tables.containsKey (tableName)) {
            return null;
        } else {
            Path tablePath = state.getFileManager ().getCurrentDirectory ().resolve (tableName);
            try {
                state.getFileManager ().makeDirectory (tablePath);
            }
            catch (IOException e) {
                throw new IllegalArgumentException (e.getMessage ());
            }
            MultiTable newTable = createNewTable (tableName);
            tables.put (newTable.getName (), newTable);
            return newTable;
        }
    }

    @Override
    public void removeTable (String tableName) {
        validTableNameCheck (tableName);
        Table table = tables.remove (tableName);
        if (table != null) {
            Path tablePath = state.getFileManager ().getCurrentDirectory ().resolve (tableName);
            if (Files.exists (tablePath)) {
                try {
                    state.getFileManager ().deleteFile (tablePath);
                }
                catch (IOException e) {
                    throw new IllegalArgumentException (e.getMessage ());
                }
            }
        }
        if (table == null) {
            throw new IllegalStateException ("Try to delete unknown table");
        }
    }

    @Override
    public Table getTable (String tableName) throws IllegalArgumentException {
        validTableNameCheck (tableName);
        return tables.get (tableName);
    }

    private Set<Path> deleteFilesThatChanged (MultiTable table) throws IOException {
        Set<Path> paths = new HashSet<> ();
        Path tablePath = state.getFileManager ().getCurrentDirectory ().resolve (table.getName ());
        if (Files.notExists (tablePath)) {
            Files.createDirectory (tablePath);
        }
        for (String key : table.getKeysThatValuesHaveChanged ()) {
            paths.add (deleteFileForKey (key, tablePath));
        }

        return paths;
    }


    private void rewriteFilesThatChanged (MultiTable table, Set<Path> filesChanged) throws IOException {
        Path tablePath = state.getFileManager ().getCurrentDirectory ().resolve (table.getName ());
        if (Files.notExists (tablePath)) {
            Files.createDirectory (tablePath);
        }

        for (String key : table.getKeys ()) {

            Path file = createFileForKeyIfNotExists (key, tablePath);
            if (filesChanged.contains (file)) {

                try (DataOutputStream out = new DataOutputStream (new BufferedOutputStream
                        (new FileOutputStream (file.toFile (), true)))) {

                    String value = table.get (key);
                    if (value != null) {
                        DatabaseUtils.writeKeyValue (new DatabaseUtils.KeyValue (key, value), out);
                    }
                }
                catch (IOException e) {
                    throw new IOException ("Unable to write to file: " + e.getMessage ());
                }
            }
        }


    }


    public void writeTableOnDisk (MultiTable table) throws IOException, IllegalArgumentException {

        Set<Path> filesThatChanged = deleteFilesThatChanged (table);
        rewriteFilesThatChanged (table, filesThatChanged);

    }

    @Override
    protected void getDatabaseFromDisk () throws IOException {
        File[] tableDirectories = state.getFileManager ().getCurrentDirectoryFiles ();

        for (File tableDirectory : tableDirectories) {
            if (!Files.isDirectory (tableDirectory.toPath ())) {
                continue;
            }

            MultiTable table = createNewTable (tableDirectory.getName ());
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
                            table.putValueFromDisk (pair.key, pair.value);
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
