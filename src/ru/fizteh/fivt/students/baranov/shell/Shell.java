package ru.fizteh.fivt.students.baranov.shell;

import java.util.HashMap;
import java.nio.file.Path;
import java.util.Scanner;

public class Shell {
    public HashMap<String, BasicCommand> commands;
    public ShellState path;

    Shell(Path path_) {
        this.path = new ShellState(path_);
        this.commands = new HashMap<String, BasicCommand>();
    }

    public void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String arguments = scanner.nextLine().trim();
            String[] args = arguments.split("\\s+");
            BasicCommand command = commands.get(args[0]);
            if (command == null) {
                System.err.println(args[0] + " - wrong command");
                continue;
            }
            if (!command.doCommand(args, path)) {
                return;
            }
            path.changeCurrentPath(path.getCurrentPath().normalize());
            path.copyMade = 0;
        }
    }

    public void pocketMode(String[] args) {
        String commandsString = "";
        for (int i = 0; i < args.length; ++i) {
            commandsString = commandsString + " "  + args[i];
        }
        String[] commandList = commandsString.trim().split("\\s*;\\s*");
        for (int i = 0; i < commandList.length; ++i) {
            String[] arguments = commandList[i].split(" ");
            BasicCommand cmd = commands.get(arguments[0]);
            if (cmd == null) {
                System.err.println(arguments[0] + " - wrong command");
                continue;
            }
            if (!cmd.doCommand(arguments, path)) {
                return;
            }
            path.changeCurrentPath(path.getCurrentPath().normalize());
            path.copyMade = 0;
        }
    }
}
