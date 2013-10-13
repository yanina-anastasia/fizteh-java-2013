package ru.fizteh.fivt.students.drozdowsky.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PathController {
    private File parentDirectory;
    private String name;

    public PathController() {
        try {
            setPath(new File("").getAbsolutePath());
        } catch (IOException e) {
            // to add later
        }
    }

    public PathController(String path) {
        try {
            setPath(path);
        } catch (IOException e) {
            // to add later
        }
    }

    public PathController(PathController another) {
        this.parentDirectory = another.parentDirectory;
        this.name = another.name;
    }

    public File getPath() {
        return new File(parentDirectory, name);
    }

    public boolean isDirectory() {
        return getPath().isDirectory();
    }

    public boolean exists() {
        return getPath().exists();
    }

    public boolean isFile() {
        return getPath().isFile();
    }

    public String toString() {
        return new File(parentDirectory, name).getAbsolutePath();
    }

    private void setPath(String newPath) throws IOException {
        File testParentDirectory = new File(newPath).getParentFile();
        if (testParentDirectory == null) {
            parentDirectory = testParentDirectory;
            name = newPath;
        } else if (!testParentDirectory.exists() || !testParentDirectory.isDirectory()) {
            throw new IOException("No such file or directory");
        } else {
            parentDirectory = testParentDirectory;
            name = new File(newPath).getName();
        }
    }

    public void changePath(String newPath) throws IOException {
        newPath = newPath.trim();
        if (newPath.charAt(0) == '/') {
            setPath(newPath);
        } else {
            modifyPath(newPath);
        }
    }

    private ArrayList<String> parsePath(String path) {
        ArrayList<String> result = new ArrayList<String>();
        int last = -1;
        for (int i = 0; i < path.length(); i++) {
             if (path.charAt(i) == '/') {
                if (last + 1 != i) {
                result.add(path.substring(last + 1, i));
                }
                last = i;
            }
        }
        if (last + 1 != path.length()) {
            result.add(path.substring(last + 1, path.length()));
        }
        return result;
    }

    private void modifyPath(String modificator) throws IOException {
        if (!getPath().isDirectory()) {
            throw new IOException("Not a directory");
        }

        ArrayList<String> currentPath = parsePath(getPath().getAbsolutePath());
        ArrayList<String> modificatorPath = parsePath(modificator);
        for (String aModificatorPath : modificatorPath) {
            if (aModificatorPath.equals("..")) {
                if (currentPath.size() != 0) {
                    currentPath.remove(currentPath.size() - 1);
                }
            } else if (!aModificatorPath.equals(".")) {
                currentPath.add(aModificatorPath);
            }
        }

        if (currentPath.size() == 0) {
            name = "/";
            parentDirectory = null;
            return;
        }

        StringBuilder finalPath = new StringBuilder();
        finalPath.append("/");
        for (String aCurrentPath : currentPath) {
            finalPath.append(aCurrentPath);
            finalPath.append("/");
        }

        setPath(finalPath.toString());
    }
}

