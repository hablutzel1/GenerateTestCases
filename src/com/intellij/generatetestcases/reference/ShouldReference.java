package com.intellij.generatetestcases.reference;

import com.intellij.generatetestcases.BDDCore;
import com.intellij.generatetestcases.TestClass;
import com.intellij.generatetestcases.TestFrameworkNotConfigured;
import com.intellij.generatetestcases.TestMethod;
import com.intellij.generatetestcases.impl.TestMethodImpl;
import com.intellij.generatetestcases.util.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by jhe
 * Time: 18:54
 */
public class ShouldReference implements PsiReference {

    private  final Logger logger = Logger.getInstance(getClass());

    private PsiDocTag psiDocTag;

    public ShouldReference(PsiDocTag psiDocTag) {
        this.psiDocTag = psiDocTag;
    }

    @Override
    public PsiElement getElement() {
        return psiDocTag;
    }


    /**
     *
     *
     * It  supports multiline descriptions too, and for these the hyperlink continue over
     * asterisks
     *
     * @return
     * @should return only the range for the description without the javadoc tag
     */
    @Override
    public TextRange getRangeInElement() {

        List<BddUtil.DocOffsetPair> elementPairsInDocTag = BddUtil.getElementPairsInDocTag(psiDocTag);

        PsiElement start = elementPairsInDocTag.get(0).getStart();

        PsiElement lastEl = elementPairsInDocTag.get(elementPairsInDocTag.size()-1).getEnd();

        int fullStart = start.getTextRange().getStartOffset();
        int fullEnd = lastEl.getTextRange().getEndOffset();
        int referenceStart = psiDocTag.getTextRange().getStartOffset();


        return new TextRange(fullStart-referenceStart, fullEnd - referenceStart);
    }

    @Override
    public PsiElement resolve() {
        //  find the target test method
        PsiElement parentPsiClass = psiDocTag;

        do {
            parentPsiClass = parentPsiClass.getParent();
        } while (!(parentPsiClass instanceof PsiClass));

        TestClass testClass;
        try {
            testClass = BDDCore.createTestClass((PsiClass) parentPsiClass);
        } catch (TestFrameworkNotConfigured testFrameworkNotConfigured) {
            // TODO log it
            logger.warn("Trying to resolve test methods but no framework is configured");
            return null;
        }
        List<TestMethod> allMethods = testClass.getAllMethods();
        for (TestMethod testMethod : allMethods) {
//            if (allMethod.get)
            PsiDocTag backingTag = ((TestMethodImpl) testMethod).getBackingTag();
            if (backingTag.equals(psiDocTag)) {
                if (testMethod.reallyExists()) {
                    return testMethod.getBackingElement();
                }
            }
        }

        return null;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        // TODO return the fqdn of the test method
        return null;
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        // TODO rename PsiDocTag and test method
        // consider PsiPolyVariantReference
        // consider BindablePsiReference for fixing refactoring
        // look for implementation that make use of bindToElement method

//          <renamePsiElementProcessor implementation="com.intellij.refactoring.rename.RenamePsiFileProcessor" order="last"/> if nothing else works
        return null;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
//        return null;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return false;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
