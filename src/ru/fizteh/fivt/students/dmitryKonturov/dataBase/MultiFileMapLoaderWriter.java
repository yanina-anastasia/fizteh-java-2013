
package ru.fizteh.fivt.students.dmitryKonturov.dataBase;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;


public class MultiFileMapLoaderWriter {

    private static void loadDatabaseSubdirectory(Path subDir, SimpleDatabase base) throws DatabaseException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(subDir)) {
            for (Path entry : stream) {
                SimpleDatabaseLoaderWriter.databaseLoadFromFile(base, entry);
            }
        } catch (DatabaseException dbe) {
            throw dbe;
        } catch (Exception ioException) {
            throw new DatabaseException("Fail to load" + subDir.toString(), ioException.toString());
        }
    }

    static void loadDatabase(Path workspace, String baseName, SimpleDatabase base) throws DatabaseException {
        Path baseDir;
        try {
            baseDir = workspace.resolve(baseName);
            CheckDatabasesWorkspace.checkDatabaseDirectory(baseDir);
        } catch (Exception e) {
            throw new DatabaseException("Load", e.toString());
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(baseDir)) {
            for (Path entry : stream) {
                loadDatabaseSubdirectory(entry, base);
            }
        } catch (DatabaseException dbe) {
          throw dbe;
        } catch (Exception ioException) {
            throw new DatabaseException("Load", ioException.toString());
        }
    }

    private static void writeToSubdir(Path subdir, SimpleDatabase base) throws DatabaseException {
        boolean toDelete = true;
        try {
            if (!Files.exists(subdir)) {
                Files.createDirectory(subdir);
            }
            SimpleDatabase[] bases = new SimpleDatabase[16];
            for (int i = 0; i < 16; ++i) {
                bases[i] = new SimpleDatabase();
            }
            Set<Map.Entry<String, Object>> set = base.getEntries();
            for (Map.Entry<String, Object> entry : set) {
                int hash = Math.abs(entry.getKey().hashCode()) / 16;
                bases[hash % 16].put(entry.getKey(), entry.getValue());
            }
            for (int i = 0; i < 16; ++i) {
                Path file = subdir.resolve(String.format("%d.dat", i));
                SimpleDatabaseLoaderWriter.databaseWriteToFile(bases[i], file);
                if (Files.size(file) == 0) {
                    Files.delete(file);
                } else {
                    toDelete = false;
                }
            }

        } catch (IOException e) {
            System.err.println("\"IOexception: could not continue to work");
            System.exit(1);
        }
        if (toDelete) {
            try {
                Files.deleteIfExists(subdir);
            } catch (IOException e) {
                System.err.println("IOexception: could not continue to work");
                System.exit(1);
            }
        }
    }

    static void writeDatabase(Path workspace, String baseName, SimpleDatabase base) throws DatabaseException {
        try {
            SimpleDatabase[] bases = new SimpleDatabase[16];
            for (int i = 0; i < 16; ++i) {
                bases[i] = new SimpleDatabase();
            }
            Set<Map.Entry<String, Object>> set = base.getEntries();
            for (Map.Entry<String, Object> entry : set) {
                int hash = Math.abs(entry.getKey().hashCode());
                //System.out.println(entry.getKey() + " <-:-> " + entry.getValue());
                bases[hash % 16].put(entry.getKey(), entry.getValue());
            }
            Path baseDir = workspace.resolve(baseName);
            for (int i = 0; i < 16; ++i) {
                writeToSubdir(baseDir.resolve(String.format("%d.dir", i)), bases[i]);
            }
        } catch (DatabaseException dbe) {
            throw dbe;
        } catch (Exception e) {
            throw new DatabaseException("Something go wrong", e.toString());
        }
    }
}
