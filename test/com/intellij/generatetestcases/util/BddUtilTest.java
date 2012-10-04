package com.intellij.generatetestcases.util;


import com.intellij.generatetestcases.test.ExpectExceptionsExecutor;
import com.intellij.generatetestcases.test.ExpectExceptionsTemplate;
import com.intellij.generatetestcases.testframework.JUnit4Strategy;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.testFramework.IdeaTestCase;

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
     * @verifies return psi element pairs for start element and end element in each line for each should tag
     * @see BddUtil#getElementPairsInDocTag(com.intellij.psi.javadoc.PsiDocTag)
     */
    public void testGetElementPairsInDocTag_shouldReturnPsiElementPairsForStartElementAndEndElementInEachLineForEachShouldTag() throws Exception {

//        Case 1
        assertForOneShouldTag("/**\n" +
                "     * @should foo\n" +
                "     */", 0, new int[][]{{0, 2, 2}}, myProject);

        // case 2
        assertForOneShouldTag("/**\n" +
                "     * @should foo yoo zas\n" +
                "     */", 0, new int[][]{{0, 2, 4}}, myProject);

        // case 3
        assertForOneShouldTag("/**\n" +
                "     * @should foo bar zas\n" +
                "     * yoo doo right\n" +
                "     * asgasdg asdfgasd\n" +
                "     */", 0, new int[][]{{0, 2, 4}, {1, 7, 7}, {2, 10, 10}}, myProject);

        // case 4
        assertForOneShouldTag("/**\n" +
                "     * @should foo\n" +
                "     * yoo\n" +
                "     */", 0, new int[][]{{0, 2, 2}, {1, 5, 5}}, myProject);

        // matches pair caught for 'yoo' description
        assertForOneShouldTag("/**\n" +
                "     * @should doo  \n" +
                "     * @should yoo\n" +
                "     * \n" +
                "     */"
                , 1, new int[][]{{0, 2, 2}}, myProject);


        // return only one pair for doo, ignore trailing space at the next line
        assertForOneShouldTag("/**\n" +
                "     * @should doo  \n" +
                "     * @should yoo\n" +
                "     * \n" +
                "     */"
                , 0, new int[][]{{0, 2, 2}}, myProject);

        // test when there are two @should's and the first contains two tokens
        assertForOneShouldTag("/**\n" +
                "     * @should foo bar\n" +
                "     * @should doo\n" +
                "     * \n" +
                "     */"
                , 0, new int[][]{{0, 2, 4}}, myProject);


        // test when there are two @should's and the first contains a semicolon
        assertForOneShouldTag("/**\n" +
                "     * @should a;\n" +
                "     * @should b\n" +
                "     * \n" +
                "     */"
                , 0, new int[][]{{0, 2, 3}}, myProject);

        // test when there are two @should's and the first contains a semicolon and more than one line
        assertForOneShouldTag("/**\n" +
                "     * @should a; b\n" +
                "     * c d\n" +
                "     * @should b\n" +
                "     * \n" +
                "     */"
                , 0, new int[][]{{0, 2, 3}, {1, 6, 6}}, myProject);


    }

    /**
     * @param docCommentText
     * @param shouldTagIndex
     * @param matchings      { {line, startElIdx, endElIdx}, {line, startElIdx, endElIdx}}
     * @param project
     */
    private void assertForOneShouldTag(String docCommentText, int shouldTagIndex, int[][] matchings, Project project) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        PsiDocComment docComment = elementFactory.createDocCommentFromText(docCommentText);
        PsiDocTag[] docCommentTags = docComment.getTags();
        PsiDocTag psiDocTagUnderTest = docCommentTags[shouldTagIndex];
        //  get doctag elements
        List<BddUtil.DocOffsetPair> offsetPairsMatches = BddUtil.getElementPairsInDocTag(psiDocTagUnderTest);
        assertThat(offsetPairsMatches.size(), is(matchings.length));
        for (int[] matching : matchings) {
            assertThat(offsetPairsMatches.get(matching[0]).getStart(), is(psiDocTagUnderTest.getChildren()[matching[1]]));
            assertThat(offsetPairsMatches.get(matching[0]).getEnd(), is(psiDocTagUnderTest.getChildren()[matching[2]]));
        }
    }

    /**
     * @verifies not consider part of the problem whitespace/nl for not ending tags
     * @see BddUtil#getElementPairsInDocTag(com.intellij.psi.javadoc.PsiDocTag)
     */
    public void testGetElementPairsInDocTag_shouldNotConsiderPartOfTheProblemWhitespacenlForNotEndingTags() throws Exception {

//        int[][] matchings = new int[][]{{0, 2, 2}};
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(myProject);
        //  count psiDocTags for first tag in javadoc and expect one only
        PsiDocComment docCommentFromText1 = elementFactory.createDocCommentFromText("/**\n" +
                "     * @should doo  \n" +
                "     * @should yoo\n" +
                "     * \n" +
                "     */");
        //  get doctag elements
        assertThat(BddUtil.getElementPairsInDocTag(docCommentFromText1.getTags()[0]).size(), is(1));

        PsiDocComment docCommentFromText2 = elementFactory.createDocCommentFromText("/**\n" +
                "     * @should doo zas \n" +
                "     * @should yoo\n" +
                "     * \n" +
                "     */");

        assertThat(BddUtil.getElementPairsInDocTag(docCommentFromText2.getTags()[0]).size(), is(1));
    }


}