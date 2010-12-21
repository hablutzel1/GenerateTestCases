package com.intellij.generatetestcases;


import com.intellij.generatetestcases.impl.TestClassImpl;
import com.intellij.generatetestcases.test.BaseTests;
import com.intellij.generatetestcases.test.TestUtil;
import com.intellij.generatetestcases.testframework.JUnit4Strategy;
import com.intellij.openapi.module.Module;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiManagerImpl;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.*;

public class TestClassTest extends BaseTests {


    /**
     * @verifies return true only if there is a class in the classpath with the name that this class should have
     * @see TestClass#reallyExists()
     */
    @Test
    public void testReallyExists_shouldReturnTrueOnlyIfThereIsAClassInTheClasspathWithTheNameThatThisClassShouldHave()
            throws Exception {
//        Project project = getProject();

        //  create a testclass from a PsiClass with no corresponding PsiClass.getName()  +  "Test" in classpath

        PsiClass sutClass = createSutClass();
        //  create psi class
//        String text = "package com.example;  public interface Zas {}";
//        PsiClass aClass = createClassFromTextInPackage(project, text, "Zas", comExamplePackage);

        //  instantiate TestClassImpl
        TestClassImpl testClass = new TestClassImpl(sutClass, new JUnit4Strategy(myProject));

        //  expect reallyExists return false
        assertThat(testClass.reallyExists(), is(false));


        //  create a testclass from PsiClass with corresponding PsiClass.getName() + "Test" in classpatha

        createTestClassForSut(myProject);


        TestClassImpl testClass1 = new TestClassImpl(sutClass, new JUnit4Strategy(myProject));

        //  expect reallyExists return true
        assertThat(testClass1.reallyExists(), is(true));


    }

    /**
     * fix: should support looking for classes in the default package
     * TODO como probar una situacion inusual cuando se prueba el comporamiento de una operacion de una interfaz
     *
     * @verifies return a psiClass if this really exists, null otherwise
     * @see TestClass#getBackingClass()
     */
    @Test
    public void testGetBackingClass_shouldReturnAPsiClassIfThisReallyExistsNullOtherwise()
            throws Exception {
        PsiClass sutClass = createSutClass();
        //  instantiate TestClassImpl
        TestClassImpl testClass = new TestClassImpl(sutClass, new JUnit4Strategy(myProject));
        //  expect reallyExists return false
        assertThat(testClass.reallyExists(), is(false));
        assertThat(testClass.getBackingClass(), nullValue());
        //  create a testclass from PsiClass with corresponding PsiClass.getName() + "Test" in classpatha
        createTestClassForSut(myProject);
        TestClassImpl testClass1 = new TestClassImpl(sutClass, new JUnit4Strategy(myProject));
        //  expect reallyExists return true
        assertThat(testClass1.reallyExists(), is(true));
        assertThat(testClass1.getBackingClass(), not(nullValue()));


        PsiClass classInDefaultPackage = createClassFromTextInPackage(myProject, "  public interface Foo {\n" +
                "}", "Foo", defaultSourcePackageRoot);
        TestClass testClass2 = BDDCore.createTestClass(myProject, classInDefaultPackage);
        assertThat(testClass2.getBackingClass(), nullValue());
        PsiClass testClassInDefaultPackage = createClassFromTextInPackage(myProject, "  public class FooTest {\n" +
                "}", "FooTest", defaultSourcePackageRoot);
        assertThat(testClass2.getBackingClass(), not(nullValue()));
        assertThat(testClass2.getBackingClass(), is(testClassInDefaultPackage));

    }

