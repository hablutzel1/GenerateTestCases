package com.intellij.generatetestcases.quickfix;

import com.intellij.generatetestcases.*;
import com.intellij.generatetestcases.model.BDDCore;
import com.intellij.generatetestcases.model.TestClass;
import com.intellij.generatetestcases.model.TestMethod;
import com.intellij.generatetestcases.test.*;
import com.intellij.psi.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: jhe
 */
public class CreateTestMethodFixTest extends BaseTests {


    /**
     * @verifies create test method
     * @see CreateTestMethodFix#invoke(com.intellij.openapi.project.Project, com.intellij.openapi.editor.Editor, com.intellij.psi.PsiFile)
     */
    public void testInvoke_shouldCreateTestMethod() throws Exception {
        CreateTestMethodFix createTestMethodFix = buildCreateTestMethodFixIntention();

        createTestMethodFix.invoke();

        //  assert the test method has been created (exists())
        assertThat(createTestMethodFix.getTestMethod().reallyExists(), is(true));


    }

    private CreateTestMethodFix buildCreateTestMethodFixIntention() {
        //  create a test method
        PsiClass sutClass = createSutClass();
        TestClass result;
        try {
            result = BDDCore.createTestClass(sutClass);
        } catch (TestFrameworkNotConfigured testFrameworkNotConfigured) {
            throw new RuntimeException(testFrameworkNotConfigured);
        }
        TestClass testClass = result;
        TestMethod testMethod = testClass.getAllMethods().get(0);
        assertThat(testMethod.reallyExists(), is(false));

        //  create a method fix
        return new CreateTestMethodFix(testMethod);
    }


    /**
     * @verifies assert the name for the intention has the fqn for the test method
     * @see CreateTestMethodFix#getText()
     */
    public void testGetText_shouldAssertTheNameForTheIntentionHasTheFqnForTheTestMethod() throws Exception {
        //  create the fix
        CreateTestMethodFix createTestMethodFix = buildCreateTestMethodFixIntention();

        //  assert the name
        assertThat(createTestMethodFix.getText(), is("Create test method 'FooTest.testGetUser_shouldFetchUserWithGivenUserId()'"));

    }


}
