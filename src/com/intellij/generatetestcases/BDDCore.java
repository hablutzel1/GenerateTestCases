package com.intellij.generatetestcases;

import com.intellij.generatetestcases.impl.GenerateTestCasesSettings;
import com.intellij.generatetestcases.impl.TestClassImpl;
import com.intellij.generatetestcases.testframework.JUnit4Strategy;
import com.intellij.generatetestcases.testframework.TestFrameworkStrategy;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.generatetestcases.util.Constants;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.apache.commons.lang.StringUtils;

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
     * <p/>
     * TODO remove this method and move test methods to com.intellij.generatetestcases.BDDCore#createTestClass(com.intellij.openapi.project.Project, com.intellij.psi.PsiClass)
     *
     * @param project
     * @param psiClass origin class
     * @return
     * @should create a new test class with test methods unitialized
     * @should return a test class that already exists for a sut class with some test methods initialized
     * @should ignore should tags without a description when creating bdd test methods
     * @should throw exception if there is a try to create a test class with an unsupported PsiClass
     * @deprecated use com.intellij.generatetestcases.BDDCore#createTestClass(com.intellij.openapi.project.Project, com.intellij.psi.PsiClass) instead
     */
    public static TestClass createTestClass(Project project, PsiClass psiClass) {
        return createTestClass(project, psiClass, new JUnit4Strategy(project));
    }

    /**
     * @param project
     * @param psiClass
     * @param frameworkStrategy
     * @return
     * @deprecated use com.intellij.generatetestcases.BDDCore#createTestClass(com.intellij.openapi.project.Project, com.intellij.psi.PsiClass) instead
     */
    public static TestClass createTestClass(Project project, PsiClass psiClass, TestFrameworkStrategy frameworkStrategy) {

        //  instanciar un testclass
        TestClass testClass = new TestClassImpl(psiClass, frameworkStrategy);
        return testClass;
    }

    /**
     * It will create a test class and it will use the test framework configured for this project, if the project doesn't have a TestFramework selected it will throw an exception of type {@link TestFrameworkNotConfigured}
     *
     * @param psiClass the sut class to be tested
     * @return
     */
    public static TestClass createTestClass(PsiClass psiClass) throws TestFrameworkNotConfigured {
        Project project = psiClass.getProject();

        String testFramework;

        if (ApplicationManager.getApplication().isUnitTestMode()) {
            testFramework = "JUNIT3";
        } else {


            testFramework = GenerateTestCasesSettings.getInstance(project).getTestFramework();
            if (StringUtils.isEmpty(testFramework)) {
                throw new TestFrameworkNotConfigured();
            }

        }
        //  create TestClass for current class
        return new TestClassImpl(psiClass, BddUtil.getStrategyForFramework(project, testFramework));
    }

}
