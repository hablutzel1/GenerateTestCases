package com.intellij.generatetestcases.refactor.rename;

import com.intellij.generatetestcases.*;
import com.intellij.generatetestcases.model.*;
import com.intellij.generatetestcases.testframework.*;
import com.intellij.generatetestcases.util.*;
import com.intellij.generatetestcases.util.Constants;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.refactoring.rename.RenameDialog;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class ShouldTagRenameDialog extends RenameDialog {


    private PsiDocTag shouldDocTag;
    private final PsiElement testMethod;

    public ShouldTagRenameDialog(@NotNull Project project, @NotNull PsiElement psiElement, @Nullable PsiElement nameSuggestionContext, Editor editor, @NotNull PsiDocTag shouldDocTag) {
        super(project, psiElement, nameSuggestionContext, editor);
        this.shouldDocTag = shouldDocTag;
        this.testMethod = psiElement;
    }

    @Override
    protected boolean areButtonsValid() {
        //  should return true if there is actually something written
        return !StringUtils.isEmpty(getNewName());
    }

    private final Logger logger = Logger.getInstance(getClass());

    protected void doAction() {


        //  check if test method actually exists
        //  obtener clase actual
        PsiClass parentEligibleForTestingPsiClass = BddUtil.getParentEligibleForTestingPsiClass(shouldDocTag);

        TestClass testClass = null;
        try {

            testClass = BDDCore.createTestClass(parentEligibleForTestingPsiClass);
        } catch (TestFrameworkNotConfigured testFrameworkNotConfigured) {
            logger.warn("Test Framework isn't configured");
        }

        String newTestMethodName = null;

        //  renombrar metodo si existe si no solo renombrar descripcion
        if (testClass != null) {
            List<TestMethod> allMethods = testClass.getAllMethods();
            for (TestMethod allMethod : allMethods) {
                PsiDocTag backingTag = ((TestMethodImpl) allMethod).getBackingTag();
                if (backingTag.equals(shouldDocTag)) {
                    if (allMethod.reallyExists()) {
                        TestFrameworkStrategy testFrameworkStrategy = testClass.getTestFrameworkStrategy();

                        newTestMethodName = testFrameworkStrategy.getExpectedNameForThisTestMethod(allMethod.getSutMethod().getName(), getNewName());

                        break;
                    }
                }
            }
        }

        final Project project = shouldDocTag.getProject();


        final String finalNewTestMethodName = newTestMethodName;
        new WriteCommandAction(project, "Renaming Test Case @should tag") {

            @Override
            protected void run(Result result) throws Throwable {
                //  rename description
                changePsiDocTagContent(shouldDocTag, getNewName());

                if (finalNewTestMethodName != null) {
                    performRename(finalNewTestMethodName);

                    //  rename @verifies in test method
                    PsiDocComment docComment = ((PsiMethodImpl) testMethod).getDocComment();
                    PsiDocTag[] tags = docComment.getTags(); // TODO manage nullity with tests
                    for (PsiDocTag tag : tags) {
                        if (tag.getName().equals(Constants.VERIFIES_DOC_TAG)) {
                            String contents = getNewName();
                            changePsiDocTagContent(tag, contents);
                        }
                    }


                } else { // we have only renamed the tag
                    close(DialogWrapper.OK_EXIT_CODE);
                }
            }
        }.execute();

    }


    /**
     * It will change the contents of a PsiDocTag as this one
     * (at)foo content here --> (at)foo new content
     * <p/>
     * TODO move to test util
     *
     * @param tag         the tag its content is to be changed
     * @param newContents new contents
     */
    private void changePsiDocTagContent(PsiDocTag tag, String newContents) {
        PsiDocTag newVerifiesDocTag = JavaPsiFacade.getElementFactory(tag.getProject()).createDocTagFromText("@" + tag.getName() + " " + newContents);
        PsiDocComment containingComment = tag.getContainingComment();
        // FIXME it is adding a new line when the PsiDocTag proccesed is in the end of javadoc
        containingComment.addBefore(newVerifiesDocTag, tag);
        containingComment.deleteChildRange(tag, tag);

    }


    // it is  called from parent constructor so we are using a nameSuggestionProvider
//    @Override
//    public String[] getSuggestedNames() {
//
//        String shouldTagDescription = BddUtil.getShouldTagDescription(shouldDocTag);
//        return new String[] {shouldTagDescription};
//    }

    ////////// when renamiin


}
