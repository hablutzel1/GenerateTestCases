package com.intellij.generatetestcases.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.SuggestedNameInfo;
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
        result.clear();
        result.add("Helllo :D");
        // TODO determine the description only :)
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
