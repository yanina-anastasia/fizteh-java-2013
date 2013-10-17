package ru.fizteh.fivt.students.paulinMatavina.utils;

import java.util.Scanner;
import java.util.StringTokenizer;

public class CommandRunner {    
    public static void run(String[] args, State state) {  
        if (args.length > 0) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i]);
                str.append(" ");
            }
            int status = executeQueryLine(str.toString(), state);
            System.exit(status);
        } else {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                if (scanner.hasNextLine()) {
                    String queryLine = scanner.nextLine();
                    executeQueryLine(queryLine, state);
                } else {
                    scanner.close();
                    return;
                }
            } 
        }
    }
     
    private static int execute(String query, State state) {
        query = query.trim();
        if (query.equals("")) {
            return 0;
        }  
        StringTokenizer token = new StringTokenizer(query);
        int tokenNum = token.countTokens();
        String nextCommand = token.nextToken();
        
        for (Command command : state.commands.values()) {
            if (command.getName().equals(nextCommand)) {
                if (command.getArgNum() != tokenNum - 1 
                        && (command.spaceAllowed() && command.getArgNum() >= tokenNum)) {
                    System.err.println("Wrong arguments number: " 
                            + command.getArgNum() + " expected");
                    return 1;
                }
                
                String[] args = new String[command.getArgNum()];
                if (command.spaceAllowed()) {
                    for (int i = 0; i < command.getArgNum() - 1; i++) {
                        args[i] = token.nextToken();
                    }
                    
                    int currSize = 0;
                    for (int i = 0; i < command.getArgNum(); i++) {
                        while (Character.toString(query.charAt(currSize)).matches("\\S")) {
                            currSize++;
                        }
                        while (Character.toString(query.charAt(currSize)).matches("\\s")) {
                            currSize++;
                        } 
                    }
                    args[command.getArgNum() - 1] = query.substring(currSize).trim();
                    return command.execute(args, state);
                }                
                for (int i = 0; i < command.getArgNum(); i++) {
                    args[i] = token.nextToken();
                }
                return command.execute(args, state);
            }
        }
        
        System.err.println("Wrong command " + nextCommand);
        return 1;
    }

    private static int executeQueryLine(String queryLine, State state) {
        Scanner scanner = new Scanner(queryLine);
        scanner.useDelimiter(";");
        while (scanner.hasNext()) {
            String query = scanner.next();
            int status = execute(query, state);
            if (status != 0) {
                scanner.close();
                return status;
            }  
        }
        scanner.close();
        return 0;
    }  
}
