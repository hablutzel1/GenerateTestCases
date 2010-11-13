package com.intellij.generatetestcases.impl;

import com.intellij.generatetestcases.TestClass;
import com.intellij.generatetestcases.TestMethod;
import com.intellij.generatetestcases.testframework.JUnit4Strategy;
import com.intellij.generatetestcases.testframework.TestFrameworkStrategy;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocTag;
import org.jetbrains.annotations.NotNull;

/**
 * User: Jaime Hablutzel
 */
public class TestMethodImpl implements TestMethod {
    private TestFrameworkStrategy testFrameworkStrategy;

    /**
     * TODO implement
     * State class for two possible states that TestMetho c
     */
    private class TestMethodState {

        private void created(TestMethod tm) {

        }

        private void notCreated(TestMethod tm) {

        }

    }

    // TODO create a strategy for creating test methods


    private PsiMethod sutMethod;

    private PsiDocTag shouldTag;

    private String description;

    private TestClass parent;

    private PsiMethod backingMethod;
    private Project project;
    private PsiElementFactory elementFactory;

    public TestMethodImpl(@NotNull PsiDocTag shouldTag, @NotNull TestClass parent) {

        // TODO instantiate an strategy
        testFrameworkStrategy = new JUnit4Strategy();

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
        if (parent.getBackingClass() != null) {
            this.backingMethod = testFrameworkStrategy.resolveBackingTestMethod(parent.getBackingClass(), sutMethod, description);
        }


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
        PsiMethod method1 = null;
        if (this.parent.getBackingClass() != null) {
            method1 = testFrameworkStrategy.resolveBackingTestMethod(this.parent.getBackingClass(), sutMethod, description);
        }
        PsiMethod method = method1;

        return (null != method) ? true : false;

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


        PsiMethod realTestMethod = testFrameworkStrategy.createBackingTestMethod(parent.getBackingClass(), sutMethod, description);
        this.backingMethod = realTestMethod;

        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);

        codeStyleManager.reformat(realTestMethod); // to reformat javadoc

    }

    private boolean existsInSut;


    public String getDescription() {
        return description;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PsiMethod getSutMethod() {
        return this.sutMethod;
    }


    public PsiDocTag getBackingTag() {
        return shouldTag;
    }

    public PsiMethod getBackingMethod() {
        PsiMethod method = null;
        if (this.parent.getBackingClass() != null) {
            method = testFrameworkStrategy.resolveBackingTestMethod(this.parent.getBackingClass(), sutMethod, description);
        }
        return method;
    }
}
