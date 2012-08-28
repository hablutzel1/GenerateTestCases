package com.intellij.generatetestcases.util;

import com.intellij.generatetestcases.testframework.JUnit3Strategy;
import com.intellij.generatetestcases.testframework.JUnit4Strategy;
import com.intellij.generatetestcases.testframework.TestFrameworkStrategy;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiImportStatementImpl;
import com.intellij.psi.impl.source.javadoc.PsiDocTokenImpl;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.testIntegration.JavaTestFramework;
import com.intellij.testIntegration.TestFramework;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Jaime Hablutzel
 */
public final class BddUtil {

    private BddUtil() {
    }

    /**
     * It will return the trimmed description associated to a PsiDocTag
     *
     * @param shouldTag
     * @return
     * @should return the full description for a should tag backed by a PsiDocTag
     */
    public static String getShouldTagDescription(PsiDocTag shouldTag) {
        final StringBuilder description = new StringBuilder();

        PsiElement[] dataElements = shouldTag.getDataElements();
        boolean isFirst = true;
        for (PsiElement dataElement : dataElements) {
            description.append(dataElement.getText());
            // TODO get the description taking into account the whitespaces
            if (isFirst) {
                description.append(" ");
            }
            isFirst = false;
        }

        return description.toString().trim();

    }

    /**
     * This method test that a PsiDocTag has the name "should" and that its description isn't empty
     *
     * @param tag
     * @return
     */
    public static boolean isValidShouldTag(PsiDocTag tag) {
        return tag.getName().equals(Constants.BDD_TAG) && getShouldTagDescription(tag).length() > 0;
    }

    /**
     * Given a PsiElement it will return a PsiDocTag if it exists up in its hierarchy
     *
     * @param supposedPsiDocTagChild
     * @return a parent @should PsiDocTag or null if no one found
     */
    public static PsiDocTag getPsiDocTagParent(@Nullable PsiElement supposedPsiDocTagChild) {


        PsiElement startPsiElement = supposedPsiDocTagChild;

        PsiDocTag shouldDocTag = null;
        // TODO simplify to not iterate over the parents for completely uncompatible Psi hierarchy trees, only for PsiDocTag childs
        do {

            if (startPsiElement instanceof PsiDocTag) {
                if (isValidShouldTag((PsiDocTag) startPsiElement)) {
                    shouldDocTag = (PsiDocTag) startPsiElement;
                }
            }
        } while (null != (startPsiElement = startPsiElement.getParent()));
        return shouldDocTag;
    }

    public static PsiClass getParentEligibleForTestingPsiClass(PsiElement element) {
        PsiClass parentPsiClass = null;

        while (element != null) {
            if (element instanceof PsiFile) {
                if (!(element instanceof PsiClassOwner)) {
                    parentPsiClass = null;
                    break;
                }
                final PsiClass[] classes = ((PsiClassOwner) element).getClasses();
                parentPsiClass = (classes.length == 1 ? classes[0] : null);
                break;
            }
            if (element instanceof PsiClass && !(element instanceof PsiAnonymousClass)) {
                parentPsiClass = (PsiClass) element;
                break;

            }
            element = element.getParent();
        }
        return parentPsiClass;
    }

    public static class DocOffsetPair {

        public DocOffsetPair(PsiElement start, PsiElement end) {
            this.start = start;
            this.end = end;
        }

        public PsiElement getStart() {
            return start;
        }

        public void setStart(PsiElement start) {
            this.start = start;
        }

        public PsiElement getEnd() {
            return end;
        }

        public void setEnd(PsiElement end) {
            this.end = end;
        }

        public PsiElement start;
        public PsiElement end;
    }


