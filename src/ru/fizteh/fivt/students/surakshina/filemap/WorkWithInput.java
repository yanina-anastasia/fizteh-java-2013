package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import ru.fizteh.fivt.students.surakshina.shell.Shell;

public class WorkWithInput extends Shell {

    @Override
    protected String[] extractArgumentsFromInputString(String input) {
        input = input.trim();
        if (input.isEmpty()) {
            return null;
        }
        return input.split("[\\s]+", 3);
    }

    @Override
    protected void doInteractiveMode() {
        System.out.print("$ ");
        String cur;
        Commands cmd = new Commands();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            cur = scanner.nextLine();
            cur = cur.trim();
            Scanner scanner1 = new Scanner(cur);
            scanner1.useDelimiter("[ ]*;[ ]*");
            while (scanner1.hasNext()) {
                String current = scanner1.next();
                if (current.equals("exit")) {
                    scanner.close();
                    scanner1.close();
                    System.out.println("exit");
                    return;
                } else {
                    if (!current.isEmpty()) {
                        cmd.executeProcess(extractArgumentsFromInputString(current));
                    }
                }
            }
            System.out.print("$ ");
            scanner1.close();
        }
        scanner.close();
    }

    @Override
    protected void doPackageMode(String[] input) {
        String newInput = makeNewInputString(input);
        Scanner scanner = new Scanner(newInput);
        scanner.useDelimiter("[ ]*;[ ]*");
        Commands cmd = new Commands();
        while (scanner.hasNext()) {
            String current = scanner.next();
            current = current.trim();
            if (!current.equals("exit")) {
                if (!current.isEmpty()) {
                    cmd.executeProcess(extractArgumentsFromInputString(current));
                } else {
                    printError("Incorrect input");
                    try {
                        FileMap.writeInDatabase();
                    } catch (FileNotFoundException e) {
                        System.err.println("Can't read database");
                        FileMap.closeFile(FileMap.dataBase);
                        System.exit(1);
                    } catch (IOException e1) {
                        System.err.println("Can't write in database");
                        FileMap.closeFile(FileMap.dataBase);
                        System.exit(1);
                    }
                    FileMap.closeFile(FileMap.dataBase);
                    System.exit(1);
                }
            }
        }
        scanner.close();
        System.out.println("exit");
    }

}
