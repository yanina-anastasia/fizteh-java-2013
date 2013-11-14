package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

public class AbstractStoreable extends AbstractFrame<StoreableState> {
    public AbstractStoreable(File dir) throws IOException, ClassNotFoundException {
        state = new StoreableState(dir);

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
        final StoreableCreateCommand CREATE = new StoreableCreateCommand();
        final StoreableDropCommand DROP = new StoreableDropCommand();
        final StoreableUseCommand USE = new StoreableUseCommand();
        final StoreablePutCommand PUT = new StoreablePutCommand();
        final StoreableGetCommand GET = new StoreableGetCommand();
        final StoreableRemoveCommand REMOVE = new StoreableRemoveCommand();
        final StoreableExitCommand EXIT = new StoreableExitCommand();
        final StoreableSizeCommand SIZE = new StoreableSizeCommand();
        final StoreableCommitCommand COMMIT = new StoreableCommitCommand();
        final StoreableRollbackCommand ROLLBACK = new StoreableRollbackCommand();

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

    public static void readTableOff(StoreableTable curTable) throws IOException, ParseException {
        File curDir = new File(curTable.getName());

        if (curDir.listFiles() != null) {
            for (File dir : curDir.listFiles()) {
                if (dir.listFiles() != null) {
                    if (dir.listFiles().length == 0) {
                        throw new IOException("ERROR: empty table directory");
                    }

                    for (File file : dir.listFiles()) {
                        RandomAccessFile raf = new RandomAccessFile(file, "r");

                        checkOpenFile(curTable, raf);
                    }
                }
            }
        }

        curTable.commit();
    }

    public static void checkOpenFile(StoreableTable curTable, RandomAccessFile curFile)
            throws IOException, ParseException {
        curTable.checkFile(curFile);
        curTable.checkTable();

        curTable.setTableSize(curTable.getTableSize() + curFile.length());

        Map<String, Storeable> map = readFile(curTable, curFile);
        curTable.putMapTable(map);

        curFile.close();
    }

    public static Map<String, Storeable> readFile(StoreableTable curTable, RandomAccessFile file)
            throws IOException, ParseException {
        Map<String, Storeable> map = new HashMap<>();

        if (file.length() == 0) {
            return null;
        }

        List<Integer> offsets = new ArrayList<>();

        while (file.getFilePointer() != file.length()) {
            if (file.readByte() == '\0') {
                int offset = file.readInt();

                if (offset < 0 || offset > file.length()) {
                    file.close();
                    throw new IOException("ERROR: incorrect input");
                }

                offsets.add(offset);
            }
        }
        offsets.add((int) file.length());

        file.seek(0);

        for (int i = 0; i < offsets.size() - 1; ++i) {
            List<Byte> bytesKeyList = new ArrayList<>();

            while (file.getFilePointer() != file.length()) {
                byte b = file.readByte();

                if (b == 0) {
                    break;
                }

                bytesKeyList.add(b);
            }

            byte[] bytesKeyArray = new byte[bytesKeyList.size()];

            for (int j = 0; j < bytesKeyArray.length; ++j) {
                bytesKeyArray[j] = bytesKeyList.get(j);
            }

            String key = new String(bytesKeyArray, StandardCharsets.UTF_8);

            file.read();
            file.readInt();

            int currentOffset = (int) file.getFilePointer() - 1;

            file.seek(offsets.get(i));

            byte[] valueArray = new byte[offsets.get(i + 1) - offsets.get(i)];

            file.read(valueArray);

            String value = new String(valueArray, StandardCharsets.UTF_8);

            map.put(key, curTable.getTb().deserialize(curTable, value));
            file.seek(currentOffset);
        }

        return map;
    }

    public static void saveTable(StoreableTable curTable) throws IOException {
        if (curTable == null) {
            return;
        }

        curTable.setTableSize(0);

        File[] directories = new File[curTable.getDirsNumber()];
        Map<Integer, File> files = new HashMap<>();
        Map<Integer, RandomAccessFile> RAFiles = new HashMap<>();
        Set<String> keySet = curTable.getMapContents().keySet();

        curTable.setUsedDirs();

        for (int i = 0; i < curTable.getDirsNumber(); i++) {
            directories[i] = new File(curTable.getCurTableDir(), i + ".dir");

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
                    File curFile = new File(
                            curTable.getCurTableDir().toPath().resolve(i + ".dir").toFile(), j + ".dat"
                    );

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

        Set<String> curFileKeySet = new HashSet<>();

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

                    commitFile(curTable, raf, curFileKeySet);
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

    public static void commitFile(StoreableTable curTable, RandomAccessFile file, Set<String> keySet)
            throws IOException {
        file.setLength(0);

        int curOffset = 0;
        int position = 0;

        for (String key : keySet) {
            curOffset += key.getBytes(StandardCharsets.UTF_8).length + 5;
        }

        for (String key : keySet) {
            if (curTable.get(key) != null) {
                file.seek(position);
                file.write(key.getBytes(StandardCharsets.UTF_8));
                file.write('\0');
                file.writeInt(curOffset);
                position = (int) file.getFilePointer();
                file.seek(curOffset);

                String value = curTable.getTb().serialize(curTable, curTable.get(key));

                file.write(value.getBytes(StandardCharsets.UTF_8));
                curOffset = (int) file.getFilePointer();
            }
        }
    }

    public static List<Class<?>> readTypes(String curDir) throws FileNotFoundException, ClassNotFoundException {
        Scanner scanner = new Scanner(new File(curDir, "signature.tsv"));
        List<Class<?>> types = new ArrayList<>();

        while (scanner.hasNext()) {
            try {
                types.add(ColumnTypes.nameToType(scanner.next()));
            } catch (IllegalArgumentException e) {
                scanner.close();

                throw new ClassNotFoundException("READ SIGNATURE ERROR: invalid signature");
            }
        }

        scanner.close();

        return types;
    }

    public static void writeTypes(String curDir, String[] signature) throws IOException {
        File signatureFile = new File(curDir, "signature.tsv");

        signatureFile.createNewFile();

        RandomAccessFile outputFile = new RandomAccessFile(signatureFile, "rw");

        for (String type : signature) {
            if (type.matches("(boolean|byte|double|float|int|long|String)")) {
                outputFile.writeBytes(type + " ");
            } else {
                outputFile.close();
                signatureFile.delete();

                throw new IOException("WRITE SIGNATURE ERROR: invalid type");
            }
        }

        outputFile.close();
    }
}
