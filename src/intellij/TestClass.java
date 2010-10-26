package intellij;

import java.util.List;

/**
 * User: Jaime Hablutzel
 */
public interface TestClass {


    List<TestMethod> getAllMethods();

    // TODO agregar metodos para buscar metodos de prueba

     /**
     * Creates a Test Class if it didn't exist
     */
    void create();


    /**
     * Tells if this test class file really exists
     *
     * @return
     */
    boolean reallyExists();

}
