package com.intellij.generatetestcases;

import com.intellij.generatetestcases.impl.TestClassImpl;
import com.intellij.generatetestcases.testframework.JUnit4Strategy;
import com.intellij.generatetestcases.testframework.TestFrameworkStrategy;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

/**
 * User: Jaime Hablutzel
 */
public class BDDCore {
    


    protected BDDCore() {
    }


    /**
     * It loads an existing test class for the psiClass passed OR
     * creates a new TestClass will all of its test methods not yet
     * created, but available in {@link TestClass#getAllMethods()}
     * <br>
     * <b>It does create a TestClass with JUNIT 4 framework support</b>
     *
     * @param project
     * @param psiClass origin class
     * @return
     * @should create a new test class with test methods unitialized
     * @should return a test class that already exists for a sut class with some test methods initialized
     * @should ignore should tags without a description when creating bdd test methods
     * @should throw exception if there is a try to create a test class with an unsupported PsiClass
     */
    public static TestClass createTestClass(Project project, PsiClass psiClass) {
        return createTestClass(project, psiClass, new JUnit4Strategy(project));
    }

    public static TestClass createTestClass(Project project, PsiClass psiClass, TestFrameworkStrategy frameworkStrategy) {

        //  instanciar un testclass
        TestClass testClass = new TestClassImpl(psiClass, frameworkStrategy);
        return testClass;
    }

}
