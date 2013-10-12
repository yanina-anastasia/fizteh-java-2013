package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;
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
                    cm.execute(s);
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
           cm.execute(sb.toString());
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
