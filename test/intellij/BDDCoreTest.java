package intellij;


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
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.*;

//@RunWith(JUnit4.class)

public class BDDCoreTest extends PsiTestCase {


    protected void setUp() throws Exception {
        super.setUp();

        ApplicationManager.getApplication().runWriteAction(
                new Runnable() {
                    public void run() {
                        try {
                            // TODO obtener ruta de proyecto de pruebas
//                 final File testRoot = new File(PluginPathManager.getPluginHomePath("eclipse") + "/testData", "project1");
//               String rootPath = JavaTestUtil.getJavaTestDataPath() + "/psi/constantValues";

                            VirtualFile root = PsiTestUtil.createTestProjectStructure("src", myModule, null, myFilesToDelete, true);
//               ModuleRootManager rootManager = ModuleRootManager.getInstance(myModule);
//               ModifiableRootModel rootModel = rootManager.getModifiableModel();
//               Library lib = rootModel.getModuleLibraryTable().createLibrary("test");
//               Library.ModifiableModel libModel = lib.getModifiableModel();
//               libModel.addRoot(root, OrderRootType.CLASSES);
//               libModel.commit();
//               rootModel.commit();
                        }
                        catch (Exception e) {
                            LOG.error(e);
                        }
                    }
                }
        );
    }


    /**
     * @verifies create a new test class with test methods unitialized
     * @see BDDCore#createTestClass(com.intellij.openapi.project.Project,com.intellij.psi.PsiClass,com.intellij.psi.PsiDirectory)
     */
//	@Test
    public void testCreateTestClass_shouldCreateANewTestClassWithTestMethodsUnitialized()
            throws Exception {


        //  get project
        Project project = getProject();

        //  create PsiClass with two methods, and some @should annotations
//        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);


        String text = "public interface Foo {\n" +
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

        FileType type = StdFileTypes.JAVA;
        PsiJavaFile javaFile = (PsiJavaFile) PsiFileFactory.getInstance(project).createFileFromText(type, "Foo.java", text, 0, text.length());
        final PsiClass[] classes = javaFile.getClasses();

        final PsiClass createdClass = classes[0];

        //  create or get source root
        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
        VirtualFile[] contentSourceRoots = projectRootManager.getContentSourceRoots();
        VirtualFile root = contentSourceRoots[0];
        //  convert this virtualFile to source root (PsiDirectory)
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiDirectory psiDirectory = psiManager.findDirectory(root);

        //  create a package: com.example
        final PsiDirectory myPackage = DirectoryUtil.createSubdirectories("com.example", psiDirectory, ".");

        //  put class in package and ensure it is tehere
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                JavaDirectoryService.getInstance().checkCreateInterface(myPackage, "Foo");
                PsiFile containingFile = createdClass.getContainingFile();
                containingFile.setName("Foo.java");
                myPackage.add(containingFile);
            }
        });


        //  create class there
        TestClass testClass = BDDCore.createTestClass(project, createdClass, myPackage);

        //  verificar que el retorno sea valido
        assertThat(" test class returned " , testClass, notNullValue());

//        PsiFile testFile = myPackage.findFile("FooTest.java");
//        assertThat(testFile, is(not(null)));


        //  verificar la cantidad y el estado de los TestMethod's esperados
        assertThat(testClass.getAllMethods().size(), is(4));

        List<TestMethod> methods = testClass.getAllMethods();
        for (TestMethod method : methods) {
            assertThat(method.reallyExists(), is(false));
        }

    }


	/**
	 * @see BDDCore#createTestClass(Project,PsiClass,PsiDirectory)
	 * @verifies return a test class that already exists for a sut class with some test methods initialized
	 */
	@Test
	public void testCreateTestClass_shouldReturnATestClassThatAlreadyExistsForASutClassWithSomeTestMethodsInitialized()
			throws Exception {

        // TODO create test class

		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}


}