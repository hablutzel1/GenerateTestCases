package com.intellij.generatetestcases.quickfix;

import com.intellij.codeInsight.intention.*;
import com.intellij.generatetestcases.*;
import com.intellij.generatetestcases.model.*;
import com.intellij.generatetestcases.testframework.*;
import com.intellij.openapi.diagnostic.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.util.*;
import org.jetbrains.annotations.*;

/**
 * User: jhe
 */
public class CreateTestMethodFix implements IntentionAction {

    private static final Logger LOG = Logger.getInstance("#com.intellij.generatetestcases.quickfix.CreateTestMethodFix");


    public TestMethod getTestMethod() {
        return testMethod;
    }

    private TestMethod testMethod;


    public CreateTestMethodFix(TestMethod testMethod) {
        this.testMethod = testMethod;
    }

    /**
     *
     * @return
     * @should assert the name for the intention has the fqn for the test method
     */
    @NotNull
    @Override
    public String getText() {

        TestMethodImpl tMethod = (TestMethodImpl) testMethod;
        TestFrameworkStrategy testFrameworkStrategy = tMethod.getTestFrameworkStrategy();
        String testMethodName = testFrameworkStrategy.getExpectedNameForThisTestMethod(testMethod.getSutMethod().getName(), testMethod.getDescription());
        TestClass parent = tMethod.getParent();
        String candidateClassName = testFrameworkStrategy.getCandidateTestClassName(parent.getClassUnderTest());
        return GenerateTestCasesBundle.message("plugin.GenerateTestCases.createtestmethod",
                candidateClassName + "." + testMethodName +"()");
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return GenerateTestCasesBundle.message("plugin.GenerateTestCases.bdd.family");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        // TODO check better under what conditions it should be unavailable
        // com.intellij.codeInsight.daemon.model.quickfix.SimplifyBooleanExpressionFix.isAvailable()
        return true;
    }


    /**
     *
     * @param project
     * @param editor
     * @param file
     * @throws IncorrectOperationException
     * @should jump to target test method in editor
     * @should create test method
     */
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        //  create test method
        invoke();
    }

    /**
     * It will provide undo capability
     *
     * @return
     * @see com.intellij.codeInsight.intention.IntentionAction#startInWriteAction()
     */
    @Override
    public boolean startInWriteAction() {

        return true;
    }

    public void invoke() {
        testMethod.create();
        testMethod.navigate();
    }


}
