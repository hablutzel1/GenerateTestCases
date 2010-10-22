package intellij.ui.codeinsight.generation;

import com.intellij.codeInsight.generation.ClassMemberWithElement;
import com.intellij.codeInsight.generation.MemberChooserObject;
import com.intellij.codeInsight.generation.PsiElementMemberChooserObject;
import com.intellij.codeInsight.generation.PsiMethodMember;
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


    public PsiDocAnnotationMember(@org.jetbrains.annotations.NotNull PsiElement psiElement, String text) {
        super(psiElement, text);
    }

    public PsiDocAnnotationMember(PsiElement psiElement, String text, PsiMethod method) {
        super(psiElement, text);
        this.psiDocTag = psiElement;
        this.method = method;
        this.member = new PsiMethodMember(method);

    }


    public PsiElement getElement() {
        return psiDocTag;
    }

    public MemberChooserObject getParentNodeDelegate() {
        return member;
    }
}
