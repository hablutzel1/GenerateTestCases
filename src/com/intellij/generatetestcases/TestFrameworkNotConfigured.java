package com.intellij.generatetestcases;

/**
 * Created by jhe
 * Time: 19:34
 */
public class TestFrameworkNotConfigured extends Exception {

    public TestFrameworkNotConfigured() {
    }

    public TestFrameworkNotConfigured(String message) {
        super(message);
    }

    public TestFrameworkNotConfigured(String message, Throwable cause) {
        super(message, cause);
    }

    public TestFrameworkNotConfigured(Throwable cause) {
        super(cause);
    }
}
