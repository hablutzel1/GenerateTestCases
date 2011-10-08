package com.intellij.generatetestcases.refactor;

import com.intellij.generatetestcases.TestMethod;
import com.intellij.generatetestcases.refactor.psi.NoExistentTestMethodLightReference;
import com.intellij.generatetestcases.util.*;
import com.intellij.lang.StdLanguages;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
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
    private TestMethod testMethod;

    /**
     * Construct a ShouldReference with the (at)should tag and the TestMethod corresponding to our (at)should tag so we avoid the need to recreate the TestClass, TestMethod hieararchy every time the refactor is queried.
     *
     * @param psiDocTag
     * @param testMethod
     */
    public ShouldReference(PsiDocTag psiDocTag, TestMethod testMethod) {

        // TODO assert psiDocTag is valid for us

        this.psiDocTag = psiDocTag;
        this.testMethod = testMethod;
    }

    @Override
    public PsiElement getElement() {
        return psiDocTag;
    }


    /**
     * It  supports multiline descriptions too, and for these the hyperlink continue over
     * asterisks.
     *
     * TODO IT will return an empty range for (at)should tags without a test method created * so any link is created for the user, but it disables the refactor
      *
     * @return
     * @should return only the range for the description without the javadoc tag
     */
    @Override
    public TextRange getRangeInElement() {

//        if (!testMethod.reallyExists()){
//            return TextRange.EMPTY_RANGE;
//        }

        List<BddUtil.DocOffsetPair> elementPairsInDocTag = BddUtil.getElementPairsInDocTag(psiDocTag);

        PsiElement start = elementPairsInDocTag.get(0).getStart();

        PsiElement lastEl = elementPairsInDocTag.get(elementPairsInDocTag.size()-1).getEnd();

        int fullStart = start.getTextRange().getStartOffset();
        int fullEnd = lastEl.getTextRange().getEndOffset();
        int referenceStart = psiDocTag.getTextRange().getStartOffset();


        return new TextRange(fullStart-referenceStart, fullEnd - referenceStart);
    }


    /**
     * Will return a PsiMethod for the test method or a NoExistentTestMethodLightReference if the test method isn't created yet
     *
     * @return
     */
    @Override
    public PsiElement resolve() {


        if (testMethod.reallyExists()) {
            return testMethod.getBackingElement();
        }

        //  return a dummy refactor so we can rename  @should's without a method created
        return new NoExistentTestMethodLightReference(psiDocTag.getManager(), StdLanguages.JAVA);
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        // TODO return the fqdn of the test method
        throw new UnsupportedOperationException();
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        // TODO rename PsiDocTag and test method
        // consider PsiPolyVariantReference
        // consider BindablePsiReference for fixing refactoring
        // look for implementation that make use of bindToElement method

//          <renamePsiElementProcessor implementation="com.intellij.refactoring.rename.RenamePsiFileProcessor" order="last"/> if nothing else works
//        return null;
        throw new UnsupportedOperationException();
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
