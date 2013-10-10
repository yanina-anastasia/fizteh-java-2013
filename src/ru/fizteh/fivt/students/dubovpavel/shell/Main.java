package ru.fizteh.fivt.students.dubovpavel.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        Listener listener = new Listener();
        if(args.length != 0) {
            StringBuilder concatenator = new StringBuilder();
            for(int i = 0; i < args.length; i++) {
                concatenator.append(args[i]);
                concatenator.append(' ');
            }
            try {
                listener.listen(concatenator.toString());
            } catch (Listener.IncorrectSyntaxException e) {
                System.err.println(e.getMessage());
                System.exit(-1);
            } catch (Shell.ShellException e) {
                System.err.println(e.getMessage());
                System.exit(-2);
            }
        } else {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                System.out.print("$ ");
                try {
                    if(!listener.listen(input.readLine())) {
                        break;
                    }
                } catch(IOException e) {
                    throw new RuntimeException(e.getMessage()); // Something should go totally wrong.
                } catch(Listener.IncorrectSyntaxException e) {
                    System.err.println(e.getMessage());
                } catch(Shell.ShellException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
