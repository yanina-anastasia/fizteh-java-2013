package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Shell {
    private static List<String> commandParser(String s) {
        List<String> array = new ArrayList<String>();
        int i = 0;
        for (; i < s.length(); ++i) {
            if (s.charAt(i) != ' ')
                break;
        }
        StringBuilder sb = new StringBuilder();
        for (; i < s.length(); ++i) {
            if (s.charAt(i) == ';' && sb.toString().isEmpty()){
                continue;
            }
            if ((s.charAt(i) == ';' && !sb.toString().isEmpty()) || i == s.length() - 1) {
                array.add(sb.toString());
                sb = new StringBuilder();
            }
            sb.append(s.charAt(i));
        }
        return array;
    }

    public static void main(String[] args) {
        CurrentStatus currentStatus = null;
        try {
            currentStatus = new CurrentStatus();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
        CommandsMap cm = new CommandsMap();
        cm.addCommand(new PrintWorkingDirectory(currentStatus));
        cm.addCommand(new ChangeDirectory(currentStatus));
        cm.addCommand(new MakeDirectory(currentStatus));
        cm.addCommand(new Directory(currentStatus));
        cm.addCommand(new Remove(currentStatus));
        cm.addCommand(new Copy(currentStatus));
        cm.addCommand(new Move(currentStatus));

        if (args.length == 0) {
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print(currentStatus.getCurrentDirectory() + " $ ");
                try {
                    String s = sc.nextLine();
                    int i = 0;
                    for (; i < s.length(); ++i) {
                        if (s.charAt(i) != ' ')
                            break;
                    }
                    if (s.substring(i).equals("exit")) {
                        return;
                    } else {
                        if (s.indexOf(' ') < 0) {
                            cm.execute(s.substring(i), "");
                        } else {
                            cm.execute(s.substring(i,s.indexOf(' ')), s.substring(s.indexOf(' ')));
                        }
                }
                } catch (MyException e){
                    System.err.println("Error! " + e.what());
                } catch (IOException e) {
                    System.err.println("Error! " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Error! " + e.getMessage());
            }
        }
    } else {
        try{
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length - 1 ; ++i) {
                sb.append(args[i] + ' ');
            }
            sb.append(args[args.length - 1]);
            List<String> list = commandParser(sb.toString());
            if (list.get(list.size() - 1) != "exit"){
                System.err.println("Command \"exit\' should be in the end!");
                System.exit(1);
            }
            for (int i = 0; i < list.size(); ++i){
                String s = list.get(i);
                int j = 0;
                for (; j < s.length(); ++j) {
                    if (s.charAt(j) != ' ')
                        break;
                }
                if (s.substring(j) == "exit"){
                    return;
                } else {
                    if (s.indexOf(' ') < 0) {
                        cm.execute(s.substring(j), "");
                    } else {
                        cm.execute(s.substring(j,s.indexOf(' ')), s.substring(s.indexOf(' ')));
                    }
                }
            }
        } catch (MyException e){
            System.err.println("Error! " + e.what());
        } catch (IOException e) {
            System.err.println("Error! " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error! " + e.getMessage());
        }
        }
    }
}
