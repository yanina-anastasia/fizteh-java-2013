package ru.fizteh.fivt.students.dubovpavel.filemap;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DataBase implements DataBaseHandler<String, String> {
    protected File savingEndPoint;
    protected static final Charset charset = StandardCharsets.UTF_8;
    protected static final int MAXLENGTH = 1 << 20;
    protected HashMap<String, String> dict = new HashMap<String, String>();

    private void checkValid() {
        if(savingEndPoint == null) {
            throw new RuntimeException("DataBase pointer was null");
        }
    }
    public void open() throws DataBaseException {
        checkValid();
        try(DataInputStream db = new DataInputStream(new FileInputStream(savingEndPoint))) {
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
            throw new DataBaseException(String.format("Conformity loading: IOException: %s. Empty database applied", e.getMessage()));
        } catch (DataBaseException e) {
            dict = new HashMap<String, String>();
            throw new DataBaseException(String.format("Conformity loading: DataBaseException: %s. Empty database applied", e.getMessage()));
        }
    }

    public DataBase(File path) {
        savingEndPoint = path;
    }

    protected DataBase() {
        savingEndPoint = null;
    }

    public void save() throws DataBaseException {
        checkValid();
        try(DataOutputStream db = new DataOutputStream(new FileOutputStream(savingEndPoint))) {
            for(Map.Entry<String, String> entry: dict.entrySet()) {
                byte[] key = entry.getKey().getBytes(charset);
                byte[] value = entry.getValue().getBytes(charset);
                db.writeInt(key.length);
                db.writeInt(value.length);
                db.write(key);
                db.write(value);
            }
        } catch(IOException e) {
            throw new DataBaseException(String.format("Conformity saving: IOException: %s", e.getMessage()));
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