    /**
     * For a should tag it will return a collection of pairs that correspond
     * to the first and last element of each line of <code>@should</code> description.
     * <br />
     * <p/>
     * This method will help the inspection method to only highlight the text and not the asterisks in javadoc when the description is over multiple lines
     *
     * @param psiDocTag
     * @return
     * @should return psi element pairs for start element and end element in each line for each should tag
     * @should not consider part of the problem whitespace/nl for not ending tags
     */
    public static List<DocOffsetPair> getElementPairsInDocTag(@NotNull PsiDocTag psiDocTag) {
//
//        si es soloo un elemento, la forma es:
//
//PsiDocToken: DOC_TAG_NAME
//PsiWHiteSpace
//PsiElement * *
//
//si son dos o mas en una sola linea la forma es
//
//PsiDocToken: DOC_TAG_NAME
//PsiWHiteSpace
//PsiElement *
//PsiWhiteSpace
//PsiDocToken: DOC_COMMENT_DATA *
//
//si son mas de dos lineas a partir de la segunda solo se cogen los psiDOcToken del tipo DOC COMMENT_DATA
//
//PsiDocToken: DOC_TAG_NAME
//PsiWHiteSpace
//PsiElement 1
//PsiWhiteSpace
//PsiDocToken: DOC_COMMENT_DATA 1
//PsiWhiteSpace
//PsiDocToken: DOC_COMMENT_DATA 2 2
//PsiDocToken: DOC_COMMENT_LEADING_ASTERISK
//PsiDocToken: DOC_COMMENT_DATA 2 2


        ArrayList<DocOffsetPair> returnPairs = new ArrayList<DocOffsetPair>();
        PsiElement[] dataElements = psiDocTag.getDataElements();

        if (dataElements.length == 0) {
            // TODO we should mark the @should as an error

        } else if (dataElements.length == 1) {
            DocOffsetPair offsetPair = new DocOffsetPair(dataElements[0], dataElements[0]);
            returnPairs.add(offsetPair);
        } else {

            boolean oneLineOrWeirdCharsShouldTag = true; // TODO simplify this logic using IDEA API

            PsiElement[] children = psiDocTag.getChildren();
            for (int i = 0; i < children.length; i++) {
                PsiElement child = children[i];
                if (child instanceof PsiDocTagValue) {
                    PsiElement leadingAsterisks = children[i + 2];
                    if (children.length > i + 2 &&  leadingAsterisks instanceof PsiDocToken && ((PsiDocToken) leadingAsterisks).getTokenType().toString().equals("DOC_COMMENT_LEADING_ASTERISKS")) {
                        oneLineOrWeirdCharsShouldTag = false;
                    }
                }
            }


            if (oneLineOrWeirdCharsShouldTag) {  // one line @should tags or @should foo; bar (weird chars for keyword)
                DocOffsetPair firstPair = new DocOffsetPair(dataElements[0], dataElements[1]);
                returnPairs.add(firstPair);
            } else {
                DocOffsetPair firstPair = new DocOffsetPair(dataElements[0], dataElements[0]);
                returnPairs.add(firstPair);

                if (!StringUtils.isBlank(dataElements[1].getText())) {
                    DocOffsetPair secondPair = new DocOffsetPair(dataElements[1], dataElements[1]);
                    returnPairs.add(secondPair);
                }

            }


            for (int i = 2; i < dataElements.length; i++) {

                PsiElement dataElement = dataElements[i];

                if (dataElement.getText().trim().length() != 0) { // excludes whitespace
                    DocOffsetPair furtherPairs = new DocOffsetPair(dataElement, dataElement);
                    returnPairs.add(furtherPairs);
                }
            }
        }

        return returnPairs;
    }

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

//            if (importStatementBase instanceof PsiImportStatementImpl) {
            String s = ((PsiImportStatementImpl) importStatementBase).getQualifiedName();
            if (s.equals(importName)) {
                matchingImports1.add(importStatementBase);
            }
//            }
        }
        List<PsiImportStatementBase> matchingImports = matchingImports1;
        return matchingImports;
    }


    /**
     * It will search in com.intellij.testFrameworkDescriptor extension point
     * for descriptors with the expected name
     *
     * @param name
     * @return
     */
    public static JavaTestFramework findTestFrameworkByName(String name) {

        //  get a test framework from platform extension
        for (final TestFramework descriptor : Extensions.getExtensions(TestFramework.EXTENSION_NAME)) {
            if (descriptor.getName().equals(name)) {
                return (JavaTestFramework) descriptor;
            }
        }
        //  return null if not found
        return null;
    }

    /**
     * @param sutClass
     * @return It will return null if no package declaration is found or the package
     */
    public static String getPackageName(PsiClass sutClass) {

        // TODO look at com.intellij.psi.util.PsiFormatUtil.getPackageDisplayName() for an standard way to access the package

        //  get the package
        String qualifiedSutName = sutClass.getQualifiedName();
        int i = qualifiedSutName.lastIndexOf(".");
        if (i != -1) {
            return qualifiedSutName
                    .substring(0, i);
        } else {
            return null;
        }
    }


    /**
     * Should return a framework strategy based on a String
     * <p/>
     * FIXME TODO search usages, and remove
     *
     * @param project
     * @param s
     * @return
     */
    public static TestFrameworkStrategy getStrategyForFramework(Project project, String s) {
        TestFrameworkStrategy tfs;
        if (s.equals("JUNIT3")) {
            tfs = new JUnit3Strategy(project);
        } else {
            tfs = new JUnit4Strategy(project);
        }

        return tfs;
    }
}
