package com.intellij.generatetestcases.testframework;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

/**
 * this will compute the right TestFramework Model, or a visitor for test class instead of model...
 * It should encapsulate the strategy to generate test methods and test classes
 * TODO find out how it will generate test classes names, will the generation of test classes require another
 * strategy??
 * <p/>
 * User: Jaime Hablutzel
 */
public interface TestFrameworkStrategy {


    /**
     * This strategy will generate a PsiMethod that will back up the {@link com.intellij.generatetestcases.TestMethod}
     *
     * By definition an strategy will need all the required information from the its client, research what information
     * it will need.
     *
     * @param testClass
     *@param sutMethod
     * @param testDescription   @return
     */
    PsiMethod createBackingTestMethod(PsiClass testClass, PsiMethod sutMethod, String testDescription);

    PsiMethod resolveBackingTestMethod(@NotNull PsiClass testClass, PsiMethod sutMethod, @NotNull String testDescription);
}
