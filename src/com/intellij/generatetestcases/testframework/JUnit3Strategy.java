package com.intellij.generatetestcases.testframework;

import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testIntegration.TestFrameworkDescriptor;
import org.eclipse.jdt.internal.core.search.JavaSearchScope;
import org.jetbrains.annotations.NotNull;

/**
 * User: JHABLUTZEL
 * Date: 09/11/2010
 * Time: 03:06:09 PM
 * <p/>
 * import junit.framework.Test and Assert in the same package
 * make class to extends junit.framework.TestCase
 */
public class JUnit3Strategy extends JUnitStrategyBase {
    
    public JUnit3Strategy(Project project) {
        super(project);
    }

    @NotNull
    @Override
    protected String getExpectedNameForThisTestMethod(String sutMethodName, String description) {
        return BddUtil.generateJUNIT3MethodName(sutMethodName, description);
    }

    @Override
    public TestFrameworkDescriptor getTestFrameworkDescriptor() {
        return BddUtil.findTestFrameworkDescriptorByName("JUnit3");
    }


    public boolean isTestFrameworkLibraryAvailable(Module module) {
        return getTestFrameworkDescriptor().isLibraryAttached(module);
    }

//    @Override
//    protected void afterCreatingMethod(Project project, PsiMethod realTestMethod) {
//
//
//    }

    @Override
    protected String getFrameworkBasePackage() {
        String s = "junit.framework";
        return s;
    }


    /**
     * WARNING: This method has been overrided just to test implementation details for a specific framework
     *
     * @param testClass
     * @param sutMethod
     * @param testDescription @return
     * @return
     * @should create a method and imports with the right junit 3 structure
     */
    @Override
    public PsiMethod createBackingTestMethod(PsiClass testClass, PsiMethod sutMethod, String testDescription) {
        return super.createBackingTestMethod(testClass, sutMethod, testDescription);    //To change body of overridden methods use File | Settings | File Templates.
    }


    /**
     * @param sutClass
     * @param sourceRoot
     * @return
     * @should create a test class that extends TestCase
     */
    @Override
    public PsiClass createBackingTestClass(PsiClass sutClass, PsiDirectory sourceRoot) {
        return super.createBackingTestClass(sutClass, sourceRoot);
    }

    @Override
    protected void afterCreatingClass(Project project, PsiClass backingTestClass) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        PsiJavaCodeReferenceElement referenceElementByFQClassName = elementFactory.createReferenceElementByFQClassName("junit.framework.TestCase", GlobalSearchScope.allScope(project));
        backingTestClass.getExtendsList().add(referenceElementByFQClassName);
    }
}
