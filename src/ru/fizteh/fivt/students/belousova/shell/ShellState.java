package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShellState {
    public Map<String, Command> commandList = new HashMap<String, Command>();
    private File currentDirectory;

    public ShellState() {
        currentDirectory = new File(".");
        makeCommandList();
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String directory) throws IOException {
        File newDirectory = FileUtils.getFileFromString(directory, this);
        if (newDirectory.exists() && newDirectory.isDirectory()) {
            currentDirectory = newDirectory.getCanonicalFile();
        } else {
            throw new IOException("'" + directory + "': No such file or directory");
        }
    }

    private void makeCommandList() {
        addCommand(new CommandCd(this));
        addCommand(new CommandPwd(this));
        addCommand(new CommandDir(this));
        addCommand(new CommandCp(this));
        addCommand(new CommandMkdir(this));
        addCommand(new CommandRm(this));
        addCommand(new CommandExit(this));
        addCommand(new CommandMv(this));
    }

    private void addCommand(Command command) {
        commandList.put(command.getName(), command);
    }
}
