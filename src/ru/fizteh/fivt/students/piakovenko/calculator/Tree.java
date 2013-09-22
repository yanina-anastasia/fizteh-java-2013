package ru.fizteh.fivt.students.piakovenko.calculator;

import java.util.ArrayList;
import java.util.List;


public class Tree {

    private String copyString(String s, int from, int to) {
        StringBuilder t  = new StringBuilder(5);
        for (int i = from; i <= to; ++i) {
            t.append(s.charAt(i));
        }
        return t.toString();
    }

    private int findBracket(String s, int from) {
        int bracketsSum = 0;
        for (int i = from; i < s.length(); ++i) {
            if (s.charAt(i) == '(') {
                ++bracketsSum;
            }
            else if (s.charAt(i) == ')') {
                --bracketsSum;
            }
            if (bracketsSum == 0) {
                return i;
            }
        }
        return -1;
    }

    private String deleteString(String s, int from, int to) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < from; ++i) {
            temp.append(s.charAt(i));
        }
        for (int i = to +1; i < s.length(); ++i ) {
            temp.append(s.charAt(i));
        }
        return temp.toString();
    }

    public List <Node> firstTime(String s) {
        List <Node> l =  new ArrayList<Node>();
        StringBuilder temp = new StringBuilder();
        boolean isSymbol = false;
        for (int i = 0; i < s.length(); ++i){
            if (s.charAt(i) == '('){
                int tp = findBracket(s, i);
                l.add(parser( copyString (s, i + 1,tp - 1)));
                s = deleteString(s, i, tp);
                --i;
                isSymbol = false;
                continue;
            }
            if (s.charAt(i) == '\t' || s.charAt(i) == '\n' || s.charAt(i) == ' ') {
                continue;
            }
            while (i < s.length() && s.charAt(i) != '+' &&  s.charAt(i) != '*' && s.charAt(i) != '/') {
                if (s.charAt(i) == '-' && i == 0) {
                    temp.append(s.charAt(i));
                    isSymbol = false;
                    ++i;
                    continue;
                }
                else if (s.charAt(i) == '-') {
                    break;
                }
                if (s.charAt(i) == '\t' || s.charAt(i) == '\n' || s.charAt(i) == ' ') {
                    ++i;
                    continue;
                }
                temp.append(s.charAt(i));
                ++i;
            }
            if (!temp.toString().equals("")) {
                l.add(new Node(temp.toString()));
                isSymbol = false;
                temp.delete(0, temp.length());
            }
            if (i < s.length() - 1) {
                if (isSymbol)
                    throw (new RuntimeException("Two symbols together (++, --, */, etc)"));
                temp.append(s.charAt(i));
                isSymbol = true;
                l.add(new Node(temp.toString(), true));
                temp.delete(0, temp.length());
            }
        }
        return l;
    }

    public Node secondTime(List <Node> l) {
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

    public Node parser(String s) {
        List <Node> l =  firstTime(s);
        Node f = secondTime(l);
        f.Calculate();
        return f;
    }
}
