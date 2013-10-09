package ru.fizteh.fivt.students.dubovpavel.shell;

import java.util.ArrayList;

public class Listener {
    public class IncorrectSyntaxException extends Exception {
        IncorrectSyntaxException(String message) {
            super(message);
        }
    }
    private boolean charIsNotSystem(char c) {
        return !Character.isWhitespace(c) && c != ';' && c != '\'' && c != '"';
    }

    private ArrayList<ArrayList<String>> splitLexems(String line) throws IncorrectSyntaxException {
        int pointer = 0;
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        ArrayList<String> command = new ArrayList<String>();
        while(pointer < line.length()) {
            if(Character.isWhitespace(line.charAt(pointer))) {
                pointer++;
            } else if(line.charAt(pointer) == '\'' || line.charAt(pointer) == '"') {
                int start = pointer;
                for(pointer++; pointer < line.length() && line.charAt(pointer) != line.charAt(start); pointer++);
                if(pointer >= line.length()) {
                    throw new IncorrectSyntaxException("Quotes are not closed.");
                }
                if(command.size() == 0) {
                    pointer++;
                    command.add(line.substring(start, pointer));
                } else {
                    command.add(line.substring(start + 1, pointer));
                    pointer++;
                }
            } else if(line.charAt(pointer) == ';') {
                result.add(command);
                command = new ArrayList<String>();
                pointer++;
            } else {
                int start = pointer;
                while(pointer < line.length() && charIsNotSystem(line.charAt(pointer))) {
                    pointer++;
                }
                command.add(line.substring(start, pointer));
            }
        }
        if(command.size() != 0) {
            result.add(command);
        }
        return result;
    }

    private boolean matches(ArrayList<String> command, String name, int size) {
        return command.get(0).equals(name) && command.size() == size;
    }

    public boolean listen(String line) throws IncorrectSyntaxException, Shell.ShellException {
        ArrayList<ArrayList<String>> lexems = splitLexems(line);
        Shell shell = new Shell();
        for(int i = 0; i < lexems.size(); i++) {
            if(matches(lexems.get(i), "cd", 2)) { //C# poops on Java here.
                shell.changeDirectory(lexems.get(i).get(1));
            } else if(matches(lexems.get(i), "mkdir", 2)) {
                shell.createDirectory(lexems.get(i).get(1));
            } else if(matches(lexems.get(i), "pwd", 1)) {
                shell.printWorkingDirectory();
            } else if(matches(lexems.get(i), "rm", 2)) {
                shell.remove(lexems.get(i).get(1));
            } else if(matches(lexems.get(i), "cp", 3)) {
                shell.copy(lexems.get(i).get(1), lexems.get(i).get(2));
            } else if(matches(lexems.get(i), "mv", 3)) {
                shell.move(lexems.get(i).get(1), lexems.get(i).get(2));
            } else if(matches(lexems.get(i), "dir", 1)) {
                shell.printDirectoryContent();
            } else if(matches(lexems.get(i), "exit", 1)) {
                return false;
            } else {
                throw new IncorrectSyntaxException(String.format("Unknown command #%d or invalid amount of arguments: '%s'.", i + 1, lexems.get(i).get(0)));
            }
        }
        return true;
    }
}
