package ru.fizteh.fivt.students.valentinbarishev.filemap;

import java.io.File;
import java.io.IOException;
import ru.fizteh.fivt.storage.strings.Table;

public final class DataBase implements Table {

    private String name;
    private String dataBaseDirectory;
    private DataBaseFile[] files;

    public final class DirFile {
        private int nDir;
        private int nFile;

        public DirFile(int key) {
            key = Math.abs(key);
            nDir = key % 16;
            nFile = (key / 16) % 16;
        }

        public DirFile(final int newDir, final int newFile) {
            nDir = newDir;
            nFile = newFile;
        }

        private String getNDirectory() {
            return Integer.toString(nDir) + ".dir";
        }

        private String getNFile() {
            return Integer.toString(nFile) + ".dat";
        }

        public  int getId() {
            return nDir * 16 + nFile;
        }
    }

    public DataBase(final String dbDirectory) {
        name = new File(dbDirectory).getName();
        dataBaseDirectory = dbDirectory;
        isCorrect();
        files = new DataBaseFile[256];
        loadFiles();
    }

    private void checkNames(final String[] dirs, final String secondName) {
        for (int i = 0; i < dirs.length; ++i) {
            String[] name = dirs[i].split("\\.");
            if (name.length != 2 || !name[1].equals(secondName)) {
                throw new MultiDataBaseException(dataBaseDirectory + " wrong file in path " + dirs[i]);
            }

            int firstName;
            try {
                firstName = Integer.parseInt(name[0]);
            } catch (NumberFormatException e) {
                throw new MultiDataBaseException(dataBaseDirectory +  " wrong file first name " + dirs[i]);
            }

            if ((firstName < 0) || firstName > 15) {
                throw new MultiDataBaseException(dataBaseDirectory + " wrong file first name " + dirs[i]);
            }
        }
    }

    private void isCorrectDirectory(final String dirName) {
        File file = new File(dirName);
        if (file.isFile()) {
            throw new MultiDataBaseException(dirName + " isn't a directory!");
        }
        String[] dirs = file.list();
        checkNames(dirs, "dat");
        for (int i = 0; i  < dirs.length; ++i) {
            if (new File(dirName, dirs[i]).isDirectory()) {
                throw new MultiDataBaseException(dirName + File.separator + dirs[i] + " isn't a file!");
            }
        }
    }

    private void isCorrect() {
        File file = new File(dataBaseDirectory);
        if (file.isFile()) {
            throw new MultiDataBaseException(dataBaseDirectory + " isn't directory!");
        }

        String[] dirs = file.list();
        checkNames(dirs, "dir");
        for (int i = 0; i < dirs.length; ++i) {
            isCorrectDirectory(dataBaseDirectory + File.separator + dirs[i]);
        }
    }

    private void tryAddDirectory(final String name) {
        File file = new File(dataBaseDirectory + File.separator + name);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new DataBaseException("Cannot create a directory!");
            }
        }
    }

    private void tryDeleteDirectory(final String name) {
        File file = new File(dataBaseDirectory + File.separator + name);
        if (file.exists()) {
            if (file.list().length == 0) {
                if (!file.delete()) {
                    throw new DataBaseException("Cannot delete a directory!");
                }
            }
        }
    }

    private String getFullName(final DirFile node) {
        return dataBaseDirectory + File.separator + node.getNDirectory() + File.separator + node.getNFile();
    }

    public void loadFiles() {
        try {
            for (int i = 0; i < 16; ++i) {
                tryAddDirectory(Integer.toString(i) + ".dir");
                for (int j = 0; j < 16; ++j) {
                    DirFile node = new DirFile(i, j);
                    DataBaseFile file = new DataBaseFile(getFullName(node), node.nDir, node.nFile);
                    files[node.getId()] =  file;
                }
            }
        } catch (DataBaseWrongFileFormat e) {
            save();
            throw e;
        }
    }

    private void checkKey(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null pointer key!");
        }
    }

    public void drop() {
        for (byte i = 0; i < 16; ++i) {
            for (byte j = 0; j < 16; ++j) {
                File file = new File(getFullName(new DirFile(i, j)));
                if (file.exists()) {
                    if (!file.delete()) {
                        throw new DataBaseException("Cannot delete a file!");
                    }
                }
            }
            tryDeleteDirectory(Integer.toString(i) + ".dir");
        }
    }

    public void save() {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (files[new DirFile(i, j).getId()] != null) {
                    files[new DirFile(i, j).getId()].save();
                }
            }
            tryDeleteDirectory(Integer.toString(i) + ".dir");
        }
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String put(final String keyStr, final String valueStr) {
        checkKey(keyStr);
        DirFile node = new DirFile(keyStr.getBytes()[0]);
        DataBaseFile file = files[node.getId()];
        return file.put(keyStr, valueStr);
    }

    @Override
    public String get(final String keyStr) {
        checkKey(keyStr);
        DirFile node = new DirFile(keyStr.getBytes()[0]);
        DataBaseFile file = files[node.getId()];
        return file.get(keyStr);
    }

    @Override
    public String remove(final String keyStr) {
        checkKey(keyStr);
        DirFile node = new DirFile(keyStr.getBytes()[0]);
        DataBaseFile file = files[node.getId()];
        return file.remove(keyStr);
    }

    @Override
    public int commit() {
        int allNew = 0;
        for (int i = 0; i < 256; ++i) {
            allNew += files[i].getNewKeys();
            files[i].commit();
        }
        return allNew;
    }

    @Override
    public int size() {
        int allSize = 0;
        for (int i = 0; i < 256; ++i) {
                allSize += files[i].getSize();
        }
        return allSize;
    }

    @Override
    public int rollback() {
        int allCanceled = 0;
        for (int i = 0; i < 256; ++i) {
            allCanceled += files[i].getNewKeys();
            files[i].rollback();
        }
        return allCanceled;
    }

    public int getNewKeys() {
        int allNewSize = 0;
        for (int i = 0; i < 256; ++i) {
            allNewSize += files[i].getNewKeys();
        }
        return allNewSize;
    }
}
