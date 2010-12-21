package com.intellij.generatetestcases.testframework;

import com.intellij.generatetestcases.test.BaseTests;
import com.intellij.psi.*;
import junit.framework.Assert;
import junit.framework.TestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class TestFrameworkStrategyTest extends BaseTests {

    @Override
    protected void setUp() throws Exception {

        testFrameworkStrategy = new JUnit3Strategy(myProject);

        super.setUp();
    }

    TestFrameworkStrategy testFrameworkStrategy;

    /**
     * @verifies ignore anonymous classes
     * @see TestFrameworkStrategy#findBackingPsiClass(com.intellij.psi.PsiClass)
     */
    public void testFindBackingPsiClass_shouldIgnoreAnonymousClasses() throws Exception {
        //  create or get anonymous class
        PsiNewExpression expression = (PsiNewExpression) JavaPsiFacade.getElementFactory(myProject).createExpressionFromText("new Object() { }", myFile);
        PsiAnonymousClass psiAnonymousClass = expression.getAnonymousClass();

        //  invoke method
        PsiClass psiClass = testFrameworkStrategy.findBackingPsiClass(psiAnonymousClass);

        //  assert null return
        assertThat(psiClass, is(nullValue()));


    }


}
