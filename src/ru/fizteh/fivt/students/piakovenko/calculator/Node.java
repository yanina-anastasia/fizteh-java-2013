package ru.fizteh.fivt.students.piakovenko.calculator;

import static java.lang.Math.pow;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 21.09.13
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
public class Node {
    private String s;
    private boolean isSymbol;
    private Node rightNode;
    private Node leftNode;

    private int toNumber(String s, int dec) {
        int p = 0;
        for (int i = s.length() -1; i >= 0; --i) {
            if (s.charAt(i) >= 'A') {
                p += (s.charAt(i) - 'A' + 10) * pow((double)dec, s.length() - i - 1  );
            }
            else if (s.charAt(i) >= '0') {
                p += (s.charAt(i) - '0') * pow((double)dec, (s.length() - i - 1));
            }
        }
        if (!s.isEmpty() && s.charAt(0) == '-') {
            p *= -1;
        }
        return p;
    }

    private String toString(int n, int dec) {
        StringBuilder t = new StringBuilder();
        if (n < 0) {
            n *= -1;
            while (n > 0) {
                int temp = n % dec;
                if (temp < 10) {
                    t.insert(0, (char)(temp + '0'));
                }
                else {
                    t.insert(0, (char)(temp - 10 + 'A'));
                }
                n /= dec;
            }
            t.insert(0, "-");
            return t.toString();
        }
        while (n > 0) {
            int temp = n % dec;
            if (temp < 10) {
                t.insert(0,(char) (temp + '0'));
            }
            else {
                t.insert(0,(char)(temp - 10 + 'A'));
            }
            n /= dec;
        }
        return t.toString();
    }

    private int calculation(int dec) {
        if (s.equals("*")) {
            return toNumber(leftNode.getString(), dec) * toNumber(rightNode.getString(), dec);
        }
        else if (s.equals("/")) {
            if (toNumber(rightNode.getString(), dec) == 0 ) {
                System.out.println("Programm tried to divide by zero");
                System.exit(-1);
            }
            return (toNumber(leftNode.getString(), dec) / toNumber(rightNode.getString(), dec));
        }
        else if (s.equals("-")) {
            return toNumber(leftNode.getString(), dec) - toNumber(rightNode.getString(), dec);
        }
        else {
            return toNumber(leftNode.getString(), dec) + toNumber(rightNode.getString(), dec);
        }
    }

    public Node(String s1) {
        this.s = s1;
        this.isSymbol = false;
        this.leftNode = new Node();
        this.rightNode = new Node();
    }

    public Node() {
        this.s = "";
        this.isSymbol = false;
    }

    public Node(String s1, boolean flag) {
        this.s = s1;
        this.isSymbol = flag;
        this.leftNode = new Node();
        this.rightNode = new Node();
    }

    public boolean isEmpty() {
        if (s ==  "") {
            return true;
        }
        return false;
    }

    public void addRightNode(Node n) {
        this.rightNode = n;
    }

    public void addLeftNode(Node n) {
        this.leftNode = n;
    }
    public String getString() {
        return s;
    }

    public boolean equal(String t) {
        return this.s.equals(t);
    }

    public void Calculate() {
        if (this.leftNode.isEmpty() && this.rightNode.isEmpty()){
            return;
        }
        if (!this.leftNode.isEmpty()) {
            leftNode.Calculate();
        }
        if ( !this.rightNode.isEmpty()) {
            rightNode.Calculate();
        }
        this.s = toString(calculation(19), 19);
        leftNode.s = "";
        rightNode.s = "";
    }
}
