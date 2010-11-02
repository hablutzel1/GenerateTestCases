package com.intellij.generatetestcases.test;

import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.testFramework.PsiTestCase;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.generatetestcases.TestMethod;
import org.junit.Ignore;

import java.util.List;

/**
 * User: Jaime Hablutzel
 */
@Ignore
public class BaseTests extends PsiTestCase {
    protected PsiDirectory comExamplePackage;
    protected PsiClass sutClass;
    private PsiClass javaTestClass;
    public PsiDirectory sourceRootDirectory;

    protected void setUp() throws Exception {
        super.setUp();

        //  get project
        final Project project = getProject();
        ApplicationManager.getApplication().runWriteAction(
                new Runnable() {


                    public void run() {
                        try {
                            //  obtener ruta de proyecto de pruebas
                            VirtualFile root = PsiTestUtil.createTestProjectStructure("src", myModule, null, myFilesToDelete, true);
                            String containingPackage = "com.example";
                            PsiDirectory psiDirectory = getSourcePackageRoot(project);
                            BaseTests.this.sourceRootDirectory = psiDirectory;
                            comExamplePackage = DirectoryUtil.createSubdirectories(containingPackage, psiDirectory, ".");
                        }
                        catch (Exception e) {
                            LOG.error(e);
                        }
                    }
                }
        );


    }

    /**
     * Creates the fixture sut class and returns a psi element for it
     *
     * @param project
     * @return
     */
    protected PsiClass createSutClass(Project project) {
        //  create PsiClass with two methods, and some @should annotations
        String text = "package com.example;  public interface Foo {\n" +
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
        this.sutClass = createClassFromTextInPackage(project, text, className, comExamplePackage);
        return sutClass;
    }

    protected void createTestClassForSut(Project project) {
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
        this.javaTestClass = createClassFromTextInPackage(project, testClass, className, comExamplePackage);
    }

    private PsiDirectory getSourcePackageRoot(Project project) {
        //  create or get source root
        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
        VirtualFile[] contentSourceRoots = projectRootManager.getContentSourceRoots();
        VirtualFile root = contentSourceRoots[0];
        //  convert this virtualFile to source root (PsiDirectory)
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiDirectory psiDirectory = psiManager.findDirectory(root);
        return psiDirectory;
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
                final PsiClass[] classes = javaFile[0].getClasses();
//                final PsiClass createdClass = classes[0];
                String fileName  = className + ".java";

                JavaDirectoryService.getInstance().checkCreateInterface(inPackage, className);
                javaFile[0] = (PsiJavaFile) javaFile[0].setName(fileName);
//                PsiFile containingFile = createdClass.getContainingFile();
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
            if (testMethod.getBackingMethod() != null && testMethod.getBackingMethod().getName().equals(methodName)) {
                if (testMethod.reallyExists() == true) {
                    exists = true;
                }
            }
        }
        return exists;
    }
}
