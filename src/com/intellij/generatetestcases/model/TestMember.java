package com.intellij.generatetestcases.model;

import com.intellij.generatetestcases.testframework.*;
import com.intellij.psi.*;

/**
 * User: jhe
 */
public interface TestMember {

    TestFrameworkStrategy getTestFrameworkStrategy();

    /**
     * This methods returns the PsiElement backing this TestMember
     *
     * @return
     * @should return the supporting PsiElement for this test member
     */
    PsiElement getBackingElement();

}
