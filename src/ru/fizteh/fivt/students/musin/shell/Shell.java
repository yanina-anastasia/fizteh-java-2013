package ru.fizteh.fivt.students.musin.shell;

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

    private int parseString(String s) {
        ArrayList<String> comm = new ArrayList<String>();
        int start = 0;
        boolean quote = false;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ';' && !quote) {
                comm.add(s.substring(start, i));
                start = i + 1;
            }
            if (s.charAt(i) == '"') {
                quote ^= true;
            }
        }
        if (quote) {
            System.err.println("Wrong quotation sequence");
            return -1;
        }
        if (start != s.length()) {
            comm.add(s.substring(start, s.length()));
        }
        for (int i = 0; i < comm.size(); i++) {
            String name = "";
            ArrayList<String> args = new ArrayList<String>();
            ArrayList<String> selfParseArgs = new ArrayList<String>();
            boolean nameRead = false;
            start = 0;
            quote = false;
            for (int j = 0; j < comm.get(i).length(); j++) {
                if (!quote && Character.isSpaceChar(comm.get(i).charAt(j))) {
                    if (start != j) {
                        if (!nameRead) {
                            name = comm.get(i).substring(start, j);
                            nameRead = true;
                            selfParseArgs.add(comm.get(i).substring(j + 1, comm.get(i).length()));
                        } else {
                            args.add(comm.get(i).substring(start, j));
                        }
                    }
                    start = j + 1;
                }
                if (comm.get(i).charAt(j) == '"') {
                    if (!nameRead) {
                        System.err.println("Arguments are specified, but no command was given");
                        return -1;
                    }
                    if (quote) {
                        args.add(comm.get(i).substring(start, j));
                        if (j + 1 != comm.get(i).length() && !Character.isSpaceChar(comm.get(i).charAt(j + 1))) {
                            System.err.println("Wrong argument format (Maybe space-character is forgotten?)");
                            return -1;
                        }
                    } else if (!Character.isSpaceChar(comm.get(i).charAt(j - 1))) {
                        System.err.println("Wrong argument format (Maybe space-character is forgotten?)");
                        return -1;
                    }
                    quote ^= true;
                    start = j + 1;
                }
            }
            if (start != comm.get(i).length()) {
                if (!nameRead) {
                    name = comm.get(i).substring(start, comm.get(i).length());
                    selfParseArgs.add("");
                } else {
                    args.add(comm.get(i).substring(start, comm.get(i).length()));
                }
            }
            boolean commandFound = false;
            for (int j = 0; j < commands.size(); j++) {
                if (commands.get(j).name.equals(name)) {
                    if (commands.get(j).parsingRequired) {
                        if (commands.get(j).exec.execute(this, args) != 0) {
                            return -1;
                        }
                    } else {
                        if (commands.get(j).exec.execute(this, selfParseArgs) != 0) {
                            return -1;
                        }
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

    public void addCommand(ShellCommand command) {
        commands.add(command);
    }

    public int runArgs(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s).append(" ");
        }
        String argString = sb.toString();
        return parseString(argString);
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

    public interface ShellExecutable {
        int execute(Shell shell, ArrayList<String> args);
    }

    public static class ShellCommand {
        String name;
        ShellExecutable exec;
        boolean parsingRequired;

        public ShellCommand(String name, ShellExecutable exec) {
            this.name = name;
            this.exec = exec;
            this.parsingRequired = true;
        }

        public ShellCommand(String name, boolean parsingRequired, ShellExecutable exec) {
            this.name = name;
            this.exec = exec;
            this.parsingRequired = parsingRequired;
        }
    }
}
