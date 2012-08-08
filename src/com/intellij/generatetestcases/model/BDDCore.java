package com.intellij.generatetestcases.model;

import com.intellij.generatetestcases.TestFrameworkNotConfigured;
import com.intellij.generatetestcases.testframework.TestFrameworkStrategy;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.apache.commons.lang.StringUtils;

/**
 * In a 'service provider framework' like BDDCore would be the 'service access API'
 * Effective java, item 1
 *
 * User: Jaime Hablutzel
 */
public final class BDDCore {


    protected BDDCore() {
    }


    /**
     * @param sutClass
     * @param frameworkStrategy
     * @return
     * @deprecated use com.intellij.generatetestcases.model.BDDCore#createTestClass(com.intellij.openapi.project.Project, com.intellij.psi.PsiClass) instead
     */
    public static TestClass createTestClass(PsiClass sutClass, TestFrameworkStrategy frameworkStrategy) {

        //  instanciar un testclass
        TestClass testClass = TestClassImpl.newInstance(sutClass, frameworkStrategy);
        return testClass;
    }

    /**
     * It will create a test class and it will use the test framework configured for this project, if the project doesn't have a TestFramework selected it will throw an exception of type {@link com.intellij.generatetestcases.TestFrameworkNotConfigured}
     * <br />
     * It loads an existing test class for the psiClass passed OR
     * creates a new TestClass will all of its test methods not yet
     * created, but available in {@link TestClass#getAllMethods()}
     * <p>
     *     <b>Note:</b>
     *     In unit test mode it will use the JUNIT3 Test Framework
     * </p>
     *
     *
     * @param sutClass the sut class to be tested
     * @return
     * @should create a new test class with test methods unitialized
     * @should return a test class that already exists for a sut class with some test methods initialized
     * @should ignore should tags without a description when creating bdd test methods
     * @should throw exception if there is a try to create a test class with an unsupported PsiClass
     *
     */
    public static TestClass createTestClass(PsiClass sutClass) throws TestFrameworkNotConfigured {
        Project project = sutClass.getProject();

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
        return TestClassImpl.newInstance(sutClass, BddUtil.getStrategyForFramework(project, testFramework));
    }

}
