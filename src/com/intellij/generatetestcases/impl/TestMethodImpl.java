package com.intellij.generatetestcases.impl;

import com.intellij.generatetestcases.TestClass;
import com.intellij.generatetestcases.TestMethod;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import org.jetbrains.annotations.NotNull;

/**
 * User: Jaime Hablutzel
 */
public class TestMethodImpl implements TestMethod {


    private PsiMethod sutMethod;

    private PsiDocTag shouldTag;

    private String description;

    private TestClass parent;

    private PsiMethod backingMethod;
    private Project project;
    private PsiElementFactory elementFactory;

    public TestMethodImpl(@NotNull PsiDocTag shouldTag, @NotNull TestClass parent) {
        this.shouldTag = shouldTag;
        this.project = shouldTag.getProject();

        elementFactory = JavaPsiFacade.getElementFactory(project);


        //  obtener el metodo a partir del docTag
        resolveSutMethod(shouldTag);
        //  initialize the description
        initShouldTagDescription(shouldTag);

        //  bind the current test parent...
        // TODO get this using the shouldTag, or investigate it better
        // TO get the TestClass parent from here without passing it through the constructor
        // it would be needed to implement a registry where we could look for instances for
        // some determined class to guarantee that uniqueness of parents for test methods
        //this.parent = ((PsiMethod)shouldTag.getParent().getParent()).getContainingClass();
        this.parent = parent;
        this.backingMethod = resolveBackingMethod(parent);


    }

    private PsiMethod resolveBackingMethod(TestClass parent) {
        //  resolve (find) backing test method in test class
        String nombreMetodoDePrueba = getExpectedNameForThisTestMethod();
        PsiClass parentBackingClass = null;
        PsiMethod backingMethod = null;
        if (parent != null && null != (parentBackingClass = parent.getBackingClass())) {
            PsiMethod[] byNameMethods = parentBackingClass.findMethodsByName(nombreMetodoDePrueba, false);
            if (byNameMethods.length > 0) {
                backingMethod = byNameMethods[0];
            }
        }
        return backingMethod;
    }

    /**
     * It returns the expected name for this method, it could make use
     * of an strategy for naming, investigate it further
     *
     * @return
     */
    @NotNull
    private String getExpectedNameForThisTestMethod() {
        return BddUtil.generateTestMethodName(sutMethod.getName(), description);
    }


    private void resolveSutMethod(PsiDocTag shouldTag) {
        PsiMethod method = (PsiMethod) shouldTag.getParent().getContext();
        this.sutMethod = method;
    }

    private void initShouldTagDescription(PsiDocTag shouldTag) {
        final StringBuilder description = new StringBuilder();

        PsiElement[] dataElements = shouldTag.getDataElements();
        boolean isFirst = true;
        for (PsiElement dataElement : dataElements) {
            description.append(dataElement.getText());
            // TODO get the description taking into account the whitespaces
            if (isFirst) {
                description.append(" ");
            }
            isFirst = false;
        }

        this.description = description.toString().trim();
    }

    public boolean reallyExists() {
        return (null != resolveBackingMethod(parent)) ? true : false;

    }

    public void create() {


        if (parent == null) {
            // TODO need to look for the parent psi test class in some other way
            // TODO create a stub for the parent or look in registry
            // TODO log it 
        } else if (parent != null && !parent.reallyExists()) {
            //  if parent doesn't exist, create it
            parent.create(null);

        }

        //  if it exists, just create this method
        PsiClass psiClass = parent.getBackingClass();
        //  get test method name


        PsiMethod factoriedTestMethod = elementFactory.createMethod(getExpectedNameForThisTestMethod(), PsiType.VOID);

        //  correr esto dentro de un write-action   ( Write access is allowed inside write-action only )
        psiClass.add(factoriedTestMethod);
        PsiMethod realTestMethod = psiClass.findMethodBySignature(factoriedTestMethod, false);

        // TODO get sut class name
        String sutClassName = parent.getClassUnderTest().getName();

        // TODO get sut method name and signature



        // TODO get test method description

        String commentText = "/**\n" +
                "* @see FooBar#zas()\n" +
                "* @verifies do nothing\n" +
                "*/";

        PsiComment psiComment = elementFactory.createCommentFromText(commentText, null);

        // TODO referesh psiClass because a test cliente cannot have access to test method javadoc
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
        realTestMethod.addBefore(psiComment, realTestMethod.getFirstChild());
        codeStyleManager.reformat(realTestMethod); // to reformat javadoc
    }

    private boolean existsInSut;


    public String getDescription() {
        return description;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PsiMethod getSutMethod() {
        return this.sutMethod;
    }

    public PsiMethod getBackingMethod() {
        return resolveBackingMethod(parent);
    }
}
