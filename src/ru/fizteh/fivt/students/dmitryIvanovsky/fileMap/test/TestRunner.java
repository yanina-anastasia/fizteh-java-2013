package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        runTest(TestFileMap.class);
        runTest(TestFileMapProvider.class);
        runTest(TestFileMapProviderFactory.class);
        runTest(TestFileMapStoreable.class);
    }

    public static void runTest(Class testClass) {
        Result result = JUnitCore.runClasses(testClass);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }

}
