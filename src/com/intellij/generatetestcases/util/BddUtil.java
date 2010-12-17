package com.intellij.generatetestcases.util;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiImportStatementImpl;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Jaime Hablutzel
 */
public class BddUtil {

    /**
     * TODO the plugin should have support for generating junit 3 test methods (this is: with test as prefix for the name
     * and without annotations).
     *
     * @param originMethodName
     * @param shouldDescription
     * @return
     * @should create a appropiate name for the test method
     * @should fail if wrong args
     */
    public static String generateTestMethodNameForJUNIT4(@NotNull String originMethodName, @NotNull String shouldDescription) {

        if (StringUtils.isBlank(originMethodName) || StringUtils.isBlank(shouldDescription)) {
            throw new IllegalArgumentException();
        }

        StringBuilder builder = new StringBuilder(originMethodName
                + "_should");
        @NotNull
        String[] tokens = shouldDescription.split("\\s+");
        for (String token : tokens) {
            char[] allChars = token.toCharArray();
            StringBuilder validChars = new StringBuilder();
            for (char validChar : allChars) {
                if (Character.isJavaIdentifierPart(validChar)) {
                    validChars.append(validChar);
                }
            }
            builder.append(toCamelCase(validChars.toString()));
        }
        return builder.toString();
    }

    private static String toCamelCase(String input) {
        assert input != null;
        if (input.length() == 0) {
            return ""; // is it ok?
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }


    public static String generateJUNIT3MethodName(String sutMethodName, String description) {
        String s = generateTestMethodNameForJUNIT4(sutMethodName, description);
        return "test" + StringUtils.capitalize(s);
    }

    public static List<PsiImportStatementBase> findImportsInClass(PsiClass testBackingClass, String importName) {

        final PsiImportList[] psiImportList = {null};
        testBackingClass.getScope().acceptChildren(new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof PsiImportList) {
                    psiImportList[0] = (PsiImportList) element;
                }
            }

        });

        PsiImportList list = psiImportList[0];


        PsiImportStatementBase[] importStatementBases = list.getAllImportStatements();
        List<PsiImportStatementBase> matchingImports1 = new ArrayList<PsiImportStatementBase>();
        for (PsiImportStatementBase importStatementBase : importStatementBases) {
            String s = ((PsiImportStatementImpl) importStatementBase).getQualifiedName();
            if (s.equals(importName)) {
                matchingImports1.add(importStatementBase);
            }

        }
        List<PsiImportStatementBase> matchingImports = matchingImports1;
        return matchingImports;
    }
}
