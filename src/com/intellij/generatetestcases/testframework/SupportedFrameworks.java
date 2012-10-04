package com.intellij.generatetestcases.testframework;

import com.intellij.openapi.project.Project;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Enum containing one element per supported framework, every entry should implement {@link TestFrameworkStrategy} and have at least one constructor
 * receiving only one parameter of type {@link com.intellij.openapi.project.Project} even if the implementation doesn't need it FIXME redesign to remove this obligation
 *
 * // TODO create extension point to allow another extensions to create implementations to allow to create test cases
 *
 * User: Jaime Hablutzel
 */
public enum SupportedFrameworks {

    JUNIT3(JUnit3Strategy.class), JUNIT4(JUnit4Strategy.class), TESTNG(TestNGStrategy.class);

    private Class<? extends TestFrameworkStrategy> clazz;


    SupportedFrameworks(Class<? extends TestFrameworkStrategy> clazz) {
        this.clazz = clazz;
    }

    /**
     * Should return a framework strategy based on a String
     * <p/>
     *
     * @param project
     * @param s
     * @return
     */
    public static TestFrameworkStrategy getStrategyForFramework(Project project, String s) {

        try {
            SupportedFrameworks supportedFrameworks = SupportedFrameworks.valueOf(s);
            try {
                TestFrameworkStrategy testFrameworkStrategy = supportedFrameworks.clazz.getConstructor(Project.class).newInstance(project);

                return testFrameworkStrategy;

            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("Unsupported framework: " + s);
        }

    }
}
