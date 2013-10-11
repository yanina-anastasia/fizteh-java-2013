package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Shell {

    private CommandsController controller = new CommandsController();
    private CurrentDirectory currentDirectory = new CurrentDirectory();

    public Shell() {
        currentDirectory.changeCurDir(new File("").getAbsoluteFile());
    }

    public void addCommand(Command cmd) {
        controller.addCmd(cmd);
    }

    public void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.print(currentDirectory.getCurDir().getAbsolutePath() + "$ ");
                String inputString = scanner.nextLine();
                String[] commands = inputString.split(";");
                for (String command : commands) {
                    String[] splittedCommand = command.trim().split("\\s+");
                    controller.runCommand(currentDirectory, splittedCommand);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void batchMode(String inputString) {
        try {
            String[] commands = inputString.split(";");
            for (String command : commands) {
                String[] splittedCommand = command.trim().split("\\s+");
                controller.runCommand(currentDirectory, splittedCommand);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }
}
