package ru.fizteh.fivt.students.ermolenko.filemap;

import java.io.*;
import java.util.*;

public class FileMap {

    //для хранения <key, value>
    public Map<String, String> dataBase;
    private File dataFile;

    FileMap(File currentFile) throws IOException {
        dataBase = new HashMap<String, String>();
        readDataBase(currentFile);
        dataFile = currentFile;
    }

    private String readKey(DataInputStream stream) throws IOException {
        List<Byte> buf = new ArrayList<Byte>();
        byte b = 0;
        b = stream.readByte();

        while (b != 0) {
            buf.add(b);
            try {
                b = stream.readByte();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String key = null;

        key = convertBytesToString(buf, "UTF-8");

        return key;
    }

    private static String readValue(DataInputStream dis, long offset1, long offset2, long position) throws IOException {
        dis.mark(1024 * 1024);
        dis.skip(offset1 - position);
        byte[] buffer = new byte[(int) (offset2 - offset1)];
        dis.read(buffer);
        String value = new String(buffer, "UTF-8");
        dis.reset();
        return value;
    }

    public static String convertBytesToString(Collection<Byte> collection, String Encoding) throws UnsupportedEncodingException {
        byte[] buf = new byte[collection.size()];
        int i = 0;
        for (Byte b : collection) {
            buf[i] = (byte) b;
            ++i;
        }
        return new String(buf, Encoding);
    }

    private void readDataBase(File currentFile) throws IOException {
        if (currentFile.length() == 0) {
            return;
        }
        InputStream currentStream = null;
        try {
            currentStream = new FileInputStream(currentFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedInputStream bufferStream = new BufferedInputStream(currentStream, 4096);
        DataInputStream dataStream = new DataInputStream(bufferStream);
        long pos = 0;
        //прочитали первый ключ
        String key1 = readKey(dataStream);
        pos += key1.length();
        long biasing1 = 0;
        try {
            //считали смещение
            biasing1 = dataStream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long firstValue = biasing1;
        pos += 5;

        while (pos != firstValue) {
            String key2 = readKey(dataStream);
            pos += key2.length();
            long biasing2 = 0;
            try {
                biasing2 = dataStream.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pos += 5;
            String value = null;
            try {
                value = readValue(dataStream, biasing1, biasing2, pos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            dataBase.put(key1, value);
            biasing1 = biasing2;
            key1 = key2;
        }
        String value = null;
        try {
            value = readValue(dataStream, biasing1, currentFile.length(), pos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataBase.put(key1, value);

        closeStream(dataStream);
        closeStream(bufferStream);
        closeStream(currentStream);
    }

    private void closeStream(Closeable stream) throws IOException {
        stream.close();
    }

    private void write(Map<String, String> dataBase, File currentFile) throws IOException {

        OutputStream currentStream = new FileOutputStream(currentFile);
        BufferedOutputStream bufferStream = new BufferedOutputStream(currentStream, 4096);
        DataOutputStream dataStream = new DataOutputStream(bufferStream);
        long biasing = 0;

        for (String key : dataBase.keySet()) {
            biasing += key.getBytes("UTF-8").length + 5;
        }
        List<String> values = new ArrayList<String>(dataBase.keySet().size());
        for (String key : dataBase.keySet()) {
            String value = dataBase.get(key);
            values.add(value);
            dataStream.write(key.getBytes("UTF-8"));
            dataStream.writeByte(0);
            dataStream.writeInt((int) biasing);
            biasing += value.getBytes("UTF-8").length;
        }

        for (String value : values) {
            dataStream.write(value.getBytes());
        }
        closeStream(dataStream);
        closeStream(bufferStream);
        closeStream(currentStream);
    }

    void batchState(String[] args) throws IOException {

        StringBuilder tmp = new StringBuilder();

        //слили все слова в одну строку
        for (String arg : args) {
            tmp.append(arg).append(" ");
        }

        //создали массив команд
        String[] command = tmp.toString().split("\\;");

        String cmd = "";
        Executor exec = new Executor();

        //подаем команды на выполнение
        for (int i = 0; i < command.length - 1; ++i) {

            cmd = command[i].trim();
            if (cmd.equals("exit")) {
                write(dataBase, dataFile);
                break;
            }
            try {
                exec.execute(dataBase, cmd);
                //System.out.println(cmd);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    void interactiveState() throws IOException {
        Scanner scanner = new Scanner(System.in);
        Executor exec = new Executor();
        String input;
        String[] cmd;
        while (true) {
            cmd = scanner.nextLine().trim().split("\\s*;\\s*");
            try {
                for (String aCmd : cmd) {
                    if (!aCmd.equals("exit")) {
                        exec.execute(dataBase, aCmd);
                    } else {
                        write(dataBase, dataFile);
                        return;
                    }
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}