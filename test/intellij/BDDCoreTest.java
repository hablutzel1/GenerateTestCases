package intellij;


import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.testFramework.IdeaTestCase;
import com.intellij.testFramework.LightIdeaTestCase;
import com.intellij.testFramework.PsiTestCase;
import com.intellij.testFramework.PsiTestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

//@RunWith(JUnit4.class)

public class BDDCoreTest extends PsiTestCase {


    protected void setUp() throws Exception {
      super.setUp();

       ApplicationManager.getApplication().runWriteAction(
         new Runnable() {
           public void run() {
             try{
                 // TODO obtener ruta de proyecto de pruebas
                 final File testRoot = new File(PluginPathManager.getPluginHomePath("eclipse") + "/testData", "project1");
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
             catch(Exception e){
               LOG.error(e);
             }
           }
         }
       );
    }


    /**
     * @verifies create a new test class with test methods unitialized
     * @see BDDCore#createTestClass(Project,PsiClass)
     */
//	@Test
    public void testCreateTestClass_shouldCreateANewTestClassWithTestMethodsUnitialized()
            throws Exception {


        // TODO get project
        Project project = getProject();

        // TODO create PsiClass with two methods, and some @should annotations
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        PsiClass psiClass = elementFactory.createClassFromText("public class B {} ", null);
        VirtualFile baseDir = project.getBaseDir();

        // TODO create or get package source

        // TODO create a package: com.example

        // TODO create class there


        TestClass testClass = BDDCore.createTestClass(null, null);

        // TODO verificar que el retorno sea valido

        // TODO verificar la cantidad y el estado de los TestMethod's esperados


        //TODO auto-generated
        Assert.fail("Not yet implemented");
	}
}