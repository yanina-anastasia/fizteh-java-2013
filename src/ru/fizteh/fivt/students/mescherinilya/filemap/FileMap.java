package ru.fizteh.fivt.students.mescherinilya.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.*;

public class FileMap {

    private static Map<String, Command> commandList;

    static File databaseLocation;
    static Map<String, String> storage;
    static RandomAccessFile database;

    public static boolean batchMode(String input) throws TimeToExitException {

        String[] commands = input.split("\\s*;\\s*");

        for (String cmd : commands) {
            cmd = cmd.trim(); //сомневаюсь, нужно ли это
            if (cmd.isEmpty()) {
                continue;
            }

            String cmdName;
            String[] cmdArgs;
            if (cmd.indexOf(" ") != -1) {
                cmdName = cmd.substring(0, cmd.indexOf(" "));
                cmdArgs = cmd.substring(cmd.indexOf(" ") + 1, cmd.length())
                        .trim().split("\\s+");
                for (String cmdArg : cmdArgs) {
                    cmdArg = cmdArg.trim();
                }
            } else {
                cmdName = cmd;
                cmdArgs = new String[0];
            }

            if (cmdName.equals("exit")) {
                throw new TimeToExitException();
            }

            Command command = commandList.get(cmdName);
            if (command != null) {
                if (command.getArgsCount() != cmdArgs.length) {
                    System.err.println(command.getName() + ": Wrong count of arguments!");
                    return false;
                }
                command.execute(cmdArgs);
            } else {
                System.err.println("Unknown command: " + cmdName);
                return false;
            }
        }

        return true;
    }

    public static void interactiveMode() throws TimeToExitException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine().trim();
            batchMode(input);
        }


    }

    public static void main(String[] args) {

        commandList = new HashMap<String, Command>();
        commandList.put("put", new CommandPut());
        commandList.put("get", new CommandGet());
        commandList.put("remove", new CommandRemove());

        storage = new TreeMap<String, String>();
        databaseLocation = new File(System.getProperty("fizteh.db.dir")).getAbsoluteFile();
        //databaseLocation = new File("C:\\Users\\Хозяин\\Documents\\GitHub\\fizteh-java-2013\\src\\ru\\fizteh\\fivt\\students" +
        //        "\\mescherinilya\\filemap\\dbmain.txt").getAbsoluteFile();

        try {
            database = new RandomAccessFile(databaseLocation, "rw");

            int keyLength;
            int valueLength;
            String key;
            String value;
            while (database.getFilePointer() != database.length()) {
                keyLength = database.readInt();
                valueLength = database.readInt();
                byte[] keySymbols = new byte[keyLength];
                byte[] valueSymbols = new byte[valueLength];
                database.read(keySymbols);
                database.read(valueSymbols);
                key = new String(keySymbols, "UTF-8");
                value = new String(valueSymbols, "UTF-8");
                storage.put(key, value);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        try {
            if (args.length == 0) {
                interactiveMode();
            } else {
                StringBuilder sb = new StringBuilder();
                for (String arg : args) {
                    sb.append(arg).append(" ");
                }

                batchMode(sb.toString());
            }
        } catch (Exception e) {
            try {
                database.setLength(0);
                Set<String> keySet = storage.keySet();
                for (String key : keySet) {
                    database.writeInt(key.length());
                    database.writeInt(storage.get(key).length());
                    database.write(key.getBytes("UTF-8"));
                    database.write(storage.get(key).getBytes("UTF-8"));
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }

            if (e.getClass() == Pizdation.class) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

        System.exit(0);
    }



}