    /**
     * @verifies create the new java test class in the same directory that the origin class if testRoot is null, in the specified test root if not null
     * @see TestClass#create(com.intellij.psi.PsiDirectory)
     */
    @Test
    public void testCreate_shouldCreateTheNewJavaTestClassInTheSameDirectoryThatTheOriginClassIfTestRootIsNullInTheSpecifiedTestRootIfNotNull()
            throws Exception {


        {

            //  get a psi class without corresponding test class
            String text = "package com.example;  public interface Doo {}";
            PsiClass aClass = createClassFromTextInPackage(myProject, text, "Doo", comExamplePackage);

            //  create test class
            TestClass testClass = triggerCreateTestClass(aClass, null);


            //  get source root for sut
            PsiJavaFile javaFile = (PsiJavaFile) aClass.getContainingFile();

            PsiDirectory sutContentSourceRoot = getContentSourceRoot(javaFile);

            //  get backing class for test, assert its source root equals to the sut

            PsiClass testBackingClass = testClass.getBackingClass();
            PsiFile containingFile = testBackingClass.getContainingFile();

            PsiDirectory testClassContentSourceRoot = getContentSourceRoot((PsiJavaFile) containingFile);
            assertThat(testClassContentSourceRoot, is(sutContentSourceRoot));
        }

        {
            //  get a psiClass without corresponding test class
            PsiDirectory packageName = comExamplePackage;

            String text = "package com.example;  public interface Yola {}";
            PsiClass aClass = createClassFromTextInPackage(myProject, text, "Yola", packageName);


            //  create or get a source test root

            PsiDirectory psiDirectory = TestUtil.createSourceRoot("test", myModule, myFilesToDelete, this.myPsiManager);

            //  create test class
            TestClass testClass = BDDCore.createTestClass(myProject, aClass);

            //  call create
            testClass.create(psiDirectory);

            //  get backing test method and assert its location

            PsiClass testBackingClass = testClass.getBackingClass();
            PsiFile containingFile = testBackingClass.getContainingFile();

            PsiDirectory testClassContentSourceRoot = getContentSourceRoot((PsiJavaFile) containingFile);
            assertThat(testClassContentSourceRoot, is(psiDirectory));


        }

        {
            //  create a source root
            String sourceRootName = "mysrc";

            PsiDirectory root = TestUtil.createSourceRoot(sourceRootName, myModule, myFilesToDelete, myPsiManager);

            //  create a package within it
            PsiDirectory peGobHndacPackage = TestUtil.createPackageInSourceRoot("pe.gob.hndac", root);

            //  create a sut class

            String text = "package pe.gob.hndac;  public interface A {}";
            PsiClass aClass = createClassFromTextInPackage(myProject, text, "A", peGobHndacPackage);

            //  create a test source root
            PsiDirectory testSR = TestUtil.createSourceRoot("mytest", myModule, myFilesToDelete, myPsiManager);

            //  create corresponding package
            PsiDirectory testPeGobHndacPackage = TestUtil.createPackageInSourceRoot("pe.gob.hndac", testSR);

            //  create class
            TestClass testClass = BDDCore.createTestClass(myProject, aClass);
            //  actually create
            testClass.create(testSR);

            PsiClass psiClass = testClass.getBackingClass();

            //  assert test file location
            PsiFile containingFile = psiClass.getContainingFile();
            assertThat(getContentSourceRoot((PsiJavaFile) containingFile), is(testSR));
            assertTrue("should only create destination package if it doesn't exists already", true);

        }


    }


    /**
     * FIX: When a class was being created in the default package specifying the source root
     * it was failing with null pointer exception
     *
     * @verifies create the backing test class in the same package than the sut class
     * @see TestClass#create(com.intellij.psi.PsiDirectory)
     */
    @Test
    public void testCreate_shouldCreateTheBackingTestClassInTheSamePackageThanTheSutClass() throws Exception {

        //  create test in some package
        PsiClass defaultSutClass = createSutClass();

        TestClass testClass = triggerCreateTestClass(defaultSutClass, null);

        //  verify sucess
        assertClassesAreInTheSamePackage(defaultSutClass, testClass.getBackingClass());

        // allow to test classes not in any package
        //  create test in the default package
        PsiClass psiClassWithNoPackage = createClassFromTextInPackage(myProject, "public interface B {}", "B", defaultSourcePackageRoot);
        TestClass testClass1 = triggerCreateTestClass(psiClassWithNoPackage, null);

        //  verify sucess
        assertClassesAreInTheSamePackage(psiClassWithNoPackage, testClass1.getBackingClass());


        testClass1.getBackingClass().delete();

        //  create source root
        PsiDirectory psiDirectory = TestUtil.createSourceRoot("test", myModule, myFilesToDelete, this.myPsiManager);

        testClass1 = triggerCreateTestClass(psiClassWithNoPackage, psiDirectory);

        //  verify sucess
        assertClassesAreInTheSamePackage(psiClassWithNoPackage, testClass1.getBackingClass());

    }

    private TestClass triggerCreateTestClass(PsiClass psiClass, PsiDirectory sourceRoot) {
        TestClass testClass = BDDCore.createTestClass(myProject, psiClass);
        testClass.create(sourceRoot);
        return testClass;
    }

    private void assertClassesAreInTheSamePackage(PsiClass psiClass, PsiClass testBackingClass) {
        String packageName = ((PsiJavaFile) testBackingClass.getContainingFile()).getPackageName();
        assertThat(packageName, is(((PsiJavaFile) psiClass.getContainingFile()).getPackageName()));
    }
}