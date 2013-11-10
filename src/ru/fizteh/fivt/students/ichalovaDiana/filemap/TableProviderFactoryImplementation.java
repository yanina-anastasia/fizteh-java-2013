package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class TableProviderFactoryImplementation implements TableProviderFactory {
    
    public TableProviderFactoryImplementation() {}

    @Override
    public TableProvider create(String dir) {
        
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException("Invalid dir path");
        }
         
        Path dbDir = Paths.get(dir);
    
        if (!Files.isDirectory(dbDir)) {
            throw new IllegalArgumentException(dbDir + " doesn't exist or is not a directory");
        }
        
        isCorrectDatabaseDirectory(dbDir);
        
        TableProvider database = new TableProviderImplementation(dbDir);
        return database;
    }

    private static void isCorrectDatabaseDirectory(Path databaseDirectory) throws IllegalArgumentException {
        for (String dirName : databaseDirectory.toFile().list()) {
            if (Files.isDirectory(databaseDirectory.resolve(dirName))) {
                isCorrectTableDirectory(databaseDirectory.resolve(dirName));
            } else {
                throw new IllegalArgumentException("Invalid table format");
            }
        }   
    }
    
    private static void isCorrectTableDirectory(Path tableDirectory) throws IllegalArgumentException {
        for (String dirName : tableDirectory.toFile().list()) {
            if (!dirName.matches("(1[0-5]|[0-9]).dir")) {
                throw new IllegalArgumentException("Invalid table format");
            }
            Path fileDirectory = tableDirectory.resolve(dirName);
            if (!Files.isDirectory(fileDirectory)) {
                throw new IllegalArgumentException("Invalid table format");
            }
            for (String fileName : fileDirectory.toFile().list()) {
                if (!fileName.matches("(1[0-5]|[0-9]).dat")) {
                    throw new IllegalArgumentException("Invalid table format");
                }
                if (!Files.isRegularFile(fileDirectory.resolve(fileName))) {
                    throw new IllegalArgumentException("Invalid table format");
                }
                isCorrectTableFile(fileDirectory.resolve(fileName));
            }
        }   
    }
    
    private static void isCorrectTableFile(Path tableFile) throws IllegalArgumentException {
        try (FileDatabase currentDatabase = new FileDatabase(tableFile)) {
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while reading from file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
    }
    
}
