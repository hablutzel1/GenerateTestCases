package intellij.impl;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocTag;
import intellij.TestMethod;

/**
 * User: Jaime Hablutzel
 */
public class TestMethodImpl implements TestMethod {


    private PsiMethod sutMethod;

    private PsiDocTag shouldTag;

    public TestMethodImpl(PsiDocTag shouldTag, PsiMethod sutMethod) {
        this.shouldTag = shouldTag;
        this.sutMethod = sutMethod;

        // TODO no hacer nada hasta escribir una prueba unitaria :D
        // TODO obtener el nombre de la clase

    }

    public boolean reallyExists() {
        return existsInSut;
    }

    public void create(){

    }

    private boolean existsInSut;


    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PsiMethod getSutMethod() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
