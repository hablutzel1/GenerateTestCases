package intellij.util;


import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.testFramework.IdeaTestCase;
import com.intellij.testFramework.LightIdeaTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

// important :)
@RunWith(JUnit4.class)
public class BddUtilTest extends IdeaTestCase {

//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//        final File testRoot = new File(PluginPathManager.getPluginHomePath("GenerateTestCases") + "/testData", "eml");
//        assertTrue(testRoot.getAbsolutePath(), testRoot.isDirectory());
//
//        final File currentTestRoot = new File(testRoot, getTestName(true));
//        assertTrue(currentTestRoot.getAbsolutePath(), currentTestRoot.isDirectory());
//        FileUtil.copyDir(currentTestRoot, new File(getProject().getBaseDir().getPath()));
//    }

    /**
     * @verifies create a appropiate name for the test method
     * @see BddUtil#generateTestMethodName(String,String)
     */
    @Test
    public void generateTestMethodName_shouldCreateAAppropiateNameForTheTestMethod()
            throws Exception {

        String methodName = "generateTestMethodName";
        String description = "create a appropiate name for the test method";
        String testMethodName = BddUtil.generateTestMethodName(methodName, description);
        assertEquals("generateTestMethodName_shouldCreateAAppropiateNameForTheTestMethod", testMethodName);

	}

	/**
	 * @see BddUtil#generateTestMethodName(String,String)
	 * @verifies fail if wrong args
	 */
	@Test
	public void generateTestMethodName_shouldFailIfWrongArgs() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}
}