package com.intellij.generatetestcases.javadoc;

import com.intellij.generatetestcases.util.*;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.javadoc.JavadocTagInfo;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.util.ArrayUtil;

/**
 * Provee de soporte al IDe para reconocer nativamente el should tag
 * TODO consider the possibility to use its methods (getReference, ...) to provide renaming and referencing support
 *
 * @author jaime
 */
public class ShouldTagInfo implements JavadocTagInfo {

    private final String myName;
    private final Class myContext;
    private final boolean myInline;
    private final LanguageLevel myLanguageLevel;

    public ShouldTagInfo() {
        myName = Constants.BDD_TAG;
        myContext = PsiMethod.class;
        myInline = false;
        myLanguageLevel = LanguageLevel.JDK_1_3;
    }

    public String getName() {
        return myName;
    }

    public boolean isValidInContext(PsiElement element) {
        return PsiUtil.getLanguageLevel(element).compareTo(myLanguageLevel) >= 0 && myContext.isInstance(element);
    }

    public Object[] getPossibleValues(PsiElement context, PsiElement place, String prefix) {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }

    public String checkTagValue(PsiDocTagValue value) {
        return null;
    }

    public PsiReference getReference(PsiDocTagValue value) {

        // it seems that this provide similar reference support to com.intellij.generatetestcases.reference.TestMethodReferenceProvider
        return null;
    }

    public boolean isInline() {
        return myInline;
    }
}