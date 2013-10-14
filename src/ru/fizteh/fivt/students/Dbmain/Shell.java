package ru.fizteh.fivt.students.piakovenko.Dbmain;

import ru.fizteh.fivt.students.piakovenko.Dbmain.DataBase.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Shell {
    public static void main(String[] args) {
        try {
            DataBase.loadDataBase();
        } catch (IOException e) {
            System.err.println("Error! " + e.getMessage());
        } catch (MyException e) {
            System.err.println("Error! " + e.what());
        }
        CommandsMap cm = new CommandsMap();
        cm.addCommand(new Exit());
        cm.addCommand(new Put());
        cm.addCommand(new Get());
        cm.addCommand(new Remove());

        if (args.length == 0) {
            Scanner sc = new Scanner(System.in);
            while (true) {
                try {
                    System.out.print("FileMap $ ");
                    String s = sc.nextLine();
                    cm.execute(s);
                } catch (MyException e) {
                    System.err.println("Error! " + e.what());
                } catch (IOException e) {
                    System.err.println("Error! " + e.getMessage());
                }
            }

        } else {
            try {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < args.length - 1; ++i) {
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
