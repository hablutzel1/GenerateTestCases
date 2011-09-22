package com.intellij.generatetestcases.reference;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.refactoring.rename.*;
import org.jetbrains.annotations.*;

/**
 * Creado por: jaime
 * 22/09/11
 */
public class MyRenameProccessor extends RenameJavaMethodProcessor {


    @Override
    public RenameDialog createRenameDialog(Project project, PsiElement element, PsiElement nameSuggestionContext, Editor editor) {
    // TODO if  nameSuggestionContext  eq PsiDocToken:DOC_TAG_VALUE_TOKEN with @should tag
        // create custom rename Dialog

        // TODO else standard renaming



        return new RenameDialog(project, element, nameSuggestionContext, editor);
    }

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return super.canProcessElement(element); // able to renmae PsiMethods :D
    }


}
