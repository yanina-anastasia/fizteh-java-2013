package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.Abstract;
import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.Utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class AbstractFileMap extends Abstract {
    private static final Map<String, String> content = new HashMap<String, String>();
    private static final int MAX_FILE_SIZE = 150100500;
    private static RandomAccessFile file;

    public static RandomAccessFile getFile() {
        return file;
    }

    public static Map<String, String> getMap() {
        return content;
    }

    public AbstractFileMap(File file) throws IOException {
        super(file);
        checkOpenFile();
    }

    @Override
    public Map<String, AbstractCommand> getCommands() {
        final PutCommand PUT = new PutCommand();
        final GetCommand GET = new GetCommand();
        final RemoveCommand REMOVE = new RemoveCommand();
        final ExitCommand EXIT = new ExitCommand();

        return new HashMap<String, AbstractCommand>() {{
            put(PUT.getCmdName(), PUT);
            put(GET.getCmdName(), GET);
            put(REMOVE.getCmdName(), REMOVE);
            put(EXIT.getCmdName(), EXIT);
        }};
    }

    @Override
    public void runCommands(String cmd, int end) throws IOException {
        Map<String, AbstractCommand> commands = getCommands();

        if (!commands.containsKey(cmd.substring(0, end))) {
            throw new IOException("\"ERROR: not existing command \"" + cmd.substring(0, end) + "\"");
        }

        AbstractCommand command = commands.get(cmd.substring(0, end));

        if (Utils.getCommandArguments(cmd).length != command.getArgsCount()) {
            throw new IOException(command.getCmdName() + " ERROR: \"" + command.getCmdName() +
                    "\" command receives " + command.getArgsCount() + " arguments");
        }

        command.execute(Utils.getCommandArguments(cmd), state);
    }

    private void checkOpenFile() throws IOException {
        if (state.getCurState().exists()) {
            try {
                file = new RandomAccessFile(state.getCurState(), "rw");

                if (file.length() > MAX_FILE_SIZE) {
                    file.close();
                    throw new IOException("ERROR: too big file");
                }

                readFile();
            } catch (IOException e) {
                file.close();
                System.err.println("ERROR: incorrect file format");
                return;
            }
        }

        file.close();
    }

    private void readFile() throws IOException {
        if (file.length() == 0) {
            return;
        }

        List<Integer> offsets = new ArrayList<Integer>();

        while (file.getFilePointer() != file.length()) {
            if (file.readByte() == '\0') {
                int offset = file.readInt();

                if (offset < 0 || offset > file.length()) {
                    throw new IOException("ERROR: incorrect input");
                }

                offsets.add(offset);
            }
        }
        offsets.add((int) file.length());

        file.seek(0);

        for (int i = 0; i < offsets.size() - 1; ++i) {
            List<Byte> bytesKeyList = new ArrayList<Byte>();

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

            content.put(key, value);
            file.seek(currentOffset);
        }
    }

    public static void commitFile() throws IOException {
        RandomAccessFile file = getFile();

        file.setLength(0);

        int curOffset = 0;
        int position = 0;

        Set<String> keySet = getMap().keySet();

        for (String key : keySet) {
            curOffset += key.getBytes(StandardCharsets.UTF_8).length + 5;
        }

        for (String key : keySet) {
            file.seek(position);
            file.write(key.getBytes(StandardCharsets.UTF_8));
            file.write('\0');
            file.writeInt(curOffset);
            position = (int) file.getFilePointer();
            file.seek(curOffset);
            file.write(getMap().get(key).getBytes(StandardCharsets.UTF_8));
            curOffset = (int) file.getFilePointer();
        }
    }
}
