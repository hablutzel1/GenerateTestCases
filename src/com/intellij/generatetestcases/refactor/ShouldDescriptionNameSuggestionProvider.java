package com.intellij.generatetestcases.refactor;

import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
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


    /**
     * @param element
     * @param nameSuggestionContext
     * @param result
     * @return
     * @should be able to get the (at)should tag from the context selection as the ShouldTagsAwareRenameProccessor
     */
    @Override
    public SuggestedNameInfo getSuggestedNames(PsiElement element, @Nullable PsiElement nameSuggestionContext, Set<String> result) {

        if (nameSuggestionContext == null) {// I don't undertand nameSuggestionContext oddities sometimes it is coming a null value, for those cases just assume we won't find any @should tag
            return null;
        }

        // This should have the same logic that {@link ShouldTagsAwareRenameProccessor} for knowing if we are in the context of a @should tag

        PsiDocTag shouldDocTag = BddUtil.getPsiDocTagParent(nameSuggestionContext);

        // fix for PsiWhiteSpace last resort, we could be at the right of a @should tag
        if (shouldDocTag == null && nameSuggestionContext instanceof PsiWhiteSpace) {
            PsiElement prevSibling = nameSuggestionContext.getPrevSibling();
            if (prevSibling instanceof PsiDocTag) {
                boolean validShouldTag = BddUtil.isValidShouldTag((PsiDocTag) prevSibling);
                if (validShouldTag ) {
                    shouldDocTag = (PsiDocTag) prevSibling;
                }
            }
        }

        // create custom rename Dialog
        if (shouldDocTag != null) {
            //  KEEP THE DESCRIPTION AS THE ONLY ONE NAME SUGGESTION
            result.clear();
            result.add(BddUtil.getShouldTagDescription(shouldDocTag));
        }
        return null;
    }

}
