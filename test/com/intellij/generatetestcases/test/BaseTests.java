package com.intellij.generatetestcases.test;

import com.intellij.generatetestcases.TestMethod;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.JavaSdkImpl;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.testFramework.PsiTestCase;
import com.intellij.testFramework.PsiTestUtil;
import org.junit.Ignore;

import java.io.File;
import java.util.List;

/**
 * User: Jaime Hablutzel
 */
@Ignore
public class BaseTests extends PsiTestCase {

    protected PsiDirectory comExamplePackage;
    protected PsiDirectory defaultSourcePackageRoot;

    protected boolean isAddJunit4Library() {
        return true;
    }

//    private PsiClass javaTestClass;
//    public PsiDirectory sourceRootDirectory;


    @Override
    protected Sdk getTestProjectJdk() {
        /// add jdk
        //  TODO find better way to override
//        File mockJdkCEPath = new File(getPluginHomePath(), "java/mockJDK");
//        return createMockJdk(mockJdkCEPath.getPath(), "java 1.4",
//                ApplicationManager.getApplication().getComponent(JavaSdk.class));
        return JavaSdkImpl.getMockJdk14();
    }


    protected void setUp() throws Exception {
        super.setUp();

        //  get project
//        final Project project = getProject();
        //configure("", "");
        ApplicationManager.getApplication().runWriteAction(
                new Runnable() {


                    public void run() {
                        try {
                            //  obtener ruta de proyecto de pruebas
                            VirtualFile structure = PsiTestUtil.createTestProjectStructure("src", myModule, null, myFilesToDelete, true);
                            String packageName = "com.example";
                            defaultSourcePackageRoot = myPsiManager.findDirectory(structure);
                            //PsiDirectory sourcePackageRoot = getFirstSourcePackageRoot(project);
//                            BaseTests.this.sourceRootDirectory = sourcePackageRoot;
                            comExamplePackage = TestUtil.createPackageInSourceRoot(packageName, defaultSourcePackageRoot);

                            if (isAddJunit4Library()) {
                                addJunit4LibraryToMockProject();
                            }


                        }
                        catch (Exception e) {
                            LOG.error(e);
                        }


                    }
                }
        );


    }

    protected void addJunit4LibraryToMockProject() {
        //  add junit to classpath
        //  add home path
        String path = TestUtil
                .getPluginHomePath();
        String jarName = "junit-4.7.jar";
        String junitLibraryPath = path + File.separatorChar + "testData" + File.separatorChar + "lib" + File.separatorChar;
        PsiTestUtil.addLibrary(myModule, "Junit", junitLibraryPath, jarName);

    }




    /**
     * Creates the fixture sut class and returns a psi element for it
     *
     * @return
     */
    protected PsiClass createSutClass() {
        //  create PsiClass with two methods, and some @should annotations
        String text = "package com.example;  public interface Foo  {\n" +
                "\n" +
                "\t/**\n" +
                "\t * Get user by internal user identifier.\n" +
                "\t * \n" +
                "\t * @param userId internal identifier\n" +
                "\t * @return requested user\n" +
                "\t * @throws APIException\n" +
                "\t * @should fetch user with given userId\n" +
                "\t */\n" +
                "\tpublic String getUser(Integer userId);\n" +
                "\n" +
                "\n" +
                "\t/**\n" +
                "\t * Get user by the given uuid.\n" +
                "\t * \n" +
                "\t * @param uuid\n" +
                "\t * @return\n" +
                "\t * @throws APIException\n" +
                "\t * @should fetch user with given uuid\n" +
                "\t * @should find object given valid uuid\n" +
                "\t * @should return null if no object found with given uuid\n" +
                "\t */\n" +
                "\tpublic String getUserByUuid(String uuid);\n" +
                "\n" +
                "}";

        final String className = "Foo";

        return createClassFromTextInPackage(myProject, text, className, comExamplePackage);
    }

    protected PsiClass createTestClassForSut() {
        //  create test class
        String testClass = "package com.example; import org.junit.Assert;\n" +
                "import org.junit.Test;\n" +
                "\n" +
                "public class FooTest {\n" +
                "\t/**\n" +
                "\t * @see Foo#getUser(Integer)\n" +
                "\t * @verifies fetch user with given userId\n" +
                "\t */\n" +
                "\t@Test\n" +
                "\tpublic void getUser_shouldFetchUserWithGivenUserId() throws Exception {\n" +
                "\t\t//TODO auto-generated\n" +
                "\t\tAssert.fail(\"Not yet implemented\");\n" +
                "\t}\n" +
                "\n" +
                "\t/**\n" +
                "\t * @see Foo#getUserByUuid(String)\n" +
                "\t * @verifies fetch user with given uuid\n" +
                "\t */\n" +
                "\t@Test\n" +
                "\tpublic void getUserByUuid_shouldFetchUserWithGivenUuid() throws Exception {\n" +
                "\t\t//TODO auto-generated\n" +
                "\t\tAssert.fail(\"Not yet implemented\");\n" +
                "\t}\n" +
                "}";

        //  create test class in the same package
        final String className = "FooTest";
        return createClassFromTextInPackage(myProject, testClass, className, comExamplePackage);
    }

    protected PsiClass createClassFromTextInPackage(final Project project, final String text, final String className, final PsiDirectory inPackage) {


        //  create a package: com.example
        final PsiJavaFile[] javaFile = {null};
        //  put class in package and ensure it is tehere
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {

                //  change it to create classes using JavaDirectoryService   strategy
                FileType type = StdFileTypes.JAVA;
                javaFile[0] = (PsiJavaFile) PsiFileFactory.getInstance(project).createFileFromText(type, className + ".java", text, 0, text.length());

                String fileName = className + ".java";

                JavaDirectoryService.getInstance().checkCreateInterface(inPackage, className);
                javaFile[0] = (PsiJavaFile) javaFile[0].setName(fileName);
                javaFile[0] = (PsiJavaFile) inPackage.add(javaFile[0]);

            }
        });
        return javaFile[0].getClasses()[0];
    }

    protected boolean existsDescriptionInTestMethodsList(String expectedDescription, List<TestMethod> methods) {
        boolean exists = false;
        for (TestMethod method : methods) {
            if (method.getDescription().equals(expectedDescription)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    protected boolean existsReallyInitializedTestMethodInCollection(List<TestMethod> allTestMethods, String methodName) {
        boolean exists = false;
        for (TestMethod testMethod : allTestMethods) {
            if (testMethod.getBackingElement() != null && testMethod.getBackingElement().getName().equals(methodName)) {
                if (testMethod.reallyExists() == true) {
                    exists = true;
                }
            }
        }
        return exists;
    }

    protected PsiDirectory getContentSourceRoot(PsiJavaFile javaFile) {
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
