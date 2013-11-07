package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TableProviderFactoryImplementation implements TableProviderFactory {
    
    public TableProviderFactoryImplementation() {}

    @Override
    public TableProvider create(String dir) throws IOException {
        
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
        if (tableDirectory.toFile().list().length == 0) {
            throw new IllegalArgumentException("Invalid table format");
        }
        
        boolean containsSignatureFile = false;
        for (String dirName : tableDirectory.toFile().list()) {
            if (dirName.equals("signature.tsv") && Files.isRegularFile(tableDirectory.resolve(dirName))) {
                containsSignatureFile = true;
                continue; // TODO: check signature.tsv;
            }
            
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

        if (!containsSignatureFile) {
            throw new IllegalArgumentException("Invalid table format");
        }
    }
    
    private static void isCorrectTableFile(Path tableFile) throws IllegalArgumentException {
        if (tableFile.toFile().length() == 0) {
            throw new IllegalArgumentException("Invalid table format");
        }
        try (FileDatabase currentDatabase = new FileDatabase(tableFile)) {
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while reading from file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
    }
    
}
