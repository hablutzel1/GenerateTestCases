package com.intellij.generatetestcases.javadoc;

import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.javadoc.JavadocTagInfo;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NonNls;

/**
 * Provee de soporte al IDe para reconocer nativamente el should tag
 *
 * @author jaime
 */
public class ShouldTagInfo implements JavadocTagInfo {

    private final String myName;
    private final Class myContext;
    private final boolean myInline;
    private final LanguageLevel myLanguageLevel;

    public ShouldTagInfo() {
        myName = "should";
        myContext = PsiMethod.class;
        myInline = false;
        myLanguageLevel = LanguageLevel.JDK_1_3;
    }

    public String getName() {
        return myName;
    }

    public boolean isValidInContext(PsiElement element) {
        if (PsiUtil.getLanguageLevel(element).compareTo(myLanguageLevel) < 0) {
            return false;
        }

        return myContext.isInstance(element);
    }

    public Object[] getPossibleValues(PsiElement context, PsiElement place, String prefix) {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }

    public String checkTagValue(PsiDocTagValue value) {
        return null;
    }

    public PsiReference getReference(PsiDocTagValue value) {
        return null;
    }

    public boolean isInline() {
        return myInline;
    }
}