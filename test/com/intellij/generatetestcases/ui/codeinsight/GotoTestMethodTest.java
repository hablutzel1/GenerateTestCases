package com.intellij.generatetestcases.ui.codeinsight;

import com.intellij.generatetestcases.test.*;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.*;
import junit.framework.Assert;
import org.hamcrest.core.*;
import org.jetbrains.annotations.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: jhe
 */
public class GotoTestMethodTest extends BaseTests {

    @NotNull
    protected GotoTestMethod gotoTestMethod;

    @Override
    protected void setUp() throws Exception {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        gotoTestMethod = new GotoTestMethod();
    }

    /**
     * @verifies return null if the incoming element is whatever but a should tag
     * @see GotoTestMethod#getGotoDeclarationTarget(com.intellij.psi.PsiElement)
     */
    public void testGetGotoDeclarationTarget_shouldReturnNullIfTheIncomingElementIsWhateverButAShouldTag() throws Exception {

        //  get a test method
        PsiClass psiClass = createSutClass();

        //   get a element of the description
        PsiMethod[] methods = psiClass.findMethodsByName("getUserByUuid", false);

        //  create or get a tag
        PsiDocTagValue valueElement = methods[0].getDocComment().getTags()[3].getValueElement();

        //  call method
        //  expect null
        assertThat(gotoTestMethod.getGotoDeclarationTarget(valueElement), is(nullValue()));


    }

    /**
     * @verifies return the backing test method for a (at)should tag
     * @see GotoTestMethod#getGotoDeclarationTarget(com.intellij.psi.PsiElement)
     */
    public void testGetGotoDeclarationTarget_shouldReturnTheBackingTestMethodForAAtshouldTag() throws Exception {

        //  set data
        PsiClass psiClass = createSutClass();
        PsiClass testClassForSut = createTestClassForSut();

        //  get a should PsiDocTag with a corresponding test method
        //   get a element of the description
        PsiMethod[] methods = psiClass.findMethodsByName("getUserByUuid", false);

        //  create or get a tag
        PsiElement nameElement = methods[0].getDocComment().getTags()[3].getNameElement();

        //  call method and expect the test method
        PsiElement backingTestMethod = gotoTestMethod.getGotoDeclarationTarget(nameElement);

        PsiMethod expectedBackingTestMethod = testClassForSut.findMethodsByName("testGetUserByUuid_shouldFetchUserWithGivenUuid", false)[0];

        assertThat((PsiMethod) backingTestMethod, is(expectedBackingTestMethod));


    }

    /**
     * @verifies return null if the (at)should tag doesn't have a corresponding created test method
     * @see GotoTestMethod#getGotoDeclarationTarget(com.intellij.psi.PsiElement)
     */
    public void testGetGotoDeclarationTarget_shouldReturnNullIfTheAtshouldTagDoesntHaveACorrespondingCreatedTestMethod() throws Exception {


        //  set data
        PsiClass psiClass = createSutClass();
        createTestClassForSut();

        //  get a should PsiDocTag with a corresponding test method
        //   get a element of the description
        PsiMethod[] methods = psiClass.findMethodsByName("getUserByUuid", false);

        //  create or get a tag
        PsiElement nameElement = methods[0].getDocComment().getTags()[4].getNameElement();

        //  call method and expect the test method
        PsiElement backingTestMethod = gotoTestMethod.getGotoDeclarationTarget(nameElement);

        assertThat(backingTestMethod, is(IsNull.<Object>nullValue()));


    }
}
