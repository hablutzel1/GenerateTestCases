package com.intellij.generatetestcases.util;


import com.intellij.generatetestcases.test.ExpectExceptionsExecutor;
import com.intellij.generatetestcases.test.ExpectExceptionsTemplate;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.testFramework.IdeaTestCase;
import org.junit.Test;

// important :)
//@RunWith(JUnit4.class)

/**
 * TODO find out how to extends Junit 3 test class and run it with Junit 4 annotatiosn
 * temporarily methods names has been changed to include the test word at the beginnig
 *
 */
public class BddUtilTest extends IdeaTestCase {



//
//    @Before
//    @Override
//    public void setUp() throws Exception {
//        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
//    }
//
//    @After
//    @Override
//    public void tearDown() throws Exception {
//        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
//    }


    //
//    public static junit.framework.Test suite() {
//        return new JUnit4TestAdapter(BddUtilTest.class);
//    }
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
    public void testGenerateTestMethodName_shouldCreateAAppropiateNameForTheTestMethod()
            throws Exception {

        String methodName = "generateTestMethodName";
        String description = "create a appropiate name for the test method";
        String testMethodName = BddUtil.generateTestMethodName(methodName, description);
        assertEquals("generateTestMethodName_shouldCreateAAppropiateNameForTheTestMethod", testMethodName);

    }

    /**
     * @verifies fail if wrong args
     * @see BddUtil#generateTestMethodName(String,String)
     */
    @Test
    public void testGenerateTestMethodName_shouldFailIfWrongArgs() throws Exception {

        ExpectExceptionsExecutor.execute(new ExpectExceptionsTemplate() {
            public Class getExpectedException() {
                return IllegalArgumentException.class;
            }

            public void doInttemplate() {
                BddUtil.generateTestMethodName("", "");
            }
        });

        ExpectExceptionsExecutor.execute(new ExpectExceptionsTemplate() {
            public Class getExpectedException() {
                return IllegalArgumentException.class;
            }

            public void doInttemplate() {
                BddUtil.generateTestMethodName(null, null);
            }
        });


	}
}