package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;
import ru.fizteh.fivt.students.fedoseev.filemap.AbstractFileMap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class AbstractMultiFileHashMap extends AbstractFrame<MultiFileHashMapState> {
    public AbstractMultiFileHashMap(File dir) throws IOException {
        state = new MultiFileHashMapState(dir);

        File newDir = state.getCurDir();

        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        if (newDir.listFiles() != null) {
            for (File file : newDir.listFiles()) {
                state.createTable(file.getName());
            }
        }
    }

    @Override
    public Map<String, AbstractCommand> getCommands() {
        final MultiFileHashMapCreateCommand CREATE = new MultiFileHashMapCreateCommand();
        final MultiFileHashMapDropCommand DROP = new MultiFileHashMapDropCommand();
        final MultiFileHashMapUseCommand USE = new MultiFileHashMapUseCommand();
        final MultiFileHashMapPutCommand PUT = new MultiFileHashMapPutCommand();
        final MultiFileHashMapGetCommand GET = new MultiFileHashMapGetCommand();
        final MultiFileHashMapRemoveCommand REMOVE = new MultiFileHashMapRemoveCommand();
        final MultiFileHashMapExitCommand EXIT = new MultiFileHashMapExitCommand();
        final MultiFileHashMapSizeCommand SIZE = new MultiFileHashMapSizeCommand();
        final MultiFileHashMapCommitCommand COMMIT = new MultiFileHashMapCommitCommand();
        final MultiFileHashMapRollbackCommand ROLLBACK = new MultiFileHashMapRollbackCommand();

        return new HashMap<String, AbstractCommand>() {{
            put(CREATE.getCmdName(), CREATE);
            put(DROP.getCmdName(), DROP);
            put(USE.getCmdName(), USE);
            put(PUT.getCmdName(), PUT);
            put(GET.getCmdName(), GET);
            put(REMOVE.getCmdName(), REMOVE);
            put(EXIT.getCmdName(), EXIT);
            put(SIZE.getCmdName(), SIZE);
            put(COMMIT.getCmdName(), COMMIT);
            put(ROLLBACK.getCmdName(), ROLLBACK);
        }};
    }

    public static void readTableOff(MultiFileHashMapTable table) throws IOException {
        File curDir = new File(table.getName());

        if (curDir.listFiles() != null) {
            for (File dir : curDir.listFiles()) {
                if (dir.listFiles() != null) {
                    for (File file : dir.listFiles()) {
                        RandomAccessFile raf = new RandomAccessFile(file, "r");

                        checkOpenFile(table, raf);
                    }
                }
            }
        }
    }

    public static void checkOpenFile(MultiFileHashMapTable curTable, RandomAccessFile curFile) throws IOException {
        curTable.ifUnfitCurFileSize(curFile);
        curTable.ifUnfitCurTableSize();

        curTable.setTableSize(curTable.getTableSize() + curFile.length());

        Map<String, String> map = AbstractFileMap.readFile(curFile);
        curTable.putMapTable(map);

        curFile.close();
    }

    public static void saveTable(MultiFileHashMapTable curTable) throws IOException {
        if (curTable == null) {
            return;
        }

        curTable.setTableSize(0);

        File[] directories = new File[curTable.getDirsNumber()];
        Map<Integer, File> files = new HashMap<Integer, File>();
        Map<Integer, RandomAccessFile> RAFiles = new HashMap<Integer, RandomAccessFile>();
        Set<String> keySet = curTable.getMapContent().keySet();

        curTable.setUsedDirs();

        for (int i = 0; i < curTable.getDirsNumber(); i++) {
            directories[i] = curTable.getCurTableDir().toPath().resolve(i + ".dir").toFile();

            if (!directories[i].exists() && curTable.getBoolUsedDirs()[i]) {
                directories[i].mkdirs();
            }

            for (String key : keySet) {
                if (curTable.getBoolUsedDirs()[i]) {
                    curTable.getBoolUsedFiles()[i][curTable.fileHash(key)] = true;
                }
            }

            for (int j = 0; j < curTable.getDirFilesNumber(); j++) {
                if (curTable.getBoolUsedFiles()[i][j]) {
                    File curFile = new
                            File(curTable.getCurTableDir().toPath()
                            .resolve(i + ".dir").resolve(j + ".dat").toFile().toString());

                    int numb = curTable.getDirsNumber() * i + j;
                    files.put(numb, curFile);

                    if (!files.get(numb).exists()) {
                        files.get(numb).createNewFile();
                    }

                    RandomAccessFile curRAFile = new RandomAccessFile(files.get(numb), "rw");

                    RAFiles.put(numb, curRAFile);
                }
            }
        }

        Set<String> curFileKeySet = new HashSet<String>();

        for (int i = 0; i < curTable.getDirsNumber(); i++) {
            for (int j = 0; j < curTable.getDirFilesNumber(); j++) {
                if (curTable.getBoolUsedFiles()[i][j]) {
                    RandomAccessFile raf = RAFiles.get(curTable.getDirsNumber() * i + j);

                    curFileKeySet.clear();

                    for (String curFileKey : keySet) {
                        if (j == curTable.fileHash(curFileKey) && i == curTable.dirHash(curFileKey)) {
                            curFileKeySet.add(curFileKey);
                        }
                    }

                    AbstractFileMap.commitFile(raf, curFileKeySet, curTable.getMapContent());
                }
            }
        }

        curTable.clearUsedDirs();
        curTable.clearUsedFiles();

        for (int i : RAFiles.keySet()) {
            RAFiles.get(i).close();
        }

        for (int i : files.keySet()) {
            if (files.get(i).length() == 0) {
                files.get(i).delete();
            }
        }

        for (File dir : directories) {
            if (dir.exists()) {
                if (dir.listFiles().length == 0) {
                    dir.delete();
                }
            }
        }
    }
}
