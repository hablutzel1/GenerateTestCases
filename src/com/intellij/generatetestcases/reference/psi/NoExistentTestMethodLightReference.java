package com.intellij.generatetestcases.reference.psi;

import com.intellij.lang.Language;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.light.LightElement;

/**
 * This light PsiElement will be used when referencing to non existent
 * test method, {@link com.intellij.generatetestcases.reference.ShouldTagsAwareRenameProccessor} is still able to find a valid reference and rename a (at)should tag that haven't yet a test method.
 *
 * Created by jhe
 * Time: 14:14
 */
public class NoExistentTestMethodLightReference extends LightElement{



    public NoExistentTestMethodLightReference(PsiManager manager, Language language) {
        super(manager, language);
    }

    @Override
    public String toString() {
        return "PsiBDDTestMethod:";
    }

    @Override
    public boolean isWritable() {
        return true;
    }
}
