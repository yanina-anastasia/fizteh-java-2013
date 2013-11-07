package ru.fizteh.fivt.students.dubovpavel.executor;

import java.util.ArrayList;

public class Parser {
    public class IncorrectSyntaxException extends Exception {
        public IncorrectSyntaxException(String message) {
            super(message);
        }
    }
    private boolean charIsNotSystem(char c) {
        return !Character.isWhitespace(c) && c != ';' && c != '\'' && c != '"';
    }

    public ArrayList<Command> getCommands(Dispatcher dispatcher, String line) throws IncorrectSyntaxException {
        int pointer = 0;
        ArrayList<Command> result = new ArrayList<Command>();
        Command command = null;
        while(pointer < line.length()) {
            if(Character.isWhitespace(line.charAt(pointer))) {
                pointer++;
            } else if(line.charAt(pointer) == '\'' || line.charAt(pointer) == '"') {
                int start = pointer;
                for(pointer++; pointer < line.length() && line.charAt(pointer) != line.charAt(start); pointer++);
                if(pointer >= line.length()) {
                    throw new IncorrectSyntaxException(
                            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, "Quotes are not closed"));
                }
                if(command == null) {
                    pointer++;
                    command = new Command(line.substring(start, pointer), result.size() + 1);
                } else {
                    command.addArgument(line.substring(start + 1, pointer));
                    pointer++;
                }
            } else if(line.charAt(pointer) == ';') {
                if(command != null) {
                    result.add(command);
                    command = null;
                }
                pointer++;
            } else {
                int start = pointer;
                while(pointer < line.length() && charIsNotSystem(line.charAt(pointer))) {
                    pointer++;
                }
                String word = line.substring(start, pointer);
                if(command == null) {
                    command = new Command(word, result.size() + 1);
                } else {
                    command.addArgument(word);
                }
            }
        }
        if(command != null) {
            result.add(command);
        }
        return result;
    }
}
