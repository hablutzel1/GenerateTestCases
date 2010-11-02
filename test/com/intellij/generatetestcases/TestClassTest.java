package com.intellij.generatetestcases;


import com.intellij.generatetestcases.impl.TestClassImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.generatetestcases.test.BaseTests;
import org.junit.Test;

import java.io.File;

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
            String text = "package com.example;  public interface Yola {}";
            PsiClass aClass = createClassFromTextInPackage(myProject, text, "Yola", comExamplePackage);


            //  create or get a source test root
            File dir = FileUtil.createTempDirectory("test", null);
            myFilesToDelete.add(dir);
            VirtualFile vDir = LocalFileSystem.getInstance().refreshAndFindFileByPath(dir.getCanonicalPath().replace(File.separatorChar, '/'));
            final ModuleRootManager rootManager = ModuleRootManager.getInstance(myModule);
            final ModifiableRootModel rootModel = rootManager.getModifiableModel();
            final ContentEntry contentEntry = rootModel.addContentEntry(vDir);
            contentEntry.addSourceFolder(vDir, false);
            rootModel.commit();
            //  create it in specified source test root
            PsiManager psiManager = PsiManager.getInstance(myProject);
            PsiDirectory psiDirectory = psiManager.findDirectory(vDir);

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
    }

    private PsiDirectory getContentSourceRoot(PsiJavaFile javaFile) {
        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(myProject);
        VirtualFile[] contentSourceRoots = projectRootManager.getContentSourceRoots();
        PsiDirectory returnContentSourceRoot = null;
        for (VirtualFile contentSourceRoot : contentSourceRoots) {
            PsiElement foo = null;
            PsiDirectory parent = javaFile.getParent();

            do {
                if (parent != null && parent instanceof PsiDirectory) {
                    PsiDirectory zas = parent;
                    if (contentSourceRoot.equals(zas.getVirtualFile())) {
                        returnContentSourceRoot = zas;
                    }
                }
            }
            while (parent != null && null != (parent = parent.getParent()));


        }
        return returnContentSourceRoot;
    }
}