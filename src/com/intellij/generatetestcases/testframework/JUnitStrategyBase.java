package com.intellij.generatetestcases.testframework;

import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * User: Jaime Hablutzel
 */
public abstract class JUnitStrategyBase extends AbstractTestFrameworkStrategy {

//    private

    protected JUnitStrategyBase(Project project) {
        super(project);
        this.project = project;
    }

    private Project project;

    /**
     * This method completes the test method structure returned by {@link AbstractTestFrameworkStrategy#createBackingTestMethod(com.intellij.psi.PsiClass, com.intellij.psi.PsiMethod, java.lang.String)} in the way JUNIT 3 and 4 expect.
     *
     * @param testClass
     * @param sutMethod
     * @param testDescription @return
     * @return
     * @should manage appropiately existence of multiple junit Assert's imports across junit versions
     * @should manage appropiately any condition of the backing test class (imports, existing methods, modifiers, etc)
     * @should add Assert.fail("Not yet implemented") statement to method body
     */
    @NotNull
    @Override
    public PsiMethod createBackingTestMethod(PsiClass testClass, PsiMethod sutMethod, String testDescription) {

        PsiMethod realTestMethod = super.createBackingTestMethod(testClass, sutMethod, testDescription);

        //  add org.junit.Assert.fail("Not yet implemented");,
        PsiJavaFile javaFile = (PsiJavaFile) testClass.getContainingFile();

        boolean assertImportExists = javaFile.getImportList().findSingleImportStatement("Assert") == null ? false : true;
        boolean makeFullQualified = false;

        //  if Assert exists and is different to both of previous, place fully qualified statement
        if (assertImportExists) {

            //  verify if junit.framework.Assert exists, if it does do not import org.junit.Assert
            //  verify import for Assert before actually importing


            //  replace it by ((PsiJavaFile) testClass.getContainingFile()).getImportList()
            PsiImportStatement bei = javaFile.getImportList().findSingleClassImportStatement("org.junit.Assert");
//            List<PsiImportStatementBase> basicExpectedImport = BddUtil.findImportsInClass(testClass, );

            PsiImportStatement oei = javaFile.getImportList().findSingleClassImportStatement("junit.framework.Assert");
//            List<PsiImportStatementBase> otherExpectedImport = BddUtil.findImportsInClass(testClass, "");

            if (bei == null && oei == null) {
                // then it is a weird class
                makeFullQualified = true;
            }


        } else {
            //  create basic import
            BddUtil.addImportToClass(sutMethod.getProject(), testClass, getFrameworkBasePackage() + ".Assert");
        }


        // org.junit.Assert
        PsiElementFactory elementFactory2 = JavaPsiFacade.getElementFactory(sutMethod.getProject());

        PsiStatement statement;
        if (makeFullQualified) {
            statement = elementFactory2.createStatementFromText(getFrameworkBasePackage() + ".Assert.fail(\"Not yet implemented\");", null);
        } else {
            statement = elementFactory2.createStatementFromText("Assert.fail(\"Not yet implemented\");", null);
        }

        realTestMethod.getBody().addAfter(statement, realTestMethod.getBody().getLastBodyElement());

        return realTestMethod;
    }

    protected abstract String getFrameworkBasePackage();


}
