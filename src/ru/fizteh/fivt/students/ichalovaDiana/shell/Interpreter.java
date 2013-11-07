package ru.fizteh.fivt.students.ichalovaDiana.shell;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Interpreter {

    private Map<String, Command> commands = new HashMap<String, Command>();

    public Interpreter(Map<String, Command> commands) {
        this.commands = commands;
    }

    public void run(String[] args) {
        if (args.length == 0) {
            runInteractiveMode();
        } else {
            runBatchMode(args);
        }
    }

    public void runInteractiveMode() {
        Scanner userInput = new Scanner(System.in);
        System.out.print("$ ");
        while (userInput.hasNextLine()) {
            String input = userInput.nextLine();
            try {
                executeCommands(input);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            System.out.print("$ ");
        }
        userInput.close();
    }

    public void runBatchMode(String[] args) {
        StringBuilder concatArgs = new StringBuilder();
        for (String item : args) {
            concatArgs.append(item).append(" ");
        }
        try {
            executeCommands(concatArgs.toString());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void executeCommands(String input) throws Exception {
        String[][] inputCommandsWithParams = parseCommands(input);
        for (int i = 0; i < inputCommandsWithParams.length; ++i) {
            if (inputCommandsWithParams[i].length == 1
                    && inputCommandsWithParams[i][0].trim().equals("")) {
                continue;
            }
            Command cmd = commands.get(inputCommandsWithParams[i][0]);
            if (cmd == null) {
                throw new Exception("Command not found");
            }
            cmd.execute(inputCommandsWithParams[i]); 
        }
    }

    private String[][] parseCommands(String inputString) {
        String[] inputCommands = inputString.split("[\n\t ]*;[\n\t ]*");
        String[][] inputCommandsWithParams = new String[inputCommands.length][];
        for (int i = 0; i < inputCommands.length; ++i) {
            inputCommandsWithParams[i] = inputCommands[i].trim().split("[\n\t ]+");
        }
        return inputCommandsWithParams;
    }
}
