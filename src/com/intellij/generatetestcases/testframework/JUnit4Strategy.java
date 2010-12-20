package com.intellij.generatetestcases.testframework;

import com.intellij.codeInsight.intention.AddAnnotationFix;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiMethod;
import com.intellij.testIntegration.TestFrameworkDescriptor;
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


    /**
     * It returns the expected name for this method, it could make use
     * of an strategy for naming, investigate it further
     *
     * @return
     */
    @Override
    @NotNull
    protected String getExpectedNameForThisTestMethod(String sutMethodName, String description) {
        return BddUtil.generateTestMethodNameForJUNIT4(sutMethodName, description);
    }


    @Override
    public TestFrameworkDescriptor getTestFrameworkDescriptor() {
        return  BddUtil.findTestFrameworkDescriptorByName("JUnit4");
    }

    /**
     *
     * @param sutClass
     * @param sourceRoot
     * @return
     * @should create the right test class
     */
    @Override
    public PsiClass createBackingTestClass(PsiClass sutClass, PsiDirectory sourceRoot) {
        return super.createBackingTestClass(sutClass, sourceRoot);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isTestFrameworkLibraryAvailable(Module module) {
        return getTestFrameworkDescriptor().isLibraryAttached(module);
    }

    @Override
    protected void afterCreatingMethod(Project project, PsiMethod realTestMethod) {
        //  add the annotation to the method
        AddAnnotationFix fix = new AddAnnotationFix("org.junit.Test", realTestMethod);
        if (fix.isAvailable(project, null, realTestMethod.getContainingFile())) {
            fix.invoke(project, null, realTestMethod.getContainingFile());
        }
    }

    @Override
    protected String getFrameworkBasePackage() {
               String s = "org.junit";
        return s;
    }
}
