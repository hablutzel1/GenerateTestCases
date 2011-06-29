package com.intellij.generatetestcases.ui.codeinsight;

import com.intellij.codeInsight.navigation.actions.*;
import com.intellij.generatetestcases.*;
import com.intellij.generatetestcases.impl.*;
import com.intellij.generatetestcases.util.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.*;
import org.apache.commons.lang.*;

import java.util.*;

/**
 *
 * // TODO it would work only for navigating fronm the @should because
 * it can underline only PsiElements and the full description is composed of more than one
 * PsiElement.
 *
 * <pre>
 *   **
 *    * @should (sadgasd) (a asldgj a alskdjg asdlga)
 *    * (asdg)
 *    * (asdg)
 *    *
 *    *
 *   void foo() {
 *   }
 * </pre>
 * User: jhe
 */
public class GotoTestMethod implements GotoDeclarationHandler{

    /**
     *
     * @param sourceElement
     * @return
     * @should return the backing test method for a (at)should tag
     * @should return null if the (at)should tag doesn't have a corresponding created test method
     * @should return null if the incoming element is whatever but a should tag
     */
    @Override
    public PsiElement getGotoDeclarationTarget(PsiElement sourceElement) {

        //  filter for @should tags only
        if (!(sourceElement instanceof PsiDocToken)
                || !((PsiDocToken) sourceElement).getTokenType().toString().equals("DOC_TAG_NAME")) {
            return null;
        }

        PsiDocToken shouldToken = (PsiDocToken) sourceElement;

        // TODO create a test class, determine test method (if exists) for the current @should
        Project project = shouldToken.getProject();
        String testFramework;
        if (!ApplicationManager.getApplication().isUnitTestMode()) {

            testFramework = GenerateTestCasesSettings.getInstance(project).getTestFramework();
            if (StringUtils.isEmpty(testFramework)) {
                return null;
            }
        } else {
            testFramework = Constants.DEF_TEST_FRAMEWORK;
        }

        PsiElement parentPsiClass = sourceElement;

        do {
            parentPsiClass = parentPsiClass.getParent();
        } while(!(parentPsiClass instanceof PsiClass)) ;

        //  create TestClass for current class
        TestClass testClass = BDDCore.createTestClass(project, (PsiClass) parentPsiClass, BddUtil.getStrategyForFramework(project, testFramework));

        //  return the backing test method psiElement
        List<TestMethod> allMethods = testClass.getAllMethods();
        for (TestMethod testMethod : allMethods) {
            if (testMethod.reallyExists()) {
                PsiElement nameElement = ((TestMethodImpl) testMethod).getBackingTag().getNameElement();
                if (nameElement.equals(shouldToken)) {
                      return testMethod.getBackingElement();
                }
            }
        }
        return null;

    }

}
