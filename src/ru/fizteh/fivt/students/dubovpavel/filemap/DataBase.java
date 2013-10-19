package ru.fizteh.fivt.students.dubovpavel.filemap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DataBase implements DataBaseHandler<String, String> {
    private DispatcherFileMap dispatcher;
    private String directory;
    private final String charset = "UTF-8";
    private final int MAXLENGTH = 1 << 20;
    HashMap<String, String> dict = new HashMap<String, String>();

    public DataBase(String directory, DispatcherFileMap dispatcher) {
        this.dispatcher = dispatcher;
        this.directory = directory;
        DataInputStream db = null;
        try {
            db = new DataInputStream(new FileInputStream(new File(this.directory, "db.dat")));
            while(true) {
                int keyLength;
                try {
                    keyLength = db.readInt();
                } catch (EOFException e) {
                    break;
                }
                if(keyLength <= 0 || keyLength > MAXLENGTH) {
                    throw new DataBaseException(String.format("Key length must be in [1; %d]", MAXLENGTH));
                }
                int valueLength = db.readInt();
                if(valueLength <= 0 || valueLength > MAXLENGTH) {
                    throw new DataBaseException(String.format("Value length must be in [1; %d]", MAXLENGTH));
                }
                byte[] keyBuffer = new byte[keyLength];
                db.readFully(keyBuffer, 0, keyLength);
                String key = new String(keyBuffer, charset);
                byte[] valueBuffer = new byte[valueLength];
                db.readFully(valueBuffer, 0, valueLength);
                String value = new String(valueBuffer, charset);
                dict.put(key, value);
            }
        } catch (IOException e) {
            dict = new HashMap<String, String>();
            this.dispatcher.callbackWriter(Dispatcher.MessageType.WARNING,
                    String.format("Database loading: IOException: %s. Empty database applied", e.getMessage()));
        } catch (DataBaseException e) {
            dict = new HashMap<String, String>();
            this.dispatcher.callbackWriter(Dispatcher.MessageType.WARNING,
                    String.format("Database loading: DataBaseException: %s. Empty database applied", e.getMessage()));
        } finally {
            try {
                if(db != null) {
                    db.close();
                }
            } catch(IOException e) {
                this.dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                        "Can not close stream, nothing we can do about it");
            }
        }
    }

    public void save() throws DataBaseException {
        DataOutputStream db = null;
        try {
            db = new DataOutputStream(new FileOutputStream(new File(directory, "db.dat")));
            for(Map.Entry<String, String> entry: dict.entrySet()) {
                byte[] key = entry.getKey().getBytes(charset);
                byte[] value = entry.getValue().getBytes(charset);
                db.writeInt(key.length);
                db.writeInt(value.length);
                db.write(key);
                db.write(value);
            }
        } catch(IOException e) {
            throw new DataBaseException(String.format("Database saving: IOException: %s", e.getMessage()));
        } finally {
            try {
                if(db != null) {
                    db.close();
                }
            } catch(IOException e) {
                this.dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                        "Can not close stream, nothing we can do about it");
            }
        }
    }

    public String put(String key, String value) {
        if(dict.containsKey(key)) {
            String old = dict.get(key);
            dict.put(key, value);
            return old;
        } else {
            dict.put(key, value);
            return null;
        }
    }

    public String remove(String key) {
        if(dict.containsKey(key)) {
            String removing = dict.get(key);
            dict.remove(key);
            return removing;
        } else {
            return null;
        }
    }

    public String get(String key) {
        if(dict.containsKey(key)) {
            return dict.get(key);
        } else {
            return null;
        }
    }
}
