package ru.fizteh.fivt.students.dmitryKonturov.dataBase;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Проверки:
 *    1) Директория содержит только базы данных.
 *    2) База данных это директория, которая содержит только
 *       поддиректории вида 0.dir, 1.dir, ... , 15.dir
 *    3) Поддириктории в базе данных содержат только файлы вида
 *       0.dat, 1.dat, ... , 15.dat
 *
 *
 */

class CheckDatabasesWorkspace {

    private static int getNumberFromPath(Path file) throws DatabaseException {
        try {
            String fileName = file.toFile().getName();
            int result = 0;
            if (Character.isDigit(fileName.charAt(0))) {
                result = result * 10 + Character.digit(fileName.charAt(0), 10);
            }
            if (Character.isDigit(fileName.charAt(1))) {
                result = result * 10 + Character.digit(fileName.charAt(1), 10);
            }
            return result;
        } catch (Exception e) {
            throw new DatabaseException(e.toString());
        }
    }

    static void checkFile(Path file, int ndir) throws DatabaseException {
        SimpleDatabase tmpBase = new SimpleDatabase();
        int nfile = getNumberFromPath(file);
        SimpleDatabaseLoaderWriter.databaseLoadFromFile(tmpBase, file);
        Set<Map.Entry<String, Object>> set = tmpBase.getEntries();
        for (Map.Entry<String, Object> entry : set) {
            int hash = Math.abs(entry.getKey().hashCode());
            boolean ok = ((hash % 16 == ndir) && (hash / 16 % 16 == nfile));
            if (!ok) {
                throw new DatabaseException("Check file " + file.toFile().getName(), "Invalid file format");
            }
        }
    }

    static void checkDatabaseSubdirectory(Path subDir) throws DatabaseException {
        try {
            if (!Files.isDirectory(subDir)) {
                throw new DatabaseException("Check", "subdirectory is not directory");
            }
        } catch (SecurityException e) {
            throw new DatabaseException("Check", "Security problems");
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(subDir)) {
            String regexp = "([0-9]|1[0-5])[.]dat";
            for (Path entry : stream) {
                String dirName = entry.toFile().getName();
                if (!Pattern.matches(regexp, dirName)) {
                    throw new DatabaseException(String.format("Wrong database subdirectory %s", subDir.toString()),
                            "SubDirectory contains not only 0.dat, ..., 15.dat files: " + dirName);
                }
                if (!Files.isRegularFile(entry)) {
                    throw new DatabaseException("Wrong database filename", entry.toString());
                }
                if (!(Files.isReadable(entry) && Files.isWritable(entry))) {
                    throw new DatabaseException("Wrong database", "Not enough rights to read/write");
                }
                checkFile(entry, getNumberFromPath(subDir));
            }
        } catch (Exception ioException) {
            throw new DatabaseException("Check subdirectory" + subDir.toFile().getName(), ioException.toString());
        }
    }

    static void checkDatabaseDirectory(Path databaseDir) throws DatabaseException {
        try {
            if (!Files.isDirectory(databaseDir)) {
                throw new DatabaseException("Check database", "database is not directory");
            }
        } catch (SecurityException e) {
            throw new DatabaseException("Check database", "Security problems");
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(databaseDir)) {
            String regexp = "([0-9]|1[0-5])[.]dir";
            for (Path entry : stream) {
                String dirName = entry.toFile().getName();
                if (!Pattern.matches(regexp, dirName)) {
                    throw new DatabaseException(String.format("Wrong database %s", databaseDir.toString()),
                                             "Directory contains not only 0.dir, ..., 15.dir files: " + dirName);
                }
                checkDatabaseSubdirectory(entry);
            }
        } catch (Exception ioException) {
            throw new DatabaseException("Check database" + databaseDir.toFile().getName(), ioException.toString());
        }
    }

    static void checkWorkspace(Path workspace) throws DatabaseException {
        try {
            if (!Files.isDirectory(workspace)) {
                throw new DatabaseException("Check workspace", "Workspace is not directory");
            }
        } catch (SecurityException e) {
            throw new DatabaseException("Check workspace", "Security problems");
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(workspace)) {
            for (Path entry : stream) {
                checkDatabaseDirectory(entry);
            }
        } catch (Exception ioException) {
            throw new DatabaseException("Check workspace", ioException.toString());
        }
    }
}
