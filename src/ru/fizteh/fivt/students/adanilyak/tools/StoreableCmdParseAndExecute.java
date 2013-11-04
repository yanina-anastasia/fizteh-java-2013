package ru.fizteh.fivt.students.adanilyak.tools;

import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.adanilyak.commands.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * User: Alexander
 * Date: 04.11.13
 * Time: 13:48
 */
public class StoreableCmdParseAndExecute {
    public static List<String> intoCommandsAndArgs(String cmd, String delimetr) {
        cmd.trim();
        String[] tokens = cmd.split(delimetr);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("") && !tokens[i].matches("\\s+")) {
                result.add(tokens[i]);
            }
        }
        return result;
    }

    public static void execute(String cmdWithArgs, Map<String, Cmd> cmdList) throws IOException {
        List<String> cmdAndArgs = new ArrayList<>();
        Scanner cmdScanner = new Scanner(cmdWithArgs);
        cmdAndArgs.add(cmdScanner.next());
        try {
            String commandName = cmdAndArgs.get(0);
            if (!cmdList.containsKey(commandName)) {
                throw new NoSuchElementException("Unknown command");
            }

            Cmd command = cmdList.get(commandName);
            switch (commandName) {
                case "put":
                    try {
                        cmdAndArgs.add(cmdScanner.next());
                        cmdAndArgs.add(cmdScanner.findInLine(Pattern.compile("\\[.+\\]")).replaceAll("[\\]\\[]", ""));
                    } catch (NullPointerException exc) {
                        throw new IOException("execute put: bad arguments");
                    }
                    if (cmdScanner.hasNext()) {
                        throw new IOException("Wrong amount of arguments");
                    }

                    break;
                case "create":
                    try {
                        cmdAndArgs.add(cmdScanner.next());
                        cmdAndArgs.add(cmdScanner.findInLine(Pattern.compile("\\(.+\\)")).replaceAll("[\\(|\\)]", ""));
                    } catch (NullPointerException exc) {
                        throw new IOException("execute create: bad arguments");
                    }
                    if (cmdScanner.hasNext()) {
                        throw new IOException("Wrong amount of arguments");
                    }
                    break;
                default:
                    if (cmdScanner.hasNext()) {
                        cmdAndArgs.addAll(intoCommandsAndArgs(cmdScanner.nextLine(), " "));
                    }
                    if (cmdAndArgs.size() != command.getAmArgs() + 1) {
                        throw new IOException("Wrong amount of arguments");
                    }
            }
            command.work(cmdAndArgs);
        } catch (IOException | NoSuchElementException exc) {
            System.err.println(cmdAndArgs + ": " + exc.getMessage());
        } finally {
            cmdScanner.close();
        }
    }

    public static Storeable putStringIntoStoreable(String inputValues, Table table, TableProvider provider) throws IOException {
        List<Object> valuesToPut = new ArrayList<>();
        String[] valuesAfterParse = inputValues.split(",");
        Integer i = 0;
        for (String value : valuesAfterParse) {
            if (value.equals("null")) {
                valuesToPut.add(null);
            } else {
                Class<?> type = table.getColumnType(i);
                value = value.trim();
                if (value.isEmpty()) {
                    throw new IOException("put storeable creation: bad arguments");
                }
                switch (type.getCanonicalName()) {
                    case "java.lang.Integer":
                        valuesToPut.add(new Integer(Integer.parseInt(value)));
                        break;
                    case "java.lang.Long":
                        valuesToPut.add(new Long(Long.parseLong(value)));
                        break;
                    case "java.lang.Byte":
                        valuesToPut.add(new Byte(Byte.parseByte(value)));
                        break;
                    case "java.lang.Float":
                        valuesToPut.add(new Float(Float.parseFloat(value)));
                        break;
                    case "java.lang.Double":
                        valuesToPut.add(new Double(Double.parseDouble(value)));
                        break;
                    case "java.lang.Boolean":
                        valuesToPut.add(new Boolean(Boolean.parseBoolean(value)));
                        break;
                    case "java.lang.String":
                        valuesToPut.add(value);
                        break;
                    default:
                        throw new IOException("put storeable creation: something went wrong");
                }
            }
            ++i;
        }
        return provider.createFor(table, valuesToPut);
    }

    public static String outPutToUser(Storeable storeable, Table table, TableProvider provider) {
        String jsonStringOut = provider.serialize(table, storeable);
        JSONObject jsonOut = new JSONObject(jsonStringOut);
        StringBuilder result = new StringBuilder("[");
        for (Integer i = 0; i < jsonOut.length(); ++i) {
            if (table.getColumnType(i) == String.class) {
                result.append("\"" + jsonOut.get(i.toString()) + "\"");
            } else {
                result.append(jsonOut.get(i.toString()));
            }
            if (i != jsonOut.length() - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }
}
