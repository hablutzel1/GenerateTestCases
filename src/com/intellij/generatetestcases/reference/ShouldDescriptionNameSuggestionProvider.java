package com.intellij.generatetestcases.reference;

import com.intellij.generatetestcases.reference.rename.ShouldTagRenameDialog;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.SuggestedNameInfo;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.refactoring.rename.NameSuggestionProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Created by jhe
 * Time: 0:08
 */
public class ShouldDescriptionNameSuggestionProvider implements NameSuggestionProvider {

    @Override
    public SuggestedNameInfo getSuggestedNames(PsiElement element, @Nullable PsiElement nameSuggestionContext, Set<String> result) {

        PsiDocTag shouldDocTag = BddUtil.getPsiDocTagParent(nameSuggestionContext);

        // create custom rename Dialog
        if (shouldDocTag != null) {
            //  KEEP THE DESCRIPTION AS THE ONLY ONE NAME SUGGESTION
            result.clear();
            result.add(BddUtil.getShouldTagDescription(shouldDocTag));
        }
        return null;
    }

}
