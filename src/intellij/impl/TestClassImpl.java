package intellij.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import intellij.TestClass;
import intellij.TestMethod;

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

    public TestClassImpl(PsiClass psiClass) {
        //  popular TestClass con TestMethods
        this.sutClass = psiClass;
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

    }


    public boolean reallyExists() {
        PsiClass psiClass = findBackingPsiClass();

//        sutClass.getScope()
        if (psiClass != null) {
            return true;
        } else {
            return false;
        }
        //ProjectRootManager.getInstance(project).get

//        return false;
    }

    private PsiClass findBackingPsiClass() {
        //  get the sut class name
        String s = sutClass.getName();

        //  get the package
        String qualifiedSutName = sutClass.getQualifiedName();
        String packageName = sutClass.getQualifiedName().substring(0, qualifiedSutName.lastIndexOf("."));

        //  build the test class name
        String testClassName = s + TEST_CLASS_SUFFIX;

        // TODO verify if the test class really exists in classpath for the current module/project
        Project project = sutClass.getProject();

        String fullyQualifiedTestClass = packageName + "." + testClassName;
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(fullyQualifiedTestClass, GlobalSearchScope.projectScope(project));
        return psiClass;
    }

    public PsiClass getBackingClass() {
        return findBackingPsiClass();
    }
}
