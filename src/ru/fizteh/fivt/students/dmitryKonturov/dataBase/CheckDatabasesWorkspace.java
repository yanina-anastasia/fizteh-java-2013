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

    /**
     * Возвращает двузначный номер файла, если файл начинается на (1[0-5]|[0-9])
     */
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
        String exceptionPrefix = String.format("Check \'%s'\' file", file.getFileName().toString());
        try {
            SimpleDatabase tmpBase = new SimpleDatabase();
            int nfile = getNumberFromPath(file);
            SimpleDatabaseLoaderWriter.databaseLoadFromFile(tmpBase, file);
            Set<Map.Entry<String, Object>> set = tmpBase.getEntries();
            for (Map.Entry<String, Object> entry : set) {
                int hash = Math.abs(entry.getKey().hashCode());
                boolean ok = ((hash % 16 == ndir) && (hash / 16 % 16 == nfile));
                if (!ok) {
                    throw new DatabaseException(exceptionPrefix, "Invalid file format");
                }
            }
        } catch (DatabaseException dbe) {
            throw dbe;
        } catch (Exception exc) {
            throw new DatabaseException(exceptionPrefix, exc);
        }
    }

    static void checkDatabaseSubdirectory(Path subDir) throws DatabaseException {
        String exceptionPrefix = String.format("Check subdirectory \'%s\'", subDir.getFileName().toString());
        try {
            if (!Files.isDirectory(subDir)) {
                throw new DatabaseException(exceptionPrefix, "Subdirectory is not directory");
            }
        } catch (SecurityException secExc) {
            throw new DatabaseException(exceptionPrefix, secExc);
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(subDir)) {
            String regexp = "([0-9]|1[0-5])[.]dat";
            for (Path entry : stream) {
                String dirName = entry.toFile().getName();
                if (!Pattern.matches(regexp, dirName)) {
                    throw new DatabaseException(exceptionPrefix, String.format("Contains not only 0.dat, ..., "
                                                                 + "15.dat files: %s found", dirName));
                }
                if (!Files.isRegularFile(entry)) {
                    throw new DatabaseException(exceptionPrefix, String.format("Found not regular file \'%s\'",
                                                                 entry.getFileName().toString()));
                }
                if (!(Files.isReadable(entry) && Files.isWritable(entry))) {
                    throw new DatabaseException(exceptionPrefix, "Not enough rights to read/write internal files");
                }
                try {
                    checkFile(entry, getNumberFromPath(subDir));
                } catch (DatabaseException dbe) {
                    throw new DatabaseException(exceptionPrefix, dbe);
                }
            }
        } catch (DatabaseException dbe) {
            throw dbe;
        } catch (Exception exc) {
            throw new DatabaseException(exceptionPrefix, exc);
        }
    }

    static void checkDatabaseDirectory(Path databaseDir) throws DatabaseException {
        String exceptionPrefix = String.format("Check database directory \'%s\'", databaseDir.toString());
        try {
            if (!Files.isDirectory(databaseDir)) {
                throw new DatabaseException(exceptionPrefix, "Not a directory or not exists");
            }
        } catch (SecurityException e) {
            throw new DatabaseException(exceptionPrefix, "Security problems");
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(databaseDir)) {
            String regexp = "([0-9]|1[0-5])[.]dir";
            for (Path entry : stream) {
                String dirName = entry.toFile().getName();
                if (!Pattern.matches(regexp, dirName)) {
                    throw new DatabaseException(exceptionPrefix, "Directory contains not only 0.dir, ..., "
                                                                 + "15.dir files: " + dirName);
                }
                try {
                    checkDatabaseSubdirectory(entry);
                } catch (DatabaseException dbe) {
                    throw new DatabaseException(exceptionPrefix, dbe);
                }
            }
        } catch (DatabaseException dbe) {
            throw dbe;
        } catch (Exception exception) {
            throw new DatabaseException(exceptionPrefix, exception);
        }
    }

    static void checkWorkspace(Path workspace) throws DatabaseException {
        String exceptionPrefix = String.format("Check workspace \'%s\'", workspace.toString());
        try {
            if (!Files.isDirectory(workspace)) {
                throw new DatabaseException(exceptionPrefix, "Workspace is not directory");
            }
        } catch (SecurityException e) {
            throw new DatabaseException(exceptionPrefix, "Security problems");
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(workspace)) {
            for (Path entry : stream) {
                try {
                    checkDatabaseDirectory(entry);
                } catch (DatabaseException dbe) {
                    throw new DatabaseException(exceptionPrefix, dbe);
                }
            }
        } catch (DatabaseException dbe) {
            throw dbe;
        } catch (Exception ioException) {
            throw new DatabaseException(exceptionPrefix, ioException.toString());
        }
    }
}
