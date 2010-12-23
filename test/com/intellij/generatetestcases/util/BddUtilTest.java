package com.intellij.generatetestcases.util;


import com.intellij.generatetestcases.test.ExpectExceptionsExecutor;
import com.intellij.generatetestcases.test.ExpectExceptionsTemplate;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.testFramework.IdeaTestCase;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

// important :)
//@RunWith(JUnit4.class)

/**
 * TODO find out how to extends Junit 3 test class and run it with Junit 4 annotatiosn
 * temporarily methods names has been changed to include the test word at the beginnig
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
     * @see BddUtil#generateTestMethodNameForJUNIT4(String,String)
     */
    @Test
    public void testGenerateTestMethodName_shouldCreateAAppropiateNameForTheTestMethod()
            throws Exception {

        String methodName = "generateTestMethodName";
        String description = "create a appropiate name for the test method";
        String testMethodName = BddUtil.generateTestMethodNameForJUNIT4(methodName, description);
        assertEquals("generateTestMethodName_shouldCreateAAppropiateNameForTheTestMethod", testMethodName);

    }

    /**
     * @verifies fail if wrong args
     * @see BddUtil#generateTestMethodNameForJUNIT4(String,String)
     */
    @Test
    public void testGenerateTestMethodName_shouldFailIfWrongArgs() throws Exception {

        ExpectExceptionsExecutor.execute(new ExpectExceptionsTemplate() {
            public Class getExpectedException() {
                return IllegalArgumentException.class;
            }

            public void doInttemplate() {
                BddUtil.generateTestMethodNameForJUNIT4("", "");
            }
        });

        ExpectExceptionsExecutor.execute(new ExpectExceptionsTemplate() {
            public Class getExpectedException() {
                return IllegalArgumentException.class;
            }

            public void doInttemplate() {
                BddUtil.generateTestMethodNameForJUNIT4(null, null);
            }
        });


    }

    /**
     * @verifies return psi element pairs for start element and end element in each line for each should tag
     * @see BddUtil#getElementPairsInDocTag(com.intellij.psi.javadoc.PsiDocTag)
     */
    public void testGetElementPairsInDocTag_shouldReturnPsiElementPairsForStartElementAndEndElementInEachLineForEachShouldTag() throws Exception {

        // TODO create javadoc templates
        // TODO process each of them

        // TODO assert  by elements

//        Case 1

        String docCommentText = "/**\n" +
                "     * @should foo\n" +
                "     */";

        int shouldTagIndex = 0;
        int[][] matchings = {{0, 2, 2}};
        Project project = myProject;


        assertForOneShouldTag(docCommentText, shouldTagIndex, matchings, project);


        // case 2
//        PsiDocComment case2DocComment = elementFactory.createDocCommentFromText("/**\n" +
//                "     * @should foo yoo zas\n" +
//                "     */", null);
//        PsiDocTag case2FirstTag = case2DocComment.getTags()[0];
//
//        List<BddUtil.DocOffsetPair> case2Matches = BddUtil.getElementPairsInDocTag(case2FirstTag);
//        assertThat(case2Matches.get(0).getStart(), is(case2FirstTag.getChildren()[2]));
//        assertThat(case2Matches.get(0).getEnd(), is(case2FirstTag.getChildren()[4]));


        //TODO auto-generated
        Assert.fail("Not yet implemented");
    }

    private void assertForOneShouldTag(String docCommentText, int shouldTagIndex, int[][] matchings, Project project) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        PsiDocComment case1DocComment = elementFactory.createDocCommentFromText(docCommentText, null);
        PsiDocTag[] docCommentTags = case1DocComment.getTags();
        PsiDocTag case1FirstTag = docCommentTags[shouldTagIndex];
        //  get doctag elements
        List<BddUtil.DocOffsetPair> case1Matches = BddUtil.getElementPairsInDocTag(case1FirstTag);
        for (int[] matching : matchings) {
            assertThat(case1Matches.get(matching[0]).getStart(), is(case1FirstTag.getChildren()[matching[1]]));
            assertThat(case1Matches.get(matching[0]).getEnd(), is(case1FirstTag.getChildren()[matching[2]]));
        }
    }
}