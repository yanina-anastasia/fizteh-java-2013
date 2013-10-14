package ru.fizteh.fivt.students.musin.filemap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Shell {

    private ArrayList<ShellCommand> commands;
    public File currentDirectory;
    boolean exit;

    public Shell(String startDirectory) {
        currentDirectory = new File(startDirectory);
        commands = new ArrayList<ShellCommand>();
        commands.add(new ShellCommand("exit", new ShellExecutable() {
            @Override
            public int execute(Shell shell, ArrayList<String> args) {
                exit = true;
                return 0;
            }
        }));
    }

    public void addCommand(ShellCommand command) {
        commands.add(command);
    }

    public int parseString(String s) {
        String[] comm = s.split(";");
        for (int i = 0; i < comm.length; i++) {
            String[] strings = comm[i].split("[ \\t\\r]");
            String name = "";
            ArrayList<String> args = new ArrayList<String>();
            boolean nameRead = false;
            for (int j = 0; j < strings.length; j++) {
                if (strings[j].equals("")) {
                    continue;
                }
                if (nameRead) {
                    args.add(strings[j]);
                } else {
                    name = strings[j];
                    nameRead = true;
                }
            }
            boolean commandFound = false;
            for (int j = 0; j < commands.size(); j++) {
                if (commands.get(j).name.equals(name)) {
                    if (commands.get(j).exec.execute(this, args) != 0) {
                        return -1;
                    }
                    commandFound = true;
                    break;
                }
            }
            if (!commandFound && !name.equals("")) {
                System.err.printf("No such command %s\n", name);
                return -1;
            }
        }
        return 0;
    }

    public int run(BufferedReader br) {
        exit = false;
        while (!exit) {
            System.out.print("$ ");
            try {
                String str = br.readLine();
                if (str == null) {
                    return 0;
                }
                parseString(str);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return 0;
    }
}

interface ShellExecutable {
    int execute(Shell shell, ArrayList<String> args);
}

class ShellCommand {
    String name;
    ShellExecutable exec;

    public ShellCommand(String name, ShellExecutable exec) {
        this.name = name;
        this.exec = exec;
    }
}
