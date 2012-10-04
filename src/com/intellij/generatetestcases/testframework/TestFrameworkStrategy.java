package com.intellij.generatetestcases.testframework;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * It  encapsulates the strategy to generate backing test methods and test classes
 * <p/>
 * User: Jaime Hablutzel
 */
public interface TestFrameworkStrategy {


    String TEST_CLASS_SUFFIX = "Test";

    /**
     * This strategy will generate a PsiMethod that will back up the {@link com.intellij.generatetestcases.model.TestMethod}
     *
     * @param testClass
     * @param sutMethod
     * @param testDescription @return
     */
    @NotNull PsiMethod createBackingTestMethod(@NotNull PsiClass testClass, @NotNull PsiMethod sutMethod, @NotNull String testDescription);


    /**
     *
     * @param sutClass
     * @param sourceRoot
     * @return
     */
    PsiClass createBackingTestClass(PsiClass sutClass, PsiDirectory sourceRoot);



    @Nullable PsiMethod findBackingTestMethod(@NotNull PsiClass testClass, @NotNull PsiMethod sutMethod, @NotNull String testDescription);

    /**
     * This method can delegate to com.intellij.testIntegration.TestFramework#isLibraryAttached(com.intellij.openapi.module.Module)
     * or it can make by its own the required verification to determine if the library for the module is available,
     *
     * This operation will be helpful to clients to determine if they should add the required library, before beginning to
     * make use of create operations that could result in broken references if the required libraries aren't available 
     *
     * @return
     * @param module
     */
    boolean isTestFrameworkLibraryAvailable(Module module);


    /**
     *
     * @param sutClass
     * @return
     * @should ignore anonymous classes 
     */
    PsiClass findBackingPsiClass(PsiClass sutClass);

    @NotNull
    String getExpectedNameForThisTestMethod(@NotNull String sutMethodName, @NotNull String description);

    /**
     * Returns the class name that the test class would have if it would be generated.
     *
     * @param sutClass
     * @return
     */
    String getCandidateTestClassName(PsiClass sutClass);
}
