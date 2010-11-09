package com.intellij.generatetestcases;

import com.intellij.psi.PsiMethod;

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
     * @should return a description without leading or trailing spaces
     */
    String getDescription();


    /**
     * It will return the method in the class
     * under test this test method has been created for
     *
     * @return
     * @should return the sut method for this method 
     */
    PsiMethod getSutMethod();


    /**
     * This method will return the PsiMethod for this test method,
     * it will only return a valid PsiMethod if reallyExists() returns true
     * othewise it will return null
     *
     * @return
     * @should return a valid psiMethod if reallyExists returns true, false otherwise 
     */
    PsiMethod getBackingMethod();


    /**
     * Creates a Test method if it didn't exist
     * User should delimit write action... research which is the behaviour of intellij regarding undo operations
     *
     * @should create the current test method
     * @should create the parent in the same content source root that its backing method if it didn't exist already
     * @should add required imports in test class as required for its javadoc and its body
     * @should create a test method with the expected body and javadoc
     * @should format the test file appropriately
     */
    void create();


    /**
     * Tells if the current Test Method does really exit
     * in the TestClass, if it doesn't exists it means
     * it only exists as a should javadoc tag, and it
     * can be created with {@link TestMethod#create()}
     *
     * @return
     * @should return true only if this test method has a backing psiMethod
     */
    boolean reallyExists();
}
