package com.intellij.generatetestcases.refactor.listener;

import com.intellij.generatetestcases.*;
import com.intellij.generatetestcases.util.*;
import com.intellij.psi.*;
import com.intellij.refactoring.listeners.*;

import java.util.*;

/**
 * Creado por: jaime
 * 7/10/11
 */
public class TestMethodRefactoringProvider implements RefactoringElementListenerProvider {


    @Override
    public RefactoringElementListener getListener(final PsiElement element) {
        if (element instanceof PsiMethod) {


            final ArrayList<TestMethod> testMethodsToRefactor = new ArrayList<TestMethod>();
            final String oldSutMethodName = ((PsiMethod) element).getName();
            final TestClass testClass;


            try {
                testClass = BDDCore.createTestClass(BddUtil.getParentEligibleForTestingPsiClass(element));
                List<TestMethod> allMethods = testClass.getAllMethods();
                for (TestMethod allMethod : allMethods) {
                    if (allMethod.getSutMethod().equals(element) && allMethod.reallyExists()) {
                        testMethodsToRefactor.add(allMethod);
                    }
                }
            } catch (TestFrameworkNotConfigured testFrameworkNotConfigured) {

                // TODO log it
                return null;
            }

            // verify it is a test method
            return new MyRefactoringElementListener(testMethodsToRefactor, testClass, oldSutMethodName);

        } else {

            return null;
        }

    }

}
