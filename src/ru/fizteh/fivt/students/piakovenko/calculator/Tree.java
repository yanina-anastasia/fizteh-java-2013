package ru.fizteh.fivt.students.piakovenko.calculator;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;


public class Tree {

    private int findBracket(String s, int from) {
        int bracketsSum = 0;
        for (int i = from; i < s.length(); ++i) {
            if (s.charAt(i) == '(') {
                ++bracketsSum;
            } else if (s.charAt(i) == ')') {
                --bracketsSum;
            }
            if (bracketsSum == 0) {
                return i;
            }
        }
        return -1;
    }

    public List <Node> parseExpressionNodes(String s) throws IOException {
        if (s.isEmpty()) {
            System.err.println("No numbers in brackets!");
            System.exit(6);
        }
        List <Node> l =  new ArrayList<Node>();
        boolean isSymbol = false, isNumber = true;
        for (int i = 0; i < s.length(); ++i){
            StringBuilder temp = new StringBuilder();
            if (s.charAt(i) == '('){
                int tp = findBracket(s, i);
                l.add(parseCalculationTree(s.substring(i + 1, tp)));
                s = s.substring(0, i) + s.substring(tp + 1);
                --i;
                isSymbol = false;
                continue;
            }
            if (s.charAt(i) == '\t' || s.charAt(i) == '\n' || s.charAt(i) == ' ') {
                continue;
            }
            while (i < s.length() && s.charAt(i) != '+' &&  s.charAt(i) != '-' && s.charAt(i) != '*' && s.charAt(i) != '/') {
                if (s.charAt(i) == '\t' || s.charAt(i) == '\n' || s.charAt(i) == ' ') {
                    if (!temp.toString().isEmpty()) {
                        isNumber = false;
                    }
                    ++i;
                    continue;
                }
                if (!isNumber && !temp.toString().isEmpty()) {
                    throw new IOException("No arifmetic symbol between two number!");
                }
                isNumber = true;
                temp.append(s.charAt(i));
                ++i;
            }
            if (!temp.toString().isEmpty()) {
                l.add(new Node(temp.toString()));
                isSymbol = false;
                isNumber = false;
            }
            if (i < s.length() - 1) {
                if (isSymbol) {
                    throw (new IOException("Two symbols together (++, --, */, etc)"));
                }
                StringBuilder sb = new StringBuilder();
                sb.append(s.charAt(i));
                isSymbol = true;
                l.add(new Node(sb.toString()));
            }
        }
        return l;
    }

    public Node buildCalculationTree(List <Node> l) {
        for (int i =0; i < l.size(); ++i) {
            if (l.get(i).equal("*") || l.get(i).equal("/")) {
                l.get(i).addLeftNode(l.remove(i - 1));
                l.get(i-1).addRightNode(l.remove(i));
                --i;
            }
        }
        for (int i =1; i < l.size(); ++i) {
                l.get(i).addLeftNode(l.remove(i-1));
                l.get(i-1).addRightNode(l.remove(i));
                --i;
        }
        return l.get(0);
    }

    public Node parseCalculationTree(String s) throws IOException {
        List <Node> l =  parseExpressionNodes(s);
        Node f = buildCalculationTree(l);
        f.calculate();
        return f;
    }
}
