package intellij;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import intellij.impl.TestClassImpl;
import intellij.impl.TestMethodImpl;

import java.util.ArrayList;

/**
 * User: Jaime Hablutzel
 */
public class BDDCore {
    private static final String BDD_TAG = "should";


    protected BDDCore() {
    }


    /**
     * It loads an existing test class for the psiClass passed OR
     * creates a new TestClass will all of its test methods not yet
     * created, but available in {@link TestClass#getAllMethods()}
     *
     * @param project
     * @param psiClass origin class
     * @param testRoot the specific PsiDirectory for the package where the test class should be created
     * @return
     * @should create a new test class with test methods unitialized
     * @should return a test class that already exists for a sut class with some test methods initialized 
     */
    public static TestClass createTestClass(Project project, PsiClass psiClass, PsiDirectory testRoot) {

        //  popular TestClass con TestMethods

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
                        TestMethod tm = new TestMethodImpl(tag, method);
                        array.add(tm);
                    }
                }
            }


        //  instanciar un testclass
        TestClass testClass = new TestClassImpl(array);


        return testClass;
    }

}
