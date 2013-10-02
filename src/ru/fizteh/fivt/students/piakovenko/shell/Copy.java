package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 21:54
 * To change this template use File | Settings | File Templates.
 */
public class Copy implements Commands {
    private final String name = "cp";
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

    Copy(CurrentStatus cs) {
        currentStatus = cs;
    }

    public String getName() {
        return name;
    }


    public void perform(String args) throws MyException, IOException {
        List<String> array = argumnetsParser(args);
        if (array.size() != 2) {
            throw new MyException("Wrong arguments! Usage ~ cp <source> <destination>");
        }
        File from, to;
        from = new File(array.get(0));
        if (!from.isAbsolute()) {
            from = new File(currentStatus.getCurrentDirectory() + File.separator + array.get(0));
        }
        to = new File(array.get(1));
        if (!to.isAbsolute()) {
            to = new File(currentStatus.getCurrentDirectory() + File.separator + array.get(1));
        }
        if (!to.exists()) {
            if (to.getName().indexOf('.') == -1) {
                to.mkdirs();
            }
            else {
                to.createNewFile();
            }
        }
        if (from.isFile() &&  to.isFile()) {
            currentStatus.copy(from, to);
        }  else if (from.isFile() && to.isDirectory()) {
            File fromNew = new File(to.getCanonicalPath() + File.separator + from.getName());
            fromNew.createNewFile();
            currentStatus.copy(from, fromNew);
        } else if (from.isDirectory()) {
            currentStatus.copyRecursively(from, to);
        } else {
            throw new MyException("Error! " + array.get(0) + " should be a directory!");
        }
    }
}
