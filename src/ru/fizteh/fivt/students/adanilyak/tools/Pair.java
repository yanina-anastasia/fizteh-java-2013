package ru.fizteh.fivt.students.adanilyak.tools;

/**
 * User: Alexander
 * Date: 10.11.13
 * Time: 20:12
 */
public class Pair {
    private Object first;
    private Object second;

    public Pair(Object makeFirst, Object makeSecond) {
        first = makeFirst;
        second = makeSecond;
    }

    public Object getFirst() {
        return first;
    }

    public Object getSecond() {
        return second;
    }

    public void setFirst(Object makeFirst) {
        first = makeFirst;
    }

    public void setSecond(Object makeSecond) {
        second = makeSecond;
    }

    public boolean equals(Pair x) {
        return (first == x.getFirst() && second == x.getSecond());
    }

    public static boolean equals(Pair x, Pair y) {
        return (y.getFirst() == x.getFirst() && y.getSecond() == x.getSecond());
    }

    public String toString() {
        return (first.toString() + ", " + second.toString());
    }
}
