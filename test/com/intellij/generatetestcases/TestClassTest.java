package com.intellij.generatetestcases;


import com.intellij.generatetestcases.impl.TestClassImpl;
import com.intellij.generatetestcases.test.BaseTests;
import com.intellij.generatetestcases.test.TestUtil;
import com.intellij.generatetestcases.testframework.JUnit4Strategy;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiManagerImpl;
import org.junit.Assert;
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
        Project project = getProject();

        //  create a testclass from a PsiClass with no corresponding PsiClass.getName()  +  "Test" in classpath

        //  create psi class
        String text = "package com.example;  public interface Zas {}";
        PsiClass aClass = createClassFromTextInPackage(project, text, "Zas", comExamplePackage);

        //  instantiate TestClassImpl
        TestClassImpl testClass = new TestClassImpl(aClass, new JUnit4Strategy());

        //  expect reallyExists return false
        assertThat(testClass.reallyExists(), is(false));


        //  create a testclass from PsiClass with corresponding PsiClass.getName() + "Test" in classpatha
        PsiClass sutClass = createSutClass(project);
        createTestClassForSut(project);


        TestClassImpl testClass1 = new TestClassImpl(sutClass, new JUnit4Strategy());

        //  expect reallyExists return true
        assertThat(testClass1.reallyExists(), is(true));


    }

    /**
     * @verifies return a psiClass if this really exists, null otherwise
     * @see TestClass#getBackingClass()
     */
    @Test
    public void testGetBackingClass_shouldReturnAPsiClassIfThisReallyExistsNullOtherwise()
            throws Exception {

        String text = "package com.example;  public interface Zas {}";
        PsiClass aClass = createClassFromTextInPackage(myProject, text, "Zas", comExamplePackage);

        //  instantiate TestClassImpl
        TestClassImpl testClass = new TestClassImpl(aClass, new JUnit4Strategy());

        //  expect reallyExists return false
        assertThat(testClass.reallyExists(), is(false));
        assertThat(testClass.getBackingClass(), nullValue());


        //  create a testclass from PsiClass with corresponding PsiClass.getName() + "Test" in classpatha
        PsiClass sutClass = createSutClass(myProject);
        createTestClassForSut(myProject);


        TestClassImpl testClass1 = new TestClassImpl(sutClass, new JUnit4Strategy());

        //  expect reallyExists return true
        assertThat(testClass1.reallyExists(), is(true));

        assertThat(testClass1.getBackingClass(), not(nullValue()));


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
            TestClass testClass = BDDCore.createTestClass(myProject, aClass);

            //  call create
            testClass.create(null);


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

            String sourceRootName = "test";
            Module module = myModule;
            Collection<File> filesToDelete = myFilesToDelete;
            PsiManagerImpl myPsiManager = this.myPsiManager;

            PsiDirectory psiDirectory = TestUtil.createSourceRoot(sourceRootName, module, filesToDelete, myPsiManager);

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
     * @verifies create the backing test class in the same package than the sut class
     * @see TestClass#create(com.intellij.psi.PsiDirectory)
     */
    @Test
    public void testCreate_shouldCreateTheBackingTestClassInTheSamePackageThanTheSutClass() throws Exception {

        // TODO create test in some package

        // TODO verify sucess

        // TODO create test in the default package

        // TODO verify sucess


          // TODO allow to test classes not in any package
        fail();
        //TODO auto-generated
        Assert.fail("Not yet implemented");
    }
}