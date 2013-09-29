package ru.fizteh.fivt.students.piakovenko.calculator;

import java.io.IOException;

public class Node {
    private String s;
    private Node rightNode;
    private Node leftNode;

   //"("  "((A*A" ")+ B" ")" "-C)" "/2"
    private int calculation(int dec) throws IOException {
        if (s.equals("*")) {
            if (Integer.MAX_VALUE /Integer.parseInt(leftNode.getString(), dec) < Integer.parseInt(rightNode.getString(), dec )) {
                throw(new IOException("Ovefflow of integer"));
            }
            return Integer.parseInt(leftNode.getString(), dec) * Integer.parseInt(rightNode.getString(), dec);
        } else if (s.equals("/")) {
            if (Integer.parseInt(rightNode.getString(), dec) == 0 ) {
                throw(new IOException("Trying divide by zero"));
            }
            return Integer.parseInt(leftNode.getString(), dec) / Integer.parseInt(rightNode.getString(), dec);
        } else if (s.equals("-")) {
            if ( Integer.signum(Integer.parseInt(leftNode.getString(), dec)) == Integer.signum(Integer.parseInt(rightNode.getString(), dec)) ) {
                if (Integer.MAX_VALUE - Integer.parseInt(leftNode.getString(), dec) < Integer.parseInt(rightNode.getString(), dec )) {
                    throw(new IOException("Ovefflow of integer"));
                }
            }
            return Integer.parseInt(leftNode.getString(), dec) - Integer.parseInt(rightNode.getString(), dec);
        } else {
            if ( Integer.signum(Integer.parseInt(leftNode.getString(), dec)) == Integer.signum(Integer.parseInt(rightNode.getString(), dec)) ) {
                   if (Integer.MAX_VALUE - Integer.parseInt(leftNode.getString(), dec) < Integer.parseInt(rightNode.getString(), dec )) {
                        throw(new IOException("Ovefflow of integer"));
                   }
            }
            return Integer.parseInt(leftNode.getString(), dec) + Integer.parseInt(rightNode.getString(), dec);
        }
    }

    public Node(String s1) {
        this.s = s1;
        this.leftNode = new Node();
        this.rightNode = new Node();
    }

    public Node() {
        this.s = "";
    }

    public boolean isEmpty() {
        return s.isEmpty();
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

    public void calculate() throws IOException {
        if (this.leftNode.isEmpty() && this.rightNode.isEmpty()){
            return;
        }
        if (!this.leftNode.isEmpty()) {
            leftNode.calculate();
        }
        if ( !this.rightNode.isEmpty()) {
            rightNode.calculate();
        }
        this.s = Integer.toString(calculation(19), 19);
        leftNode.s = "";
        rightNode.s = "";
    }
}
