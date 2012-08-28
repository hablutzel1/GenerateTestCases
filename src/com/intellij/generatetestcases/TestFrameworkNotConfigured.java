package com.intellij.generatetestcases;

/**
 * Exception to be thrown if the test framework is expected to be already configured and it isn't
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
