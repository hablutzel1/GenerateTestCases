package com.intellij.generatetestcases.testframework;

import com.intellij.generatetestcases.model.BDDCore;
import com.intellij.generatetestcases.model.TestClass;
import com.intellij.generatetestcases.model.TestMethod;
import com.intellij.generatetestcases.test.BaseTests;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.junit.Test;

import java.util.List;

import static com.intellij.generatetestcases.model.TestMethodTest.findTestMethodInCollection;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: JHABLUTZEL
 * Date: Dec 20, 2010
 * Time: 11:04:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class JUnit3StrategyTest extends BaseTests {
    /**
     * @verifies add junit 3 specific imports
     * @see JUnit3Strategy#createBackingTestMethod(com.intellij.psi.PsiClass, com.intellij.psi.PsiMethod, String)
     */
    @Test
    public void testCreateBackingTestMethod_shouldAddJunit3SpecificImports() throws Exception {

        //  create test method
        PsiClass aClass = createSutClass();
        TestClass testClass = BDDCore.createTestClass(aClass, new JUnit3Strategy(myProject));

        //  get unitialized test method
        List<TestMethod> allTestMethods = testClass.getAllMethods();
        TestMethod testMethod = findTestMethodInCollection(allTestMethods, "fetch user with given userId", "getUser");

        //  actually create
        testMethod.create();

        //  assert the right imports exist
        PsiClass psiClass = testMethod.getBackingElement().getContainingClass();
        assertThat(BddUtil.findImportsInClass(psiClass, "junit.framework.Assert").size(), is(1));

    }

    /**
     * @verifies create a test class that extends TestCase
     * @see TestFrameworkStrategy#createBackingTestClass(com.intellij.psi.PsiClass, com.intellij.psi.PsiDirectory)
     */
    @Test
    public void testCreateBackingTestClass_shouldCreateATestClassThatExtendsTestCase() throws Exception {

        // create sut class
        PsiClass psiClass = createSutClass();
        // create test class
        PsiClass backingTestClass = new JUnit3Strategy(myProject).createBackingTestClass(psiClass, null);

        //  assert existence of extends clause
        PsiClassType[] referencedTypes = backingTestClass.getExtendsList().getReferencedTypes();
        assertThat("number of extends for test class", referencedTypes.length, is(1));
        String firstExtend = ((PsiClassReferenceType) referencedTypes[0]).getReference().getCanonicalText();
        assertThat(firstExtend, is("junit.framework.TestCase"));

    }
}
