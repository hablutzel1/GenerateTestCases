package com.intellij.generatetestcases.testframework;

import com.intellij.codeInsight.intention.AddAnnotationFix;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.testIntegration.TestFramework;
import org.jetbrains.annotations.NotNull;

/**
 * Creado por: jaime
 * 10/3/12
 */
public class TestNGStrategy extends AbstractTestFrameworkStrategy {


    public TestNGStrategy(Project project) {
        super(project);
    }

    @Override
    public TestFramework getTestFramework() {
        return BddUtil.findTestFrameworkByName("TestNG");
    }


    /**
     *
     * @param testClass
     * @param sutMethod
     * @param testDescription @return
     * @return
     * @should add org.testng.Assert import if it doesn't collide with another x.Assert import
     * @should add org.testng.Assert.fail("Not yet implemented"); statement to method under test
     */
    @NotNull
    @Override
    public PsiMethod createBackingTestMethod(@NotNull PsiClass testClass, @NotNull PsiMethod sutMethod, @NotNull String testDescription) {
        PsiMethod backingTestMethod = super.createBackingTestMethod(testClass, sutMethod, testDescription);


        // TODO add import for org.testng.Assert if it doesn't collides with another x.Assert import as in com.intellij.generatetestcases.testframework.JUnitStrategyBase.createBackingTestMethod()

        PsiJavaFile javaFile = (PsiJavaFile) testClass.getContainingFile();
        boolean assertImportExists = javaFile.getImportList().findSingleImportStatement("Assert") == null ? false : true;

        if (!assertImportExists){
            BddUtil.addImportToClass(sutMethod.getProject(), testClass, "org.testng.Assert");
        }

        // TODO check if this is org.testng.Assert, if it is: place unqualified statement, else qualify it
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(sutMethod.getProject());
        PsiStatement statement;
        if (javaFile.getImportList().findSingleClassImportStatement("org.testng.Assert") != null) {
            statement = elementFactory.createStatementFromText("Assert.fail(\"Not yet implemented\");", null);
        } else {
            statement = elementFactory.createStatementFromText("org.testng.Assert.fail(\"Not yet implemented\");", null);
        }

        //  add failed assert in testng terms



        backingTestMethod.getBody().addAfter(statement, backingTestMethod.getBody().getLastBodyElement());

        //  add the annotation to the method
        AddAnnotationFix fix = new AddAnnotationFix("org.testng.annotations.Test", backingTestMethod);
        if (fix.isAvailable(sutMethod.getProject(), null, backingTestMethod.getContainingFile())) {
            fix.invoke(sutMethod.getProject(), null, backingTestMethod.getContainingFile());
        }

        return backingTestMethod;
    }


}
