package ru.fizteh.fivt.students.fedoseev.common;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public abstract class AbstractFrame implements Frame {
    public class FrameState {
        private File curState;

        public void setCurState(File file) {
            curState = file;
        }

        public File getCurState() {
            return curState;
        }
    }

    public abstract Map<String, AbstractCommand> getCommands();

    protected FrameState state = new FrameState();

    public void setObjectCurState(File file) {
        state.setCurState(file);
    }

    @Override
    public void runCommands(String cmd, int end) throws IOException, InterruptedException {
        Map<String, AbstractCommand> commands = getCommands();

        if (!commands.containsKey(cmd.substring(0, end))) {
            throw new IOException("\"ERROR: not existing command \"" + cmd.substring(0, end) + "\"");
        }

        AbstractCommand command = commands.get(cmd.substring(0, end));

        if (Utils.getCommandArguments(cmd).length != command.getArgsCount()) {
            throw new IOException(command.getCmdName() + " ERROR: \"" + command.getCmdName() +
                    "\" command receives " + command.getArgsCount() + " arguments");
        }

        command.execute(Utils.getCommandArguments(cmd), state);
    }

    @Override
    public void BatchMode(String[] args) {
        String[] input = Utils.join(args, " ").split("\\s*;\\s*");

        for (String cmd : input) {
            if (!Thread.currentThread().isInterrupted()) {
                cmd = cmd.trim();

                int end;
                if ((end = cmd.indexOf(" ")) == -1) {
                    end = cmd.length();
                }

                try {
                    runCommands(cmd, end);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }

    }

    @Override
    public void InteractiveMode() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        while (!Thread.currentThread().isInterrupted()) {
            Thread.currentThread().sleep(10);
            System.out.print(state.getCurState().toString() + "$ ");

            String[] input = scanner.nextLine().trim().split("\\s*;\\s*");

            for (String cmd : input) {
                cmd = cmd.trim();

                int end;
                if ((end = cmd.indexOf(" ")) == -1) {
                    end = cmd.length();
                }

                try {
                    if (cmd.substring(0, end).length() == 0) {
                        continue;
                    }

                    runCommands(cmd, end);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
