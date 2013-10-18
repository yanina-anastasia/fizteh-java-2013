package ru.fizteh.fivt.students.vyatkina.database;

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
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class SingleTable implements Table {

    private final Path currentDirectory;
    private final Path fileName = Paths.get ("db.dat");
    private Map<String, String> values = new HashMap<> ();
    private final String name;

    public SingleTable (String name, Path currentDirectory) {
        this.name = name;
        this.currentDirectory = currentDirectory;
        getDatabaseFromDisk ();
    }

    public String getName () {
        return name;
    }

    public String get (String key) throws IllegalArgumentException {
        return values.get (key);
    }

    public String put (String key, String value) {
        return values.put (key, value);
    }

    public String remove (String key) {
        return values.remove (key);
    }

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

    public void writeDatabaseOnDisk ()  throws RuntimeException {
        Path databaseStorage = currentDirectory.resolve (fileName);

        try {
            Files.deleteIfExists (databaseStorage);
            Files.createFile (databaseStorage);
        }
        catch (IOException e) {
            throw new RuntimeException ("Unable to rewrite file: " + e.getMessage () );
        }

        try (DataOutputStream out = new DataOutputStream (new BufferedOutputStream
                (new FileOutputStream (databaseStorage.toFile ())))) {
            Set<String> keys = values.keySet ();
            for (String key : keys) {
                String value = values.get (key);
                byte [] keyBytes = key.getBytes ("UTF-8");
                byte [] valueBytes = value.getBytes ("UTF-8");
                out.writeInt (keyBytes.length);
                out.writeInt (valueBytes.length);
                out.write (keyBytes);
                out.write (valueBytes);
            }
        }
        catch (IOException e) {
            throw new RuntimeException ("Unable to write to file: " + e.getMessage () );
        }
    }

    private void getDatabaseFromDisk () throws RuntimeException {
        Path databaseStorage = currentDirectory.resolve (fileName);
        if (Files.notExists (databaseStorage)) {
            try {
                Files.createFile (databaseStorage);
                return;
            }
            catch (IOException e) {
                throw new RuntimeException ("Unable to create new database file: " + e.getMessage ());
            }
        }

        try (DataInputStream in = new DataInputStream (new BufferedInputStream
                (new FileInputStream (databaseStorage.toFile ())))) {

            while (in.available () != 0) {
                int keySize = in.readInt () ;
                int valueSize = in.readInt () ;
                byte [] keyBytes = new byte [keySize];
                byte [] valueBytes = new byte [valueSize];
                in.read (keyBytes);
                in.read (valueBytes);
                String key = new String (keyBytes,"UTF-8");
                String value = new String (valueBytes, "UTF-8");
                values.put (key,value);
            }
        }
        catch (IOException e) {
            throw new RuntimeException ("Unable to read from file: " + e.getMessage () );
        }
    }
}
