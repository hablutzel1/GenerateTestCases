package com.intellij.generatetestcases.inspection;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.deprecation.DeprecationInspection;
import com.intellij.codeInspection.ex.*;
import com.intellij.generatetestcases.test.TestUtil;
import com.intellij.psi.impl.source.jsp.jspJava.*;
import com.intellij.testFramework.InspectionTestCase;
import org.hamcrest.*;
import org.jmock.*;
import org.junit.Assert;
import org.junit.Test;


public class MissingTestMethodInspectionTest extends InspectionTestCase {

    private MissingTestMethodInspection tool = new MissingTestMethodInspection();

    @Override
    protected String getTestDataPath() {
        return TestUtil.getPluginHomePath() + "/testData/inspection";
    }


    /**
     * @verifies create problem for classes without backing class
     * @see MissingTestMethodInspection#checkClass(com.intellij.psi.PsiClass, com.intellij.codeInspection.InspectionManager, boolean)
     */
    public void testCheckClass_shouldCreateProblemForClassesWithoutBackingClass() throws Exception {
        doTest("missingTestMethod/class", tool);

    }


    /**
     * @verifies create problem for should annotations without test methods
     * @see MissingTestMethodInspection#checkClass(com.intellij.psi.PsiClass, com.intellij.codeInspection.InspectionManager, boolean)
     */
    public void testCheckClass_shouldCreateProblemForShouldAnnotationsWithoutTestMethods() throws Exception {

        doTest("missingTestMethod/method", tool);
    }

    /**
     * @verifies ignore unsupported psiClasses
     * @see MissingTestMethodInspection#checkClass(com.intellij.psi.PsiClass, com.intellij.codeInspection.InspectionManager, boolean)
     */
    public void testCheckClass_shouldIgnoreUnsupportedPsiClasses() throws Exception {
        Mockery context = new Mockery(); // should be instance variable
        final JspClass mock = context.mock(JspClass.class);
        context.checking(new Expectations()); // no expectations

        ProblemDescriptor[] problemDescriptors = tool.checkClass(mock, getManager(), true);
        assertNull(problemDescriptors);

    }
}
