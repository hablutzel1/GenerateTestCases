package com.intellij.generatetestcases.refactor.listener;

import com.intellij.generatetestcases.*;
import com.intellij.generatetestcases.testframework.*;
import com.intellij.generatetestcases.util.*;
import com.intellij.openapi.application.*;
import com.intellij.psi.*;
import com.intellij.refactoring.listeners.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * // TODO move this logic to TestMethod
 * Creado por: jaime
 * 7/10/11
 */
class MyRefactoringElementListener implements RefactoringElementListener {

    private final ArrayList<TestMethod> methodsToRefactor;
    private final TestClass testClass;
    private final String oldMethodName;

    public MyRefactoringElementListener(ArrayList<TestMethod> methodsToRefactor, TestClass testClass, String oldMethodName) {
        this.methodsToRefactor = methodsToRefactor;
        this.testClass = testClass;
        this.oldMethodName = oldMethodName;
    }

    @Override
    public void elementMoved(@NotNull PsiElement newElement) {

        // TODO get test framework

        // TODO get target class

        //BddUtil.getParentEligibleForTestingPsiClass(newElement) new target
        // do nothing
    }

    /**
     * @param newElement
     */
    @Override
    public void elementRenamed(@NotNull final PsiElement newElement) {
        for (final TestMethod testMethod : methodsToRefactor) {

            //  update method name
            final JUnitStrategyBase testFrameworkStrategy = (JUnitStrategyBase) testClass.getTestFrameworkStrategy();

            // get method with name expectedNameForThisTestMethod and rename it directly
            PsiClass psiTestClass = testClass.getBackingElement();
            PsiMethod[] methodsByName = psiTestClass.findMethodsByName(testFrameworkStrategy.getExpectedNameForThisTestMethod(oldMethodName, testMethod.getDescription()), false);

            for (final PsiMethod psiMethod : methodsByName) {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        psiMethod.setName(testFrameworkStrategy.getExpectedNameForThisTestMethod(((PsiMethod) newElement).getName(), testMethod.getDescription()));
                    }
                });
            }


        }
    }
}
