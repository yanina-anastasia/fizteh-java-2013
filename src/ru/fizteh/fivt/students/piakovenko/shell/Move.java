package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public class Move implements Commands {
    private final String name = "mv";
    private CurrentStatus currentStatus;

    private List<String> argumnetsParser(String s) {
        List<String> array = new ArrayList<String>();
        int i = 0;
        for (; i < s.length(); ++i) {
            if (s.charAt(i) != ' ')
                break;
        }
        StringBuilder sb = new StringBuilder();
        for (; i < s.length(); ++i) {
            if (s.charAt(i) == ' ' && sb.toString().isEmpty()){
                continue;
            }
            if ((s.charAt(i) == ' ' && !sb.toString().isEmpty()) || i == s.length() -1) {
                if (i == s.length() - 1){
                    sb.append(s.charAt(i));
                }
                array.add(sb.toString());
                sb = new StringBuilder();
                continue;
            }
            sb.append(s.charAt(i));
        }
        return array;
    }

    private void removeRecursively (File f) throws MyException, IOException {
        if (!f.isDirectory()) {
            if (!f.delete()) {
                throw new MyException("Error! Unable to delete file - " + f.getCanonicalPath());
            }
        } else {
            for (File file: f.listFiles()) {
                removeRecursively(file);
            }
        }
    }

    Move(CurrentStatus cs) {
        currentStatus = cs;
    }

    public String getName() {
        return name;
    }


    public void perform(String args) throws MyException, IOException {
        List<String> array = argumnetsParser(args);
        if (array.size() != 2) {
            throw new MyException("Wrong arguments! Usage mv <source> <destination>");
        }
        Copy c = new Copy(currentStatus);
        Remove r = new Remove(currentStatus);
        c.perform(array.get(0)+ ' '+ array.get(1));
        r.perform(array.get(0));
    }
}
