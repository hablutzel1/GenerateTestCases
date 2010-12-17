package com.intellij.generatetestcases.testframework;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.testIntegration.TestFrameworkDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

/**
 * It  encapsulates the strategy to generate backing test methods and test classes
 * <p/>
 * User: Jaime Hablutzel
 */
public interface TestFrameworkStrategy {


    /**
     * This strategy will generate a PsiMethod that will back up the {@link com.intellij.generatetestcases.TestMethod}
     *
     * @param testClass
     * @param sutMethod
     * @param testDescription @return
     */
    @NotNull PsiMethod createBackingTestMethod(@NotNull PsiClass testClass, @NotNull PsiMethod sutMethod, @NotNull String testDescription);



    @Nullable PsiMethod findBackingTestMethod(@NotNull PsiClass testClass, @NotNull PsiMethod sutMethod, @NotNull String testDescription);

    /**
     * It will return the test framework descriptor for the specified test framework, this descriptor will give us information
     * like this:
     * 
     *
     *
     * @return
     */
    TestFrameworkDescriptor getTestFrameworkDescriptor();

}
