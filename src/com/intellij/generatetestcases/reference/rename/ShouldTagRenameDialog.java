package com.intellij.generatetestcases.reference.rename;

import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenameProcessor;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.refactoring.rename.naming.AutomaticRenamerFactory;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;


public class ShouldTagRenameDialog extends RenameDialog {


    private PsiDocTag shouldDocTag;

    public ShouldTagRenameDialog(@NotNull Project project, @NotNull PsiElement psiElement, @Nullable PsiElement nameSuggestionContext, Editor editor, PsiDocTag shouldDocTag) {
        super(project, psiElement, nameSuggestionContext, editor);
        this.shouldDocTag = shouldDocTag;
    }

    @Override
    protected boolean areButtonsValid() {
        //  should return true if there is actually something written
        return !StringUtils.isEmpty(getNewName());
    }


    public void performRename(final String newName) {

        // TODO check if test method actually exists

    // TODO obtener clase actual
    // TODO construir TestClass,  buscar tag

        // TODO renombrar metodo si existe si no solo renombrar descripcion



//        final RenamePsiElementProcessor elementProcessor = RenamePsiElementProcessor.forElement(myPsiElement);
//        elementProcessor.setToSearchInComments(myPsiElement, isSearchInComments());
//        if (myCbSearchTextOccurences.isEnabled()) {
//            elementProcessor.setToSearchForTextOccurrences(myPsiElement, isSearchInNonJavaFiles());
//        }
//        if (mySuggestedNameInfo != null) {
//            mySuggestedNameInfo.nameChoosen(newName);
//        }
//
//        final RenameProcessor processor = new RenameProcessor(getProject(), myPsiElement, newName, isSearchInComments(),
//                isSearchInNonJavaFiles());
//
//        for (Map.Entry<AutomaticRenamerFactory, JCheckBox> e : myAutomaticRenamers.entrySet()) {
//            e.getKey().setEnabled(e.getValue().isSelected());
//            if (e.getValue().isSelected()) {
//                processor.addRenamerFactory(e.getKey());
//            }
//        }
//
//        invokeRefactoring(processor);
    }


    // it is  called from parent constructor so we are using a nameSuggestionProvider
//    @Override
//    public String[] getSuggestedNames() {
//
//        String shouldTagDescription = BddUtil.getShouldTagDescription(shouldDocTag);
//        return new String[] {shouldTagDescription};
//    }

    ////////// when renamiin


}
