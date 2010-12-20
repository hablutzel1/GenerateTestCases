package com.intellij.generatetestcases.impl;

import com.intellij.generatetestcases.TestMethod;
import com.intellij.generatetestcases.testframework.JUnit4Strategy;
import com.intellij.generatetestcases.testframework.TestFrameworkStrategy;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
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
    private TestFrameworkStrategy frameworkStrategy;

    public TestClassImpl(PsiClass psiClass, TestFrameworkStrategy frameworkStrategy) {
        //  popular TestClass con TestMethods
        this.sutClass = psiClass;

        this.frameworkStrategy = frameworkStrategy;

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
                    TestMethod tm = new TestMethodImpl(tag, this, this.frameworkStrategy);
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

        if (sourceRoot == null || sourceRoot.equals(sutClass.getContainingFile().getParent())) {
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


            VirtualFile path = sourceRoot.getVirtualFile().findFileByRelativePath(getPackageName().replace(".", "/"));
            PsiDirectory psiDirectory = null;
            if (path == null) {
                //  check or create entire path to package
                psiDirectory = DirectoryUtil.createSubdirectories(getPackageName(), sourceRoot, ".");

            } else {
                //  just create a psi directory for VirtualFile
                psiDirectory = PsiManager.getInstance(project).findDirectory(path);
            }
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
        return JavaPsiFacade.getInstance(project).findClass(fullyQualifiedTestClass, GlobalSearchScope.projectScope(project));
    }

    private String getFullyQualifiedTestClassName() {

        String packageName = getPackageName();
        String testClassName = getCandidateTestClassName();

        return packageName == null ? testClassName : packageName + "." + testClassName;
    }

    private String getCandidateTestClassName() {
        //  build the test class name
        //  get the sut class name
        String s = sutClass.getName();
        return s + TEST_CLASS_SUFFIX;
    }

    /**
     * It will return null if no package declaration is found
     * 
     * @return
     */
    private String getPackageName() {
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

    public PsiClass getBackingClass() {
        return findBackingPsiClass();
    }


    public PsiClass getClassUnderTest() {
        return this.sutClass;
    }
}
