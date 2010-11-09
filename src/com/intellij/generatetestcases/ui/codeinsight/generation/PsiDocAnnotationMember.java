package com.intellij.generatetestcases.ui.codeinsight.generation;

import com.intellij.codeInsight.generation.ClassMemberWithElement;
import com.intellij.codeInsight.generation.MemberChooserObject;
import com.intellij.codeInsight.generation.PsiElementMemberChooserObject;
import com.intellij.codeInsight.generation.PsiMethodMember;
import com.intellij.generatetestcases.TestMethod;
import com.intellij.generatetestcases.impl.TestMethodImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;

/**
 *
 * TODO revisar bien esta clase y poner nombres mas adecuados a los miembros
 *
 * User: JHABLUTZEL
 * Date: 22/10/2010
 * Time: 12:22:44 PM
 */
public class PsiDocAnnotationMember extends PsiElementMemberChooserObject implements ClassMemberWithElement {
    private PsiElement psiDocTag;
    private PsiMethod method;
    private PsiMethodMember member;

    public TestMethod getTestMethod() {
        return testMethod;
    }

    private TestMethod testMethod;


    public PsiDocAnnotationMember(TestMethod tm) {
        super(((TestMethodImpl)tm).getBackingTag(), tm.getDescription());
        this.testMethod = tm;
        this.psiDocTag = ((TestMethodImpl)tm).getBackingTag();
        this.method = tm.getSutMethod();
        this.member = new PsiMethodMember(method);

    }



    public PsiElement getElement() {
        return psiDocTag;
    }

    public MemberChooserObject getParentNodeDelegate() {
        return member;
    }
}
