package com.intellij.generatetestcases.impl;

import com.intellij.generatetestcases.TestMethod;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.generatetestcases.TestClass;

import java.util.ArrayList;
import java.util.List;


/**
 * User: JHABLUTZEL
 * Date: 29/10/2010
 * Time: 08:57:26 AM
 */
public class TestClassImpl implements TestClass {

    private static final String BDD_TAG = "should";

    List<TestMethod> testMethods;
    private PsiClass sutClass;
    private static final String TEST_CLASS_SUFFIX = "Test";
    private Project project;

    public TestClassImpl(PsiClass psiClass) {
        //  popular TestClass con TestMethods
        this.sutClass = psiClass;

        // init a reference to the current project
              project = sutClass.getProject();
        

        findAndInitializeAllTestMethods(psiClass);



    }

    /**
     * Initializes all test methods from psiClass
     *
     * @param psiClass
     */
    private void findAndInitializeAllTestMethods(PsiClass psiClass) {
        //   iterar sobre los metodos de la clase
        PsiMethod[] methods = psiClass.getMethods();
        ArrayList<TestMethod> array = new ArrayList<TestMethod>();
        //  iterar sobre los metodos
        for (PsiMethod method : methods) {


            //  iterar sobre los comentarios del javadoc
            PsiDocComment comment = method.getDocComment();
            if (comment == null) { // if no doc comment
                continue;
            }
            PsiDocTag[] tags = comment.getTags();
            //   iterar sobre los comentarios del javadoc
            for (PsiDocTag tag : tags) {
                //  comprobar que el tag sea del tipo should
                if (tag.getName().equals(BDD_TAG)) {
                    TestMethod tm = new TestMethodImpl(tag, this);
                    array.add(tm);
                }
            }
        }

        this.testMethods = array;
    }

    public List<TestMethod> getAllMethods() {
        return testMethods;
    }

    public void create(PsiDirectory sourceRoot) {

        if (sourceRoot == null) {
            //  create the test class in the same source root

            //  get psiDirectory for sut class
            PsiElement parentPackage = sutClass.getScope().getParent();
            // get test class name
            String testClassName = getCandidateTestClassName();
            //  check
            JavaDirectoryService.getInstance().checkCreateClass((PsiDirectory) parentPackage, testClassName);
            //  create
            JavaDirectoryService.getInstance().createClass((PsiDirectory) parentPackage, testClassName, "Class");

        } else {

            //  create the test class in the specified source root
            // get test class name
            String testClassName = getCandidateTestClassName();

            //  check or create entire path to package
            PsiDirectory psiDirectory = DirectoryUtil.createSubdirectories(getPackageName(), sourceRoot, ".");

            //  check
            JavaDirectoryService.getInstance().checkCreateClass(psiDirectory, testClassName);
            //  create
            JavaDirectoryService.getInstance().createClass(psiDirectory, testClassName, "Class");

        }

    }


    public boolean reallyExists() {
        PsiClass psiClass = findBackingPsiClass();

        if (psiClass != null) {
            return true;
        } else {
            return false;
        }

    }

    private PsiClass findBackingPsiClass() {
        String fullyQualifiedTestClass = getFullyQualifiedTestClassName();
        //  verify if the test class really exists in classpath for the current module/project
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(fullyQualifiedTestClass, GlobalSearchScope.projectScope(project));
        return psiClass;
    }

    private String getFullyQualifiedTestClassName() {

        String packageName = getPackageName();
        String testClassName = getCandidateTestClassName();
        String fullyQualifiedTestClass = packageName + "." + testClassName;

        return fullyQualifiedTestClass;
    }

    private String getCandidateTestClassName() {
        //  build the test class name
        //  get the sut class name
        String s = sutClass.getName();
        String testClassName = s + TEST_CLASS_SUFFIX;
        return testClassName;
    }

    private String getPackageName() {
        //  get the package
        String qualifiedSutName = sutClass.getQualifiedName();
        String packageName = sutClass.getQualifiedName().substring(0, qualifiedSutName.lastIndexOf("."));
        return packageName;
    }

    public PsiClass getBackingClass() {
        return findBackingPsiClass();
    }


    public PsiClass getClassUnderTest() {
        return this.sutClass;
    }
}
