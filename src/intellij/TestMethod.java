package intellij;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocTag;

/**
 * User: Jaime Hablutzel
 */
public interface TestMethod {

    // TODO agregar operaciones que permitan conocer la ubicacion del tag javadoc

    /**
     * Devuelve la descripcion correspondiente
     * al tag de javadoc
     *
     * @return
     */
    String getDescription();


    /**
     * It will return the method in the class
     * under test, this test method has been created for
     *
     * @return
     */
    PsiMethod getSutMethod();


    /**
     * Creates a Test method if it didn't exist
     */
    void create();


    /**
     * Tells if the current Test Method does really exit
     * in the TestClass, if it doesn't exists it means
     * it only exists as a should javadoc tag, and it
     * can be created with {@link TestMethod#create()}
     *
     * @return
     */
    boolean reallyExists();
}
