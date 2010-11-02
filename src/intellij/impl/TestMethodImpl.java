package intellij.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocTag;
import intellij.TestClass;
import intellij.TestMethod;
import intellij.util.BddUtil;

/**
 * User: Jaime Hablutzel
 */
public class TestMethodImpl implements TestMethod {


    private PsiMethod sutMethod;

    private PsiDocTag shouldTag;

    private String description;

    private TestClass parent;
    private PsiMethod backingMethod;

    public TestMethodImpl(PsiDocTag shouldTag, TestClass parent) {
        this.shouldTag = shouldTag;


        //  obtener el metodo a partir del docTag
        resolveSutMethod(shouldTag);
        //  initialize the description
        initShouldTagDescription(shouldTag);

        //  bind the current test parent...
        // TODO get this using the shouldTag, or investigate it better
        this.parent = parent;
        PsiMethod backingMethod = resolveBackingMethod(parent);
        this.backingMethod = backingMethod;


    }

    private PsiMethod resolveBackingMethod(TestClass parent) {
        //  resolve (find) backing test method in test class
        String nombreMetodoDePrueba = BddUtil.generateTestMethodName(sutMethod.getName(), description);
        PsiClass parentBackingClass = null;
        PsiMethod backingMethod = null;
        if (parent != null && null != (parentBackingClass = parent.getBackingClass())) {
            PsiMethod[] byNameMethods = parentBackingClass.findMethodsByName(nombreMetodoDePrueba, false);
            if (byNameMethods.length > 0) {
           backingMethod      = byNameMethods[0];
            }
        }
        return backingMethod;
    }


    private void resolveSutMethod(PsiDocTag shouldTag) {
        PsiMethod method = (PsiMethod) shouldTag.getParent().getContext();
        this.sutMethod = method;
    }

    private void initShouldTagDescription(PsiDocTag shouldTag) {
        final StringBuilder description = new StringBuilder();

        PsiElement[] dataElements = shouldTag.getDataElements();
        boolean isFirst = true;
        for (PsiElement dataElement : dataElements) {
            description.append(dataElement.getText());
            // TODO get the description taking into account the whitespaces
            if (isFirst) {
                description.append(" ");
            }
            isFirst = false;
        }

        this.description = description.toString().trim();
    }

    public boolean reallyExists() {
        return(null != resolveBackingMethod(parent)) ?true:false;

    }

    public void create() {

    }

    private boolean existsInSut;


    public String getDescription() {
        return description;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PsiMethod getSutMethod() {
        return this.sutMethod;
    }

    public PsiMethod getBackingMethod() {
        return resolveBackingMethod(parent);
    }
}
