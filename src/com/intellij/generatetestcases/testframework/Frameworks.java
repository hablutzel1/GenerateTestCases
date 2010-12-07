package com.intellij.generatetestcases.testframework;

/**
 * User: Jaime Hablutzel
 */
public enum Frameworks {

    JUNIT3("JUnit 3"), JUNIT4("JUnit 4");

    private String name;

    Frameworks(String readableName) {
        this.name = readableName;
    }

}
