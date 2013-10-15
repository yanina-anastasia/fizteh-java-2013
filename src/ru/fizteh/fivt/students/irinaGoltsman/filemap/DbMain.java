package ru.fizteh.fivt.students.irinaGoltsman.filemap;

import java.io.IOException;
import java.util.*;

public class DbMain {
    public static void main(String[] args) throws IOException {
        //String path = "C:\\Users\\Ira\\IdeaProjects\\fizteh-java-2013\\src\\ru\\fizteh\\db\\dir";
        DataBase myDataBase = new DataBase();
        try {
            //myDataBase.load(path);
            myDataBase.load();
        } catch (Exception e) {
            System.err.println("Error while loading db");
            System.err.println(e);
            System.exit(1);
        }
        MapOfCommands cm = new MapOfCommands();
        cm.addCommand(new ShellCommands.Exit());
        cm.addCommand(new DBCommands.Put());
        cm.addCommand(new DBCommands.Get());
        cm.addCommand(new DBCommands.Remove());

        if (args.length == 0) {
            Scanner scan = new Scanner(System.in);
            System.out.print("$ ");
            while (true) {
                String nameOfCommand = "exit";
                if (scan.hasNextLine()) {
                    nameOfCommand = scan.nextLine();
                } else {
                    break;
                }
                String checkIsEmpty = nameOfCommand.replaceAll(" ", "");
                checkIsEmpty = checkIsEmpty.replaceAll("\t", "");
                if (checkIsEmpty.equals("")) {
                    System.out.print("$ ");
                    continue;
                }
                String[] commands = nameOfCommand.split(";");
                if (commands.length == 1) {
                    String checkEmpty = commands[0].replaceAll(" ", "");
                    checkEmpty = checkEmpty.replaceAll("\t", "");
                    if (checkEmpty.equals("")) {
                        System.out.print("$ ");
                        continue;
                    }
                    Code codeOfCommand = MapOfCommands.commandProcessing(commands[0]);
                    if (codeOfCommand == Code.SYSTEM_ERROR) {
                        try {
                            myDataBase.emergencyExit();
                        } catch (Exception e) {
                            System.err.println(e);
                            System.exit(1);
                        }
                        System.exit(1);
                    } else if (codeOfCommand == Code.EXIT) {
                        try {
                            myDataBase.close();
                        } catch (Exception e) {
                            System.err.println("Error while closing db");
                            System.err.println(e);
                            System.exit(1);
                        }
                        System.exit(0);
                    }
                } else {
                    for (int i = 0; i < commands.length; i++) {
                        String checkEmpty = commands[i].replaceAll(" ", "");
                        checkEmpty = checkEmpty.replaceAll("\t", "");
                        if (checkEmpty.equals("")) {
                            continue;
                        }
                        Code codeOfCommand = MapOfCommands.commandProcessing(commands[i]);
                        if (codeOfCommand == Code.SYSTEM_ERROR) {
                            try {
                                myDataBase.emergencyExit();
                            } catch (Exception e) {
                                System.err.println(e);
                                System.exit(1);
                            }
                            System.exit(1);
                        }
                    }
                }
                System.out.print("$ ");
            }
        } else {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i]);
                str.append(" ");
            }
            String input = str.toString();
            String[] commands = input.split(";");
            for (int i = 0; i < commands.length; i++) {
                String checkIsEmpty = commands[i].replaceAll(" ", "");
                checkIsEmpty = checkIsEmpty.replaceAll("\t", "");
                if (checkIsEmpty.equals("")) {
                    continue;
                }
                Code codeOfCommand = MapOfCommands.commandProcessing(commands[i]);
                if (codeOfCommand == Code.SYSTEM_ERROR || codeOfCommand == Code.ERROR) {
                    try {
                        myDataBase.emergencyExit();
                    } catch (Exception e) {
                        System.err.println(e);
                        System.exit(1);
                    }
                    System.exit(1);
                }
                if (codeOfCommand == Code.EXIT) {
                    try {
                        myDataBase.close();
                    } catch (Exception e) {
                        System.err.println("Error while closing db");
                        System.err.println(e);
                        System.exit(1);
                    }
                    System.exit(0);
                }
            }
        }

        try {
            myDataBase.close();
        } catch (Exception e) {
            System.err.println("Error while closing db");
            System.err.println(e);
            System.exit(1);
        }
    }
}
