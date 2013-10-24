package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.filemap.DataBase;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DataBaseMultiFileHashMap extends DataBase {
    private final int dirsCount = 16;
    private final int chunksCount = 16;
    private File root;

    public DataBaseMultiFileHashMap(File path) {
        root = path;
    }

    public File getPath() {
        return root;
    }

    private void setPath(File path) {
        savingEndPoint = path;
    }

    private File generateChunksDir(int dir) {
        return new File(root, String.format("%d.dir", dir));
    }

    private File generateChunk(int dir, int chunk) {
        return new File(generateChunksDir(dir), String.format("%d.dat", chunk));
    }

    private Distribution<Integer, Integer> getDistribution(String key) {
        int hashcode = key.hashCode();
        int ndirectory = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        return new Distribution<Integer, Integer>(ndirectory, nfile);
    }

    @Override
    public void open() throws DataBaseException {
        boolean allReadSuccessfully = true;
        HashMap<String, String> chunksCollector = new HashMap<>();
        String exceptionMessage = "";
        for(int i = 0; i < dirsCount; i++) {
            File sub = generateChunksDir(i);
            if(sub.isDirectory()) {
                for(int j = 0; j < chunksCount; j++) {
                    File data = generateChunk(i, j);
                    if(data.isFile()) {
                        try {
                            dict = new HashMap<>();
                            setPath(data);
                            super.open();
                            for(String key: dict.keySet()) {
                                Distribution<Integer, Integer> distr = getDistribution(key);
                                if(i != distr.getDir() || j != distr.getChunk()) {
                                    throw new DataBaseException(
                                            String.format("Key '%s' should not belong to this chunk. The whole chunk denied", key));
                                }
                            }
                            chunksCollector.putAll(dict);
                        } catch(DataBaseException e) {
                            allReadSuccessfully = false;
                            exceptionMessage = String.format("\nChunk (%d, %d): %s", i, j, e.getMessage());
                        }
                    }
                }
            }
        }
        dict = chunksCollector;
        if(!allReadSuccessfully) {
            throw new DataBaseException(exceptionMessage);
        }
    }
    @Override
    public void save() throws DataBaseException {
        HashMap<String, String> backUp = dict;
        HashMap<String, String>[][] distribution = new HashMap[dirsCount][chunksCount];
        for(int i = 0; i < dirsCount; i++) {
            File sub = generateChunksDir(i);
            for(int j = 0; j < chunksCount; j++) {
                File data = generateChunk(i, j);
                distribution[i][j] = new HashMap<>();
                if(data.isFile()) {
                    if(!data.delete()) {
                        dict = backUp;
                        throw new DataBaseException(String.format("Can not delete chunk '%s'", data.getPath()));
                    }
                }
            }
            if(sub.isDirectory() && sub.listFiles().length == 0 && !sub.delete()) {
                dict = backUp;
                throw new DataBaseException(String.format("Can not delete directory '%s'", sub.getPath()));
            }
        }
        for(Map.Entry<String, String> entry: dict.entrySet()) {
            Distribution<Integer, Integer> distr = getDistribution(entry.getKey());
            distribution[distr.getDir()][distr.getChunk()].put(entry.getKey(), entry.getValue());
        }
        for(int i = 0; i < dirsCount; i++) {
            File sub = generateChunksDir(i);
            for(int j = 0; j < chunksCount; j++) {
                if(!distribution[i][j].isEmpty()) {
                    if(!sub.mkdirs()) {
                        dict = backUp;
                        throw new DataBaseException("Chunk (%d, *): Can not create directory");
                    }
                    dict = distribution[i][j];
                    setPath(generateChunk(i, j));
                    try {
                        super.save();
                    } catch(DataBaseException e) {
                        dict = backUp;
                        throw new DataBaseException(String.format("Chunk (%d, %d): %s", i, j, e.getMessage()));
                    }
                }
            }
        }
        dict = backUp;
    }
}
