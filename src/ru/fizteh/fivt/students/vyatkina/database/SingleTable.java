package ru.fizteh.fivt.students.vyatkina.database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ru.fizteh.fivt.storage.strings.Table;

public class SingleTable implements Table {

    private final Path currentDirectory;
    private final Path fileName = Paths.get ("db.dat");
    private Map<String, String> values = new HashMap<> ();
    private final String name;
    private final Charset CHARSET = StandardCharsets.UTF_8;
    private final int MAX_SUPPORTED_SIZE = 1024 * 1024;

    public SingleTable (String name, Path currentDirectory) throws IOException {
        this.name = name;
        this.currentDirectory = currentDirectory;
        getDatabaseFromDisk ();
    }
    @Override
    public String getName () {
        return name;
    }
    @Override
    public String get (String key) throws IllegalArgumentException {
        return values.get (key);
    }
    @Override
    public String put (String key, String value) {
        return values.put (key, value);
    }
    @Override
    public String remove (String key) {
        return values.remove (key);
    }
   @Override
   public int size () {
        return 0;
    }

    @Override
    public int commit () {
        return 0;
    }

    @Override
    public int rollback () {
        return 0;
    }

    private boolean isValidSize (int size) {
        return ((size <= MAX_SUPPORTED_SIZE) && (size > 0));
    }

    public void writeDatabaseOnDisk ()  throws IOException {
        Path databaseStorage = currentDirectory.resolve (fileName);

        try {
            Files.deleteIfExists (databaseStorage);
            Files.createFile (databaseStorage);
        }
        catch (IOException e) {
            throw new IOException ("Unable to rewrite file: " + e.getMessage () );
        }

        try (DataOutputStream out = new DataOutputStream (new BufferedOutputStream
                (new FileOutputStream (databaseStorage.toFile ())))) {
            Set<String> keys = values.keySet ();
            for (String key : keys) {
                String value = values.get (key);
                byte [] keyBytes = key.getBytes (CHARSET);
                byte [] valueBytes = value.getBytes (CHARSET);
                out.writeInt (keyBytes.length);
                out.writeInt (valueBytes.length);
                out.write (keyBytes);
                out.write (valueBytes);
            }
        }
        catch (IOException e) {
            throw new IOException ("Unable to write to file: " + e.getMessage () );
        }
    }

    private void getDatabaseFromDisk () throws IOException {
        Path databaseStorage = currentDirectory.resolve (fileName);
        if (Files.notExists (databaseStorage)) {
            try {
                Files.createFile (databaseStorage);
                return;
            }
            catch (IOException e) {
                throw new IOException ("Unable to create new database file: " + e.getMessage ());
            }
        }

        try (DataInputStream in = new DataInputStream (new BufferedInputStream
                (new FileInputStream (databaseStorage.toFile ())))) {

            while (in.available () != 0) {
                int keySize = in.readInt () ;
                int valueSize = in.readInt () ;
                if (!(isValidSize (keySize) && isValidSize (valueSize))) {
                    throw new IOException ("Invalid key or value size");
                }
                byte [] keyBytes = new byte [keySize];
                byte [] valueBytes = new byte [valueSize];
                in.read (keyBytes);
                in.read (valueBytes);
                String key = new String (keyBytes, CHARSET);
                String value = new String (valueBytes, CHARSET);
                values.put (key,value);
            }
        }
        catch (IOException e) {
            throw new IOException ("Unable to read from file: " + e.getMessage () );
        }
    }
}
