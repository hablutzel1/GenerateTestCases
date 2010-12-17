package com.intellij.generatetestcases.testframework;

/**
 * User: Jaime Hablutzel
 *
 * // TODO rename to SupportedFrameworks
 * // TODO create extension point to allow another extensions to create implementations to allow to create
 * test cases 
 *
 */
public enum Frameworks {

    JUNIT3("JUnit 3"), JUNIT4("JUnit 4");

    private String name;

    Frameworks(String readableName) {
        this.name = readableName;
    }

}
