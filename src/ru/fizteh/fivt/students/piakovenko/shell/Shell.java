package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Shell {
    public static void main(String[] args) {
        CurrentStatus currentStatus = null;
        currentStatus = new CurrentStatus(new File("."));
        CommandsMap cm = new CommandsMap();
        cm.addCommand(new PrintWorkingDirectory(currentStatus));
        cm.addCommand(new ChangeDirectory(currentStatus));
        cm.addCommand(new MakeDirectory(currentStatus));
        cm.addCommand(new Directory(currentStatus));
        cm.addCommand(new Remove(currentStatus));
        cm.addCommand(new Copy(currentStatus));
        cm.addCommand(new Move(currentStatus));
        cm.addCommand(new Exit(currentStatus));

        if (args.length == 0) {
            Scanner sc = new Scanner(System.in);
            while (true) {
                try {
                    System.out.print(currentStatus.getCurrentDirectory() + " $ ");
                    String s = sc.nextLine();
                    int i = 0;
                    s.trim();
                    if (s.indexOf(' ') < 0) {
                        cm.execute(s.substring(i), "");
                    } else {
                        cm.execute(s.substring(0,s.indexOf(' ')), s.substring(s.indexOf(' ')));
                    }
                } catch (MyException e){
                    System.err.println("Error! " + e.what());
                } catch (IOException e) {
                    System.err.println("Error! " + e.getMessage());
                }
            }

    } else {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length - 1 ; ++i) {
                sb.append(args[i] + ' ');
            }
            sb.append(args[args.length - 1]);
            String[] commands = sb.toString().trim().split("\\s*;\\s*");
            for (int i = 0; i < commands.length; ++i){
                commands[i].trim();
                if (commands[i].indexOf(' ') < 0) {
                    cm.execute(commands[i], "");
                } else {
                    cm.execute(commands[i].substring(0,commands[i].indexOf(' ')), commands[i].substring(commands[i].indexOf(' ')));
                }
            }
        } catch (MyException e) {
            System.err.println("Error! " + e.what());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error! " + e.getMessage());
            System.exit(1);
        }
        }
    }
}
