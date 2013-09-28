package ru.fizteh.fivt.students.vyatkina.shell;

import java.util.Scanner;
import java.util.HashMap;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 23.09.13
 * Time: 22:00
 * To change this template use File | Settings | File Templates.
 */
public class Shell {
    Scanner shellScanner = new Scanner (System.in);
    File currentDirectory = new File (".");
    HashMap <String,Command> commands = new HashMap<String, Command>();
    final String possibleCommands = "(pwd|exit|dir|cd|mkdir|rm|cp|rm|mv)";

    public Shell ()
    {
       initializeCommands();
    }

    private void initializeCommands ()
    {
        commands.put ("pwd", new pwdCommand ());
        commands.put ("exit", new exitCommand ());
        commands.put ("dir", new dirCommand ());
        commands.put ("mkdir", new mkdirCommand ());
        commands.put ("rm", new rmCommand ());
        commands.put ("cp", new cpCommand ());
        commands.put ("cd", new cdCommand ());
        commands.put ("mv", new mvCommand ());
    }

    public static void main (String [] args) throws Exception {
        Shell shell = new Shell ();
        if ( args.length > 0 ) {
            for (String s: args) {
                System.out.println (s);
            }
        } else {
            shell.startInteractiveMode();
        }
    }

    private  void startInteractiveMode () throws Exception {
        String currentCommandName = null;
        while (true) {
        System.out.print(currentDirectory.getAbsolutePath() + "$ ");
        if (shellScanner.hasNext (possibleCommands)) {
            currentCommandName = shellScanner.next (possibleCommands);
            commands.get (currentCommandName).execute(this);
        } else {
            System.err.println ("Unknown command:" + shellScanner.next ());
         }
        }
    }




}
