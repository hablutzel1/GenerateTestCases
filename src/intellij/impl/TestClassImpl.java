package intellij.impl;

import intellij.TestClass;
import intellij.TestMethod;

import java.util.List;


/**
 * User: JHABLUTZEL
 * Date: 29/10/2010
 * Time: 08:57:26 AM
 */
public class TestClassImpl implements TestClass {

    List<TestMethod> testMethods;

    public TestClassImpl(List<TestMethod> testMethods) {
        this.testMethods = testMethods;
    }

    public List<TestMethod> getAllMethods() {
        return testMethods;
    }

    public void create() {

    }

    public boolean reallyExists() {
        return false;
    }
}
