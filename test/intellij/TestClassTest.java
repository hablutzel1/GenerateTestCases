package intellij;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import intellij.impl.TestClassImpl;
import intellij.test.BaseTests;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

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
        TestClassImpl testClass = new TestClassImpl(aClass);

        //  expect reallyExists return false
        assertThat(testClass.reallyExists(), is(false));


        //  create a testclass from PsiClass with corresponding PsiClass.getName() + "Test" in classpatha
        PsiClass sutClass = createSutClass(project);
        createTestClassForSut(project);


        TestClassImpl testClass1 = new TestClassImpl(sutClass);

        //  expect reallyExists return true
        assertThat(testClass1.reallyExists(), is(true));


    }

    /**
     * @verifies return a psiClass if this really exists, null otherwise
     * @see TestClass#getBackingClass()
     */
    @Test
    public void testgetBackingClass_shouldReturnAPsiClassIfThisReallyExistsNullOtherwise()
            throws Exception {

        String text = "package com.example;  public interface Zas {}";
        PsiClass aClass = createClassFromTextInPackage(myProject, text, "Zas", comExamplePackage);

        //  instantiate TestClassImpl
        TestClassImpl testClass = new TestClassImpl(aClass);

        //  expect reallyExists return false
        assertThat(testClass.reallyExists(), is(false));
        assertThat(testClass.getBackingClass(), nullValue());


        //  create a testclass from PsiClass with corresponding PsiClass.getName() + "Test" in classpatha
        PsiClass sutClass = createSutClass(myProject);
        createTestClassForSut(myProject);


        TestClassImpl testClass1 = new TestClassImpl(sutClass);

        //  expect reallyExists return true
        assertThat(testClass1.reallyExists(), is(true));

        assertThat(testClass1.getBackingClass(), not(nullValue()));


    }

    /**
     * @verifies create the new java test class in the same directory that the origin class if testRoot is null, in the specified test root if not null
     * @see TestClass#create(com.intellij.psi.PsiDirectory)
     */
    @Test
    public void testcreate_shouldCreateTheNewJavaTestClassInTheSameDirectoryThatTheOriginClassIfTestRootIsNullInTheSpecifiedTestRootIfNotNull()
            throws Exception {

        //  get a psi class without corresponding test class
        String text = "package com.example;  public interface Doo {}";
        PsiClass aClass = createClassFromTextInPackage(myProject, text, "Doo", comExamplePackage);

//         PsiClass foo = JavaDirectoryService.getInstance().createClass(comExamplePackage, "Ipsumad", "Class");

        //  create test class
        TestClass testClass = BDDCore.createTestClass(myProject, aClass);

        //  call create
        testClass.create(null);


        // TODO get source root for sut
        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(myProject);
        VirtualFile[] contentSourceRoots = projectRootManager.getContentSourceRoots();
        PsiJavaFile javaFile = (PsiJavaFile) aClass.getContainingFile();
//        contentSourceRoots[0].s
        //PsiFileFactory.getInstance(myProject).
//        javaFile.
        VirtualFile file = javaFile.getVirtualFile();
//        VirtualFile root = contentSourceRoots[0];
        //  convert this virtualFile to source root (PsiDirectory)
//        PsiManager psiManager = PsiManager.getInstance(myProject);
  //      PsiDirectory psiDirectory = psiManager.findDirectory(root);
        // TODO rewrote BaseTests.createClassFromTextInPackage() to use JavaDirectoryService


        // TODO get backing class for test, assert its source root equals to the sut

        // TODO get a psiClass without corresponding test class

        // TODO create it in specified source test root
        // TODO create or get a source test root

        // TODO get backing test method and assert its location

        //TODO auto-generated
        Assert.fail("Not yet implemented");
    }
}