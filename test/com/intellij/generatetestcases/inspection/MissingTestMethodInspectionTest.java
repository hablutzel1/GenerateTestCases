package com.intellij.generatetestcases.inspection;

import com.intellij.codeInspection.deprecation.DeprecationInspection;
import com.intellij.generatetestcases.test.TestUtil;
import com.intellij.testFramework.InspectionTestCase;
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
        //crear pruebas de inspection para cada uno de estos escenarios 
        fail("Ensure ony the text for @should annotations is selected");
    }
}
