package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class FileMapController {

    private DataFileController dataFileController = new DataFileController();
    private DataTable dataTable = new DataTable();
    private CommandsController controller = new CommandsController();
    File dataFile;

    public FileMapController(File file) {
        dataFile = file;
    }


    public void addCommand(Command cmd) {
        controller.addCmd(cmd);
    }

    public void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        boolean flagFirst = true;
        while (true) {
            try {
                System.out.print("$ ");
                String inputString = scanner.nextLine();
                String[] commands = inputString.split(";");
                for (String command : commands) {
                    String[] splittedCommand = command.trim().split("\\s+");
                    controller.runCommand(splittedCommand, flagFirst, dataTable, dataFile);
                    flagFirst = false;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void batchMode(String inputString) {
        try {
            boolean flagFirst = true;
            String[] commands = inputString.split(";");
            for (String command : commands) {
                String[] splittedCommand = command.trim().split("\\s+");
                controller.runCommand(splittedCommand, flagFirst, dataTable, dataFile);
                flagFirst = false;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            dataFileController.writeDataToFile(dataFile, dataTable);
        }

    }
}
