package com.intellij.generatetestcases.testframework;

import com.intellij.codeInsight.intention.AddAnnotationFix;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.testIntegration.TestFramework;
import org.jetbrains.annotations.NotNull;

/**
 * User: JHABLUTZEL
 * Date: 09/11/2010
 * Time: 03:06:09 PM
 */
public class JUnit4Strategy extends JUnitStrategyBase {

 

    public  JUnit4Strategy(Project project) {
        super(project);
    }


    @Override
    public TestFramework getTestFramework() {
        return  BddUtil.findTestFrameworkByName("JUnit4");
    }




//    @Override // add junit 4 Test annotation
//    protected void afterCreatingJunitMethod(Project project, PsiMethod realTestMethod) {
//
//    }

    @NotNull
    @Override
    public PsiMethod createBackingTestMethod(PsiClass testClass, PsiMethod sutMethod, String testDescription) {
        PsiMethod backingTestMethod = super.createBackingTestMethod(testClass, sutMethod, testDescription);

        //  add the annotation to the method
        AddAnnotationFix fix = new AddAnnotationFix("org.junit.Test", backingTestMethod);
        if (fix.isAvailable(sutMethod.getProject(), null, backingTestMethod.getContainingFile())) {
            fix.invoke(sutMethod.getProject(), null, backingTestMethod.getContainingFile());
        }
        return backingTestMethod;
    }

    @Override
    protected String getFrameworkBasePackage() {
        return "org.junit";
    }
}
