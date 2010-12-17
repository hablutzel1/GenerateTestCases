package com.intellij.generatetestcases.testframework;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.daemon.impl.quickfix.OrderEntryFix;
import com.intellij.codeInsight.intention.AddAnnotationFix;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Jaime Hablutzel
 */
public abstract class JUnitStrategyBase implements TestFrameworkStrategy {

    private PsiElementFactory elementFactory;


    @Override
    public PsiMethod findBackingTestMethod(PsiClass testClass, PsiMethod sutMethod, String testDescription) {
        //  resolve (find) backing test method in test class
        String nombreMetodoDePrueba = getExpectedNameForThisTestMethod(sutMethod.getName(), testDescription);
        PsiClass parentBackingClass = testClass;
        PsiMethod backingMethod = null;
//        PsiClass parent = testClass.getContainingClass();
        // TODO get the test class


        PsiMethod[] byNameMethods = parentBackingClass.findMethodsByName(nombreMetodoDePrueba, false);
        if (byNameMethods.length > 0) {
            backingMethod = byNameMethods[0];
        }



        return backingMethod;
    }

    @NotNull
    protected abstract String getExpectedNameForThisTestMethod(@NotNull String sutMethodName, @NotNull String description);


    /**
     * @param testClass
     * @param sutMethod
     * @param testDescription @return
     * @return
     * @should create a junit test method with the expected body and javadoc and verify class structure
     * @should manage appropiately existence of multiple Assert's imports
     * @should manage appropiately any condition of the backing test class (imports, existing methods, modifiers, etc)
     * @should create test method even with broken references if test libraries aren't available
     */
    @Override
    public PsiMethod createBackingTestMethod(PsiClass testClass, PsiMethod sutMethod, String testDescription) {

        Project project = sutMethod.getProject();
        elementFactory = JavaPsiFacade.getElementFactory(project);
        //  get test method name
        PsiMethod factoriedTestMethod = elementFactory.createMethod(getExpectedNameForThisTestMethod(sutMethod.getName(), testDescription), PsiType.VOID);

        //  correr esto dentro de un write-action   ( Write access is allowed inside write-action only )
        testClass.add(factoriedTestMethod);
        PsiMethod realTestMethod = testClass.findMethodBySignature(factoriedTestMethod, false);


        //  get sut method name and signature
        // use fqn#methodName(ParamType)
        String methodQualifiedName;

        PsiClass aClass = sutMethod.getContainingClass();
        String className = aClass == null ? "" : aClass.getQualifiedName();
        methodQualifiedName = className == null ? "" : className;
        if (methodQualifiedName.length() != 0) methodQualifiedName += "#";
        methodQualifiedName += sutMethod.getName() + "(";
        PsiParameter[] parameters = sutMethod.getParameterList().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            PsiParameter parameter = parameters[i];
            if (i != 0) methodQualifiedName += ", ";
            methodQualifiedName += parameter.getType().getCanonicalText();
        }
        methodQualifiedName += ")";

        //  replace <.*> by blanks, there is a better way :S
        methodQualifiedName = methodQualifiedName.replaceAll("<.*?>", "");


        //  get test method description

        String commentText = "/**\n" +

                "* @verifies " + testDescription + "\n" +
                "*/";

        PsiDocTag docTag = elementFactory.createDocTagFromText("@see  " + methodQualifiedName, null);

        PsiComment psiComment = elementFactory.createCommentFromText(commentText, null);
        psiComment.add(docTag);

        final JavaCodeStyleManager codeStyleManagerEx = JavaCodeStyleManager.getInstance(project);


        realTestMethod.addBefore(psiComment, realTestMethod.getFirstChild());
        //  add junit 4 Test annotation
        //  verify org.junit.Test exists in classpath as an anntoation, if it doesn't throw exception
        

//        PsiClass junitTestAnnotation = JavaPsiFacade.getInstance(project).findClass(jUnit4TestAnnotation, GlobalSearchScope.allScope(project));

        //  get current module
        Module module = ModuleUtil.findModuleForPsiElement(sutMethod);

        //  test if framework is available in project
//        boolean isLibraryAvailable = getTestFrameworkDescriptor().isLibraryAttached(module);

        //  TODO if it isn't display dialog allowing the user to add library to the project
//        if (!isLibraryAvailable){

            
            // TODO display alert, look for something similiar, not obstrusive :D
//        }


//        if (junitTestAnnotation == null) {

//             myFixLibraryButton = new JButton(CodeInsightBundle.message("intention.create.test.dialog.fix.library"));
//    myFixLibraryButton.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        ApplicationManager.getApplication().runWriteAction(new Runnable() {
//          public void run() {
//            OrderEntryFix.addJarToRoots(mySelectedTestDescriptor.getLibraryPath(), myTargetModule, null);
//          }
//        });
//        myFixLibraryPanel.setVisible(false);
//      }
//    });
//            getTestFrameworkDescriptor().get



//            throw new RuntimeException(jUnit4TestAnnotation + " haven't been found in classpath");
//        } else {
          String jUnit4TestAnnotation = "org.junit.Test";

            //  add the annotation to the method
            AddAnnotationFix fix = new AddAnnotationFix(jUnit4TestAnnotation, realTestMethod);
            if (fix.isAvailable(project, null, realTestMethod.getContainingFile())) {
                fix.invoke(project, null, realTestMethod.getContainingFile());
            }
//        }
//
        //  add throws Exception

//        PsiClass javaLangException = JavaPsiFacade.getInstance(project).findClass("java.lang.Exception", GlobalSearchScope.allScope(project));

        PsiClassType fqExceptionName = JavaPsiFacade.getInstance(project)
                .getElementFactory().createTypeByFQClassName(
                        CommonClassNames.JAVA_LANG_EXCEPTION, GlobalSearchScope.allScope(project));

        PsiClass exceptionClass = fqExceptionName.resolve();
        if (exceptionClass != null) {
            PsiUtil.addException(realTestMethod, exceptionClass);
        }

        //  add //TODO auto-generated comment in the body
        PsiComment fromText = elementFactory.createCommentFromText("//TODO auto-generated", null);
        PsiElement todoComment = realTestMethod.getBody().addBefore(fromText, null);

        //  add org.junit.Assert.fail("Not yet implemented");,

        PsiJavaFile javaFile = (PsiJavaFile) testClass.getContainingFile();

        boolean assertImportExists = javaFile.getImportList().findSingleImportStatement("Assert") == null ? false: true;
        boolean makeFullQualified = false;

        // TODO if Assert exists and is different to both of previous, place fully qualified statement
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
            //  create basic org.junit.Assert
            addBasicImport(testClass, project);
        }


        // org.junit.Assert
        PsiStatement statement;

        if (makeFullQualified) {
            statement = elementFactory.createStatementFromText("org.junit.Assert.fail(\"Not yet implemented\");", null);
        } else {
            statement = elementFactory.createStatementFromText("Assert.fail(\"Not yet implemented\");", null);
        }

        realTestMethod.getBody().addAfter(statement, todoComment);

        return realTestMethod;
    }

    private void addBasicImport(PsiClass testClass, Project project) {
        PsiClass junitAssert = JavaPsiFacade.getInstance(project).findClass("org.junit.Assert", GlobalSearchScope.allScope(project));
        PsiImportStatement importStaticStatement = elementFactory.createImportStatement(junitAssert);
        PsiImportList list = ((PsiJavaFile) testClass.getContainingFile()).getImportList();
        list.add(importStaticStatement);
    }

}
