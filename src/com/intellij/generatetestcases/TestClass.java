package com.intellij.generatetestcases;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;

import java.util.List;

/**
 * User: Jaime Hablutzel
 */
public interface TestClass {


    /**
     *
     * @return
     * @should get all test methods matching sut should tags
     * @should always reflect the test class state of created methods
     */
    List<TestMethod> getAllMethods();

    // TODO agregar metodos para buscar metodos de prueba

    /**
     * Creates a Test Class if it didn't exist  in the specified source root, by the time it just creates
     * a class with the suffix Test, this is, if a sut class is called Foo, a new class with the name FooTest
     * will be created in the specified sourceRoot directory, if a class called do.com.Zas (fully qualified name) is created
     * then two packages will be verified/created in the specified sourceRoot.
     *
     *
     * @should create the new java test class in the same directory that the origin class if testRoot is null, in the specified test root if not null
     * @param sourceRoot the specific PsiDirectory source root where the test class and package should be created
     */
    void create(PsiDirectory sourceRoot);


    /**
     * Tells if this test class file really exists in the classpath,
     * it is: if this is a test class for the class Foo, it return true if
     * there in the classpath (test classpath ??) exists a class with the
     * name FooTest
     *
     * @return
     * @should return true only if there is a class in the classpath with the name that this class should have
     */
    boolean reallyExists();


    /**
     * It should return the actual PsiClass for this testClass if reallyExists return true, or null
     * otherwise
     *
     * @return
     * @should return a psiClass if this really exists, null otherwise
     */
    PsiClass getBackingClass();

}
