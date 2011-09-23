package com.intellij.generatetestcases.impl;

import com.intellij.generatetestcases.TestMethod;
import com.intellij.generatetestcases.testframework.TestFrameworkStrategy;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.jsp.jspJava.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.generatetestcases.TestClass;

import java.util.ArrayList;
import java.util.List;


/**
 * User: JHABLUTZEL
 * Date: 29/10/2010
 * Time: 08:57:26 AM
 */
public class TestClassImpl implements TestClass {

    List<TestMethod> testMethods;
    private PsiClass sutClass;
    private static final String TEST_CLASS_SUFFIX = "Test";

//    public TestFrameworkStrategy getFrameworkStrategy() {
//        return frameworkStrategy;
//    }

    //    private Project project;
    private TestFrameworkStrategy frameworkStrategy;

    public TestClassImpl(PsiClass psiClass, TestFrameworkStrategy frameworkStrategy) {

        //  check for unsupported classes
        if (psiClass instanceof JspClass) {
            throw new IllegalArgumentException("Unsupported class: " + psiClass.getClass().getName());
        }

        //  popular TestClass con TestMethods
        this.sutClass = psiClass;

        this.frameworkStrategy = frameworkStrategy;

        // init a reference to the current project
//        project = sutClass.getProject();


        findAndInitializeAllTestMethods(psiClass);


    }

    /**
     * Initializes all test methods from psiClass
     *
     * @param psiClass
     */
    private void findAndInitializeAllTestMethods(PsiClass psiClass) {
        //   iterar sobre los metodos de la clase
        PsiMethod[] methods = psiClass.getMethods();
        ArrayList<TestMethod> array = new ArrayList<TestMethod>();
        //  iterar sobre los metodos
        for (PsiMethod method : methods) {


            //  iterar sobre los comentarios del javadoc
            PsiDocComment comment = method.getDocComment();
            if (comment == null) { // if no doc comment
                continue;
            }
            PsiDocTag[] tags = comment.getTags();
            //   iterar sobre los comentarios del javadoc
            for (PsiDocTag tag : tags) {


                //  comprobar que el tag sea del tipo should
                if (BddUtil.isValidShouldTag(tag)) {
                    // TODO change it to receive a string instead of the tag, or to test if the tag
                    // have empty description
                    TestMethod tm = new TestMethodImpl(tag, this, this.frameworkStrategy);
                    array.add(tm);
                }
            }
        }

        this.testMethods = array;
    }

    public List<TestMethod> getAllMethods() {
        return testMethods;
    }

    public void create(PsiDirectory sourceRoot) {

        frameworkStrategy.createBackingTestClass(sutClass, sourceRoot);

    }


    public boolean reallyExists() {
        PsiClass psiClass = frameworkStrategy.findBackingPsiClass(sutClass);

        if (psiClass != null) {
            return true;
        } else {
            return false;
        }

    }

    public PsiClass getBackingElement() {
        return frameworkStrategy.findBackingPsiClass(sutClass);
    }


    public PsiClass getClassUnderTest() {
        return this.sutClass;
    }

    @Override
    public TestFrameworkStrategy getTestFrameworkStrategy() {
        return frameworkStrategy;
    }
}
