package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Shell;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.StringMethods;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        FileWriter fileWriter = new FileWriter();
        try {
            File dataFile = new File(System.getProperty("fizteh.db.dir"), "db.dat");

            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }
            FileMapState state = new FileMapState(dataFile);
            Shell fileMap = new Shell();
            fileMap.addCommand(new GetCommand(state));
            fileMap.addCommand(new PutCommand(state));
            fileMap.addCommand(new RemoveCommand(state));
            fileMap.addCommand(new ExitCommand(state));
            FileReader fileReader = new FileReader(dataFile, state.dataStorage);
            while (fileReader.checkingLoadingConditions()) {
                fileReader.getNextKey();
            }
            fileReader.putKeysToTable();
            fileReader.closeResources();
            if (args.length == 0) {
                fileMap.interactiveMode();
            } else {
                String arguments = StringMethods.join(Arrays.asList(args), " ");
                try {
                    fileMap.batchMode(arguments);
                    fileWriter.writeDataToFile(state.getDataFile(), state.dataStorage);
                } catch (IOException e) {
                    fileWriter.writeDataToFile(state.getDataFile(), state.dataStorage);
                    System.err.println(e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("not managed to create file");
            System.exit(1);
        }
    }
}
