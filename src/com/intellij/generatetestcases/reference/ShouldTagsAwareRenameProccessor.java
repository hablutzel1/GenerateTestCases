package com.intellij.generatetestcases.reference;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.generatetestcases.reference.rename.ShouldTagRenameDialog;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.SharedPsiElementImplUtil;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.refactoring.rename.*;
import org.jetbrains.annotations.*;

/**
 * This {@link RenamePsiElementProcessor} will provide java methods renaming support
 * for any java method it will provide standard renaming, but for methods being renamed from a @should tag it will provide renaming of the test method from the @should tag description
 *
 *
 * Creado por: jaime
 * 22/09/11
 */
public class ShouldTagsAwareRenameProccessor extends RenameJavaMethodProcessor {


    /**
     * its nameSuggestionContext isn't using the same logic than the reference provider to find the element to rename from the reference, it is passing a whitespace when the cursor is after (at)should tag, so we should use the same logic than the reference provider to know where are we located in the editor.
     *
     * @should create ShouldTagRenameDialog instead of RenameDialog when element is a TestMethod got from a (at)should tag reference
     */
    @Override
    public RenameDialog createRenameDialog(Project project, PsiElement element, PsiElement nameSuggestionContext, Editor editor) {
        // nameSuggestionContext isn't precise

        PsiReference shouldRef =  TargetElementUtilBase.findReference(editor);

        // create custom rename Dialog
        if (shouldRef instanceof ShouldReference) { // we are coming from a javadoc
            return new ShouldTagRenameDialog(project, element, nameSuggestionContext, editor, (PsiDocTag) shouldRef.getElement());

        } else {             //  else standard renaming
            return new RenameDialog(project, element, nameSuggestionContext, editor);
        }
    }

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return super.canProcessElement(element); // able to renmae PsiMethods :D
    }


}
