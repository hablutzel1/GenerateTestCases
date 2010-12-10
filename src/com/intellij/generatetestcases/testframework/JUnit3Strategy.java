package com.intellij.generatetestcases.testframework;

import com.intellij.codeInsight.intention.AddAnnotationFix;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

/**
 * User: JHABLUTZEL
 * Date: 09/11/2010
 * Time: 03:06:09 PM
 */
public class JUnit3Strategy extends JUnitStrategyBase {

    @NotNull
    @Override
    protected String getExpectedNameForThisTestMethod(String sutMethodName, String description) {
        return BddUtil.generateJUNIT3MethodName(sutMethodName, description);
    }
    
}
