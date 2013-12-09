package ru.fizteh.fivt.students.baranov.wordcounter;

import ru.fizteh.fivt.file.WordCounter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MyWordCounter implements WordCounter {
    public void count(List<File> files, OutputStream out, boolean aggregate) throws IOException {
        Map<String, Integer> mapOfWords = new HashMap<>();
        if (files.isEmpty()) {
            throw new IllegalArgumentException("files for counting not found");
        }

        for (int i = 0; i < files.size(); ++i) {
            if (!files.get(i).exists()) {
                if (!aggregate) {
                    out.write((files.get(i).getName() + ": file not found\n").getBytes(StandardCharsets.UTF_8));
                }
                continue;
            }
            if (files.get(i).isHidden()) {
                if (!aggregate) {
                    out.write((files.get(i).getName() + ": file not available\n").getBytes(StandardCharsets.UTF_8));
                }
                continue;
            }
            Scanner scanner = new Scanner(files.get(i));
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                String[] words = Parse(str.trim());

                for (int j = 0; j < words.length; ++j) {
                    if (mapOfWords.get(words[j]) == null) {
                        mapOfWords.put(words[j], 1);
                    } else {
                        mapOfWords.put(words[j], mapOfWords.get(words[j]) + 1);
                    }
                }
            }
            if (!aggregate) {
                out.write((files.get(i).getName() + ":\n").getBytes(StandardCharsets.UTF_8));
                PrintMap(mapOfWords, out);
                mapOfWords = new HashMap<>();
            }
            scanner.close();
        }
        if (aggregate) {
            PrintMap(mapOfWords, out);
        }
    }

    private void PrintMap(Map<String, Integer> map, OutputStream out) {
        Map<String, Integer> treeMap = new TreeMap<>(map);
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            try {
                out.write((entry.getKey() + " " + entry.getValue() + "\n").getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    private String[] Parse(String s) {
        List<String> listOfWords = new ArrayList<String>();
        s = s.toLowerCase();
        char prevCh = new Character(' ');
        String currentString = "";
        for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            if (Character.isLetterOrDigit(ch) && (Character.isLetterOrDigit(prevCh) || Character.isSpaceChar(prevCh))) {
                currentString = currentString + Character.toString(ch);
                prevCh = ch;
                continue;
            }
            if (Character.isLetterOrDigit(ch) && prevCh == '-') {
                if (!currentString.equals("")) {
                    currentString = currentString + "-" + Character.toString(ch);
                    prevCh = ch;
                } else {
                    currentString = currentString + Character.toString(ch);
                    prevCh = ch;
                }
                continue;
            }
            if (ch == '-') {
                if (prevCh == '-' && !currentString.equals("")) {
                    listOfWords.add(currentString);
                    currentString = "";
                }
                prevCh = ch;
                continue;
            }
            if (!currentString.equals("")) {
                listOfWords.add(currentString);
                currentString = "";
            }
        }
        if (!currentString.equals("")) {
            listOfWords.add(currentString);
        }

        String[] simpleArray = new String[listOfWords.size()];
        return listOfWords.toArray(simpleArray);
    }
}
