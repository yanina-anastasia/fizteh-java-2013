package ru.fizteh.fivt.students.vyatkina;

public class State {

    protected IOStreams ioStreams;
    protected FileManager fileManager;

    public State (FileManager fileManager) {
        this.fileManager = fileManager;
        this.ioStreams = new IOStreams ();
    }

    public IOStreams getIoStreams () {
        return ioStreams;
    }

    public void setIOStreams (IOStreams ioStreams) {
        this.ioStreams = ioStreams;
    }

    public FileManager getFileManager () {
        return fileManager;
    }

    public void setFileManager (FileManager fileManager) {
        this.fileManager = fileManager;
    }

}
