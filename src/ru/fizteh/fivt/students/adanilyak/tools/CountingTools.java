package ru.fizteh.fivt.students.adanilyak.tools;

import java.util.Map;
import java.util.Set;

/**
 * User: Alexander
 * Date: 02.11.13
 * Time: 5:03
 */
public class CountingTools {
    public static int correctCountingOfChanges(Map<String, String> changes, Set<String> removedKeys) {
        int result = 0;
        for (String key : removedKeys) {
            if (changes.get(key) == null) {
                result++;
            }
        }
        result += changes.size();
        return result;
    }
}
