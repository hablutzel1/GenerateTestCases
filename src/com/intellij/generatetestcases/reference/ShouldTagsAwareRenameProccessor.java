package com.intellij.generatetestcases.reference;

import com.intellij.generatetestcases.reference.rename.ShouldTagRenameDialog;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
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


    @Override
    public RenameDialog createRenameDialog(Project project, PsiElement element, PsiElement nameSuggestionContext, Editor editor) {

        //  if nameSuggestionContext  eq PsiDocToken:DOC_TAG_VALUE_TOKEN with @should tag
        PsiDocTag shouldDocTag = BddUtil.getPsiDocTagParent(nameSuggestionContext);

        // create custom rename Dialog
        if (shouldDocTag != null) {
            return new ShouldTagRenameDialog(project, element, nameSuggestionContext, editor, shouldDocTag);

        } else {             //  else standard renaming
            return new RenameDialog(project, element, nameSuggestionContext, editor);
        }
    }

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return super.canProcessElement(element); // able to renmae PsiMethods :D
    }


}
