package com.intellij.generatetestcases.reference;

import com.intellij.generatetestcases.util.Constants;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;


public class ShouldReferenceProvider extends PsiReferenceProvider {

    @Override
    public boolean acceptsTarget(@NotNull PsiElement target) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {


        // TODO it doesn't get called as often as the methods in ShouldReference so it would be convenient if some static
        // information could be passed to it or created in its constructor

        // TODO it should only create references for @should PsiDocTags

        PsiDocTag psiDocTag = (PsiDocTag) element;
        if (psiDocTag.getName().equals(Constants.BDD_TAG)) {
            ShouldReference shouldReference = new ShouldReference(psiDocTag);
            return new PsiReference[]{shouldReference};
        } else {
            return new PsiReference[0];

        }

    }
}
