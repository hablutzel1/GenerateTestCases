package intellij;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;

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
     * @param project
     * @param psiClass origin class
     * @param testRoot the specific PsiDirectory for the package where the test class should be created
     * @return
     * @should create a new test class with test methods unitialized
     */
    public static TestClass createTestClass(Project project, PsiClass psiClass, PsiDirectory testRoot) {

        // TODO obtener paquete de la clase actual
        // TODO crear clase de prueba en el testRoot

        // TODO  iterar sobre los metodos de la clase
        // TODO  iterar sobre los comentarios del javadoc
        // TODO comprobar que el tag sea del tipo should


        // TODO instanciar TestClass

        // TODO popular TestClass con TestMethods


        // TODO implementar

        return null;
    }

}
