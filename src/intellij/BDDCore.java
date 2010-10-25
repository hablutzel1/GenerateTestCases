package intellij;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

/**
 * User: Jaime Hablutzel
 */
public class BDDCore {


    protected BDDCore() {
    }


    /**
     * It loads an existing test class for the psiClass passed OR
     * creates a new TestClass will all of its test methods not yet
     * created, but available in {@link TestClass#getAllMethods()}
     *
     *
     * @param project
     * @param psiClass
     * @return
     * @should create a new test class with test methods unitialized 
     */
    public static TestClass createTestClass(Project project, PsiClass psiClass) {

        return null;
    }

}
