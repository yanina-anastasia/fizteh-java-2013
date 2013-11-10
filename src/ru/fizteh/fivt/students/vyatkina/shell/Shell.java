package ru.fizteh.fivt.students.vyatkina.shell;

import ru.fizteh.fivt.students.vyatkina.Command;
import ru.fizteh.fivt.students.vyatkina.shell.commands.ExitCommand;
import ru.fizteh.fivt.students.vyatkina.FileManager;
import ru.fizteh.fivt.students.vyatkina.State;
import ru.fizteh.fivt.students.vyatkina.shell.commands.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Shell {

    State state;
    private HashMap<String, Command> COMMAND_MAP = new HashMap<> ();
    Mode mode;

    public enum Mode {
        PACKET,
        INTERACTIVE;
    }

    public Shell (Collection<Command> commands, Mode mode, State state) {
        this.state = state;

        if (commands == null) {
            commands = standardCommands ();
        }
        for (Command c : commands) {
            COMMAND_MAP.put (c.getName (), c);
        }
        this.mode = mode;
    }

    Set<Command> standardCommands () throws IllegalArgumentException {

        Set<Command> commands = new HashSet ();
        commands.add (new DirCommand (state));
        commands.add (new PwdCommand (state));
        commands.add (new ExitCommand (state));
        commands.add (new CdCommand (state));
        commands.add (new MkdirCommand (state));
        commands.add (new CpCommand (state));
        commands.add (new RmCommand (state));
        commands.add (new MvCommand (state));

        return commands;
    }


    public static void main (String[] args) {
        Shell shell;
        try {
            if (args.length == 0) {
                shell = new Shell (null, Mode.INTERACTIVE, new State (new FileManager ()));
            } else {
                shell = new Shell (null, Mode.PACKET, new State (new FileManager ()));
            }
            shell.startWork (args);
        }
        catch (IllegalArgumentException e) {
            System.out.println (e.getMessage ());
        }
    }

    public void startWork (String[] args) {
        switch (mode) {
            case INTERACTIVE: {
                startInteractiveMode ();
                break;
            }
            case PACKET: {
                startPacketMode (args);
            }
        }
    }

    private class CommandToExecute {

        Command command;
        String[] args;

        CommandToExecute (Command command, String[] args) {
            this.command = command;
            this.args = args;
        }
    }

    ArrayList<CommandToExecute> parseArguments (String input) throws IllegalArgumentException {
        ArrayList<CommandToExecute> commandsToExecute = new ArrayList<> ();
        String[] commandsWithArgs = input.trim ().split ("\\s*;\\s*");
        for (String command : commandsWithArgs) {
            String[] splitted = command.split ("\\s+");
            if (COMMAND_MAP.get (splitted[0]) != null) {
                Command cmd = COMMAND_MAP.get (splitted[0]);
                int argsNumber = cmd.getArgumentCount ();
                if (splitted.length - 1 == argsNumber) {
                    String[] args = new String[argsNumber];
                    for (int i = 0; i < argsNumber; i++) {
                        args[i] = splitted[i + 1];
                    }
                    commandsToExecute.add (new CommandToExecute (cmd, args));
                } else {
                    throw new IllegalArgumentException ("Wrong number of arguments in " + cmd.getName () + ": needed: "
                            + argsNumber + " have: " + (splitted.length - 1));
                }
            } else {
                throw new IllegalArgumentException ("Unknown command: [" + splitted[0] + "]");
            }
        }
        return commandsToExecute;
    }

    private void startPacketMode (String[] args) {
        StringBuilder sb = new StringBuilder ();
        for (String arg : args) {
            sb.append (arg);
            sb.append (" ");
        }
        try {
            ArrayList<CommandToExecute> commandsToExecute = parseArguments (sb.toString ());

            for (CommandToExecute cmd : commandsToExecute) {
                cmd.command.execute (cmd.args);
            }

            COMMAND_MAP.get ("exit").execute (new String[0]);
        }
        catch (IllegalArgumentException | ExecutionException e) {
            state.getIoStreams ().out.println (e.getMessage ());
            System.exit (-1);
        }
    }

    private void startInteractiveMode () {
        Scanner scanner = new Scanner (state.getIoStreams ().in);
        while (!Thread.currentThread ().isInterrupted ()) {
            state.getIoStreams ().out.print ("$ ");
            String line = scanner.nextLine ();
            try {
                ArrayList<CommandToExecute> commandsToExecute = parseArguments (line);
                for (CommandToExecute cmd : commandsToExecute) {
                    cmd.command.execute (cmd.args);
                }
            }
            catch (IllegalArgumentException | ExecutionException e) {
                state.getIoStreams ().out.println (e.getMessage ());
            }
        }
    }


}
