package com.intellij.generatetestcases.testframework;

import com.intellij.codeInsight.intention.AddAnnotationFix;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

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

//        if (parent != null) {
        PsiMethod[] byNameMethods = parentBackingClass.findMethodsByName(nombreMetodoDePrueba, false);
        if (byNameMethods.length > 0) {
            backingMethod = byNameMethods[0];
        }


//        }
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

        //    codeStyleManagerEx.shortenClassReferences(element, JavaCodeStyleManager.UNCOMPLETE_CODE);

        realTestMethod.addBefore(psiComment, realTestMethod.getFirstChild());

        //  add junit 4 Test annotation

        //  verify org.junit.Test exists in classpath as an anntoation, if it doesn't throw exception
        String jUnit4TestAnnotation = "org.junit.Test";
        PsiClass junitTestAnnotation = JavaPsiFacade.getInstance(project).findClass(jUnit4TestAnnotation, GlobalSearchScope.allScope(project));
        if (junitTestAnnotation == null) {
            // TODO display alert, look for something similiar, not obstrusive :D
            throw new RuntimeException(jUnit4TestAnnotation + " haven't been found in classpath");
        } else {
            //  add the annotation to the method
            AddAnnotationFix fix = new AddAnnotationFix(jUnit4TestAnnotation, realTestMethod);
            if (fix.isAvailable(project, null, realTestMethod.getContainingFile())) {
                fix.invoke(project, null, realTestMethod.getContainingFile());
            }
        }

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

        // TODO verify import for Assert before actually importing

        // TODO verify if junit.framework.Assert exists, if it does do not import org.junit.Assert

        // TODO if Assert exists and is different to both of previous, place fully qualified statement

        PsiClass junitAssert = JavaPsiFacade.getInstance(project).findClass("org.junit.Assert", GlobalSearchScope.allScope(project));
        PsiImportStatement importStaticStatement = elementFactory.createImportStatement(junitAssert);
        PsiImportList list = ((PsiJavaFile) testClass.getContainingFile()).getImportList();
        list.add(importStaticStatement);

        // org.junit.Assert
        PsiStatement statement = elementFactory.createStatementFromText("Assert.fail(\"Not yet implemented\");", null);
        realTestMethod.getBody().addAfter(statement, todoComment);

        return realTestMethod;
    }

}
