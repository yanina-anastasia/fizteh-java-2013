package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.utilities.Abstract;
import ru.fizteh.fivt.students.fedoseev.utilities.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.utilities.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractFileMap extends Abstract {
    private static final Map<String, String> content = new HashMap<String, String>();
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

                loadFromFile();
            } catch (IOException e) {
                System.err.println("ERROR: incorrect file format");
                System.exit(1);
            }
        } else {
            if (!state.getCurState().createNewFile()) {
                throw new IOException("ERROR: cannot create db.dat");
            } else {
                file = new RandomAccessFile(state.getCurState(), "rw");
            }
        }
    }

    private void loadFromFile() throws IOException {
        if (file.length() == 0) {
            return;
        }

        int curOffset;

        file.seek(0);
        String key = file.readUTF();
        file.readChar();
        int first = file.readInt();
        int position = (int) file.getFilePointer();
        file.seek(first);
        content.put(key, file.readUTF());

        while (position < first) {
            file.seek(position);
            key = file.readUTF();
            file.readChar();
            curOffset = file.readInt();
            position = (int) file.getFilePointer();
            file.seek(curOffset);
            content.put(key, file.readUTF());
        }
    }

    public static void commitFile() throws IOException {
        RandomAccessFile file = getFile();

        file.setLength(0);

        int curOffset = 0;
        int position = 0;

        Set<String> keySet = getMap().keySet();

        for (String key : keySet) {
            curOffset += key.getBytes("UTF-8").length + 8;
        }

        for (String key : keySet) {
            file.seek(position);
            file.writeUTF(key);
            file.writeChar('\0');
            file.writeInt(curOffset);
            position = (int) file.getFilePointer();
            file.seek(curOffset);
            file.writeUTF(getMap().get(key));
            curOffset = (int) file.getFilePointer();
        }
    }
}
