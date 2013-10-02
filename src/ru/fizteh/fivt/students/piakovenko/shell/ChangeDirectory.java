package ru.fizteh.fivt.students.piakovenko.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 01.10.13
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public class ChangeDirectory implements Commands {
    private final String name = "cd";
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

    ChangeDirectory(CurrentStatus cs) {
        currentStatus = cs;
    }

    public String getName() {
        return name;
    }


    public void perform(String args) throws MyException, IOException {
        List<String> array = argumnetsParser(args);
        if (array.size() != 1) {
            throw new MyException("Wrong arguments! Usage ~ cd <destination>");
        }
        File f =  new File(currentStatus.getCurrentDirectory() + File.separator + array.get(0));
        currentStatus.changeCurrentDirectory(f.getCanonicalPath());
    }

}
