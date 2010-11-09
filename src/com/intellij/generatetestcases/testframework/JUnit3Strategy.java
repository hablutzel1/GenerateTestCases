package com.intellij.generatetestcases.testframework;

import com.intellij.psi.PsiMethod;

/**
 * User: JHABLUTZEL
 * Date: 09/11/2010
 * Time: 03:06:09 PM
 */
public class JUnit3Strategy implements TestFrameworkStrategy{


    @Override
    public PsiMethod createBackingTestMethod(PsiMethod sutMethod, String testDescription) {
        return null;
    }
    
}
