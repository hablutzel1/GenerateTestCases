package com.intellij.generatetestcases.inspection;

import com.intellij.testFramework.InspectionTestCase;
import org.junit.Assert;
import org.junit.Test;


public class MissingTestMethodInspectionTest extends InspectionTestCase {
    /**
     * @verifies create problem for classes without backing class
     * @see MissingTestMethodInspection#checkClass(com.intellij.psi.PsiClass, com.intellij.codeInspection.InspectionManager, boolean)
     */
    @Test
    public void testCheckClass_shouldCreateProblemForClassesWithoutBackingClass() throws Exception {
        //TODO auto-generated
        Assert.fail("Not yet implemented");
    }

    /**
     * @verifies create problem for should annotations without test methods
     * @see MissingTestMethodInspection#checkClass(com.intellij.psi.PsiClass, com.intellij.codeInspection.InspectionManager, boolean)
     */
    public void testCheckClass_shouldCreateProblemForShouldAnnotationsWithoutTestMethods() throws Exception {
        //TODO auto-generated
        Assert.fail("Not yet implemented");
    }
}
