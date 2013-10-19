package ru.fizteh.fivt.students.mescherinilya.filemap;

import java.io.EOFException;
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
                try {
                    command.execute(cmdArgs);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    return false;
                }
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

    public static void readDatabase() throws Exception {
        database = new RandomAccessFile(databaseLocation, "r");

        if (database.length() == 0) {
            return;
        }

        ArrayList<Integer> offsets = new ArrayList<Integer>();
        ArrayList<String> keys = new ArrayList<String>();

        try {
            do {
                ArrayList<Byte> keySymbols = new ArrayList<Byte>();
                byte b = database.readByte();
                while (b != 0) {
                    keySymbols.add(b);
                    b = database.readByte();
                }
                byte[] bytes = new byte[keySymbols.size()];
                for (int i = 0; i < bytes.length; ++i) {
                    bytes[i] = keySymbols.get(i);
                }
                keys.add(new String(bytes, "UTF-8"));

                int offset = database.readInt();
                if (!offsets.isEmpty() &&
                        (offset <= offsets.get(offsets.size() - 1))) {
                    System.out.println(Integer.toHexString(offset) + " " + Integer.toHexString(offsets.get(offsets.size()-1)));
                    throw new IncorrectFileFormatException("Bad offset value");
                }
                offsets.add(offset);


            } while (database.getFilePointer() != offsets.get(0));
        } catch (EOFException e) {
            throw new IncorrectFileFormatException("Suddenly the end of the file was reached");
        }

        offsets.add((int) database.length());

        for (int i = 0; i < offsets.size(); ++i) {
            System.out.println(offsets.get(i).toString());
        }

        ArrayList<String> values = new ArrayList<String>();

        for (int i = 0; i < keys.size(); ++i) {
            byte[] bytes = new byte[offsets.get(i+1) - offsets.get(i)];
            database.read(bytes);
            values.add(new String(bytes, "UTF-8"));
        }

        for (int i = 0; i < keys.size(); ++i) {
            storage.put(keys.get(i), values.get(i));
        }

    }

    public static void main(String[] args) {

        commandList = new HashMap<String, Command>();
        commandList.put("put", new CommandPut());
        commandList.put("get", new CommandGet());
        commandList.put("remove", new CommandRemove());
        commandList.put("commit", new CommandCommit());

        storage = new TreeMap<String, String>();

        databaseLocation = new File(System.getProperty("fizteh.db.dir"), "db.dat").getAbsoluteFile();

        //databaseLocation = new File("C:\\Users\\Хозяин\\Documents\\GitHub\\fizteh-java-2013\\src\\ru\\fizteh\\fivt\\students" +
        //        "\\mescherinilya\\filemap\\dbmain.txt").getAbsoluteFile();

        try {

            readDatabase();

        } catch (IncorrectFileFormatException ie) {
            System.out.println("Incorrect database format: " + ie.getMessage());
            System.exit(1);
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
        } catch (TimeToExitException te) {
            System.exit(0);
        } catch (Throwable e) {
            System.err.println("Something bad has occured.");
            System.exit(1);
        }

        System.exit(0);
    }



}
