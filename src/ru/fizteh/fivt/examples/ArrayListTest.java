package ru.fizteh.fivt.examples;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public class ArrayListTest {
    @Test
    public void newArrayListIsEmpty() {
        ArrayList<Object> list = new ArrayList<>();
        Assert.assertEquals("size() should be 0", 0, list.size());
        Assert.assertTrue("isEmpty() should be true", list.isEmpty());
    }

    private ArrayList<String> nonEmptyArrayList;

    @Before
    public void fillArrayList() {
        nonEmptyArrayList = new ArrayList<>();
        nonEmptyArrayList.add("Hello");
        nonEmptyArrayList.add("World");
    }

    @Test
    public void testArrayListSizeIs2() {
        Assert.assertEquals(2, nonEmptyArrayList.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeIndexShouldFail() {
        nonEmptyArrayList.get(-1);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void negativeIndexShouldFail2() {
        thrown.expect(IndexOutOfBoundsException.class);
        thrown.expectMessage("-1");
        nonEmptyArrayList.get(-1);
    }
}
