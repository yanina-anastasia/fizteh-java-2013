package ru.fizteh.fivt.students.drozdowsky.filemap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.ByteBuffer;

import com.sun.tools.javac.util.Pair;
import ru.fizteh.fivt.students.drozdowsky.filemap.commands.Get;
import ru.fizteh.fivt.students.drozdowsky.filemap.commands.Put;
import ru.fizteh.fivt.students.drozdowsky.filemap.commands.Remove;

public class Utils {
    public static boolean executeCommand(String[] args, File db) {
        if (args.length == 0) {
            return true;
        }
        String command = args[0];
        if (command.equals("put")) {
            Put put = new Put(db, args);
            return put.execute();
        } else if (command.equals("get")) {
            Get get = new Get(db, args);
            return get.execute();
        } else if (command.equals("remove")) {
            Remove rm = new Remove(db, args);
            return rm.execute();
        } else if (command.equals("exit")) {
            System.exit(0);
        } else {
            System.err.println(args[0] + ": command not found");
            return false;
        }
        return true;
    }

    static final int BUFFSIZE = 100000;

    public static HashMap<String, String> readDB(File db) {
        try {
            HashMap<String, String> map = new HashMap<String, String>();
            ArrayList<Pair<String, Integer>> offset = new ArrayList<Pair<String, Integer>>();
            FileInputStream inputDB = new FileInputStream(db);

            Byte next = 0;
            int byteRead = 0;
            ByteBuffer key = ByteBuffer.allocate(BUFFSIZE);

            while ((next = (byte) inputDB.read()) != -1) {
                if (next == 0) {
                    byte[] sizeBuf = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        if ((sizeBuf[i] = (byte) inputDB.read()) == -1) {
                            System.err.println("Incorrect database");
                            System.exit(1);
                        }
                    }
                    int valueSize = ByteBuffer.wrap(sizeBuf).getInt();

                    byte[] keyArray = new byte[key.position()];
                    byteRead += key.position() + 4 + 1;
                    key.clear();
                    key.get(keyArray);
                    offset.add(new Pair<String, Integer>(new String(keyArray, "UTF-8"), valueSize));
                    key.clear();

                } else {
                    key.put(next);
                }
            }

            if ((offset.size() == 0 && key.position() != 0) || (offset.get(0).snd != byteRead)) {
                System.err.println("Incorrect database");
                System.exit(1);
            }

            if (offset.get(0).snd - byteRead != 0) {
                System.err.println("Incorrect database");
                System.exit(1);
            }
            for (int i = 0; i < offset.size() - 1; i++) {
                offset.set(i, new Pair<String, Integer>(offset.get(i).fst, offset.get(i + 1).snd - byteRead));
            }
            int n = offset.size();
            offset.set(n - 1, new Pair<String, Integer>(offset.get(n - 1).fst, key.position()));

            int prevOffset = 0;
            for (Pair<String, Integer> now: offset) {
                Integer currentOffset = now.snd;
                if (currentOffset <= prevOffset) {
                    System.err.println("Incorrect database");
                    System.exit(1);
                } else {
                    ByteBuffer valueBuf = ByteBuffer.allocate(currentOffset - prevOffset);
                    for (int i = prevOffset; i < currentOffset; i++) {
                        valueBuf.put(key.get(i));
                    }
                    prevOffset = currentOffset;
                    String value = new String(valueBuf.array(), "UTF-8");
                    map.put(now.fst, value);
                }
            }

            return map;
        } catch (FileNotFoundException e) {
            System.err.println(db.getAbsolutePath() + ": No such file or directory");
            System.exit(1);
        } catch (IOException e) {
            System.err.println(db.getAbsolutePath() + e.toString());
            System.exit(1);
        }
        return null;
    }

    public static void writeDB(File db, HashMap<String, String> map) {
        try {
            FileOutputStream out = new FileOutputStream(db);
            ArrayList<Integer> length = new ArrayList<Integer>();
            ArrayList<String> values = new ArrayList<String>();
            ArrayList<String> keys = new ArrayList<String>();
            int totalLength = 0;
            for (String key:map.keySet()) {
                keys.add(key);
                values.add(map.get(key));
                totalLength += key.getBytes("UTF-8").length;
                length.add(map.get(key).getBytes("UTF-8").length);
                totalLength += 4;
                totalLength += "\0".getBytes("UTF-8").length;
            }
            for (int i = 0; i < keys.size(); i++) {
                out.write(keys.get(i).getBytes("UTF-8"));
                out.write("\0".getBytes("UTF-8"));
                out.write(ByteBuffer.allocate(4).putInt(totalLength).array());
                totalLength += length.get(i);
            }
            for (int i = 0; i < keys.size(); i++) {
                out.write(values.get(i).getBytes("UTF-8"));
            }

        } catch (FileNotFoundException e) {
            System.err.println(db.getAbsolutePath() + ": No such file or directory");
            System.exit(1);
        } catch (IOException e) {
            System.err.println(db.getAbsolutePath() + e.toString());
            System.exit(1);
        }
    }

}
