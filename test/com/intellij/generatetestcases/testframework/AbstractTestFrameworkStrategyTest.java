package com.intellij.generatetestcases.testframework;

import com.intellij.generatetestcases.model.BDDCore;
import com.intellij.generatetestcases.model.TestClass;
import com.intellij.generatetestcases.model.TestMethod;
import com.intellij.generatetestcases.test.BaseTests;
import com.intellij.generatetestcases.test.ExpectExceptionsExecutor;
import com.intellij.generatetestcases.test.ExpectExceptionsTemplate;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.testIntegration.TestFramework;
import org.hamcrest.core.Is;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.generatetestcases.model.TestMethodTest.findTestMethodInCollection;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Creado por: jaime
 * 10/3/12
 */
public class AbstractTestFrameworkStrategyTest extends BaseTests {

    public static void verifyStructureForGeneratedTestMethod(PsiMethod backingMethod) {
        //  verify backing method structure like this one

        /////////////////////////////////////////////////
        //	/**
        //	 * @see FooBar#zas()
        //	 * @verifies do nothing
        //	 */
        //	public void zas_shouldDoNothing() throws Exception {
        //		//TODO auto-generated
        //	}
        //////////////////////////////////////////////////


        PsiDocTag[] docTags = backingMethod.getDocComment().getTags();
        assertThat(docTags.length, Is.is(2));

        PsiDocTag tag = findDocTagByName(backingMethod, "see");

        assertThat(tag.getValueElement().getText(), Is.is("FooBar#zas()"));

        PsiDocTag verifiesTag = findDocTagByName(backingMethod, "verifies");
        PsiElement[] elements = verifiesTag.getDataElements();
        String verifiesDescription = "";
        for (PsiElement element : elements) {
            verifiesDescription += element.getText() + " ";
        }
        verifiesDescription = verifiesDescription.trim();
        assertThat(verifiesDescription, Is.is("do nothing"));

        //  assert presence of throws clause
        assertThat(backingMethod.getThrowsList().getReferencedTypes()[0].getCanonicalText(), Is.is("java.lang.Exception"));

        MethodBodyVisitor bodyVisitor = new MethodBodyVisitor();
        backingMethod.getBody().acceptChildren(bodyVisitor);
        //  assert comment in the body
        assertThat(bodyVisitor.getComments().get(0).getText(), Is.is("//TODO auto-generated"));

    }

    /**
     *
     * @verifies create a test class with the suffix 'Test'
     * @see AbstractTestFrameworkStrategy#createBackingTestClass(com.intellij.psi.PsiClass, com.intellij.psi.PsiDirectory)
     */
    public void testCreateBackingTestClass_shouldCreateATestClassWithTheSuffixTest() throws Exception {
        // create sut class
        PsiClass psiClass = createSutClass();
        // create test class
        PsiClass backingTestClass = new AbstractTestFrameworkStrategy(myProject) {

            @Override
            public TestFramework getTestFramework() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @NotNull
            @Override
            public PsiMethod createBackingTestMethod(@NotNull PsiClass testClass, @NotNull PsiMethod sutMethod, @NotNull String testDescription) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public PsiMethod findBackingTestMethod(@NotNull PsiClass testClass, @NotNull PsiMethod sutMethod, @NotNull String testDescription) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @NotNull
            @Override
            public String getExpectedNameForThisTestMethod(@NotNull String sutMethodName, @NotNull String description) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        }.createBackingTestClass(psiClass, null);
        //  assert the name
        assertThat(backingTestClass.getName(), is("FooTest"));
        //  assert no extends exists
        PsiClassType[] referencedTypes = backingTestClass.getExtendsList().getReferencedTypes();
        assertThat("number of extends for test class", referencedTypes.length, is(0));
    }


    /**
     * @verifies create a appropiate name for the test method
     * @see com.intellij.generatetestcases.testframework.JUnit4Strategy#generateGenericTestMethodName(String, String)
     */
    public void testGenerateGenericTestMethodName_shouldCreateAAppropiateNameForTheTestMethod() throws Exception {

        String methodName = "generateTestMethodName";
        String description = "create a appropiate name for the test method";
        String testMethodName = AbstractTestFrameworkStrategy.generateGenericTestMethodName(methodName, description);
        assertEquals("generateTestMethodName_shouldCreateAAppropiateNameForTheTestMethod", testMethodName);

    }

    /**
     * @verifies fail if wrong args
     * @see com.intellij.generatetestcases.testframework.JUnit4Strategy#generateGenericTestMethodName(String, String)
     */
    public void testGenerateGenericTestMethodName_shouldFailIfWrongArgs() throws Exception {

        ExpectExceptionsExecutor.execute(new ExpectExceptionsTemplate() {
            public Class getExpectedException() {
                return IllegalArgumentException.class;
            }

            public void doInttemplate() {
                AbstractTestFrameworkStrategy.generateGenericTestMethodName("", "");
            }
        });

        ExpectExceptionsExecutor.execute(new ExpectExceptionsTemplate() {
            public Class getExpectedException() {
                return IllegalArgumentException.class;
            }

            public void doInttemplate() {
                AbstractTestFrameworkStrategy.generateGenericTestMethodName(null, null);
            }
        });


    }

    /**
     * @verifies create a test method with the expected generic body and javadoc and verify class structure
     * @see AbstractTestFrameworkStrategy#createBackingTestMethod(com.intellij.psi.PsiClass, com.intellij.psi.PsiMethod, String)
     */
    public void testCreateBackingTestMethod_shouldCreateATestMethodWithTheExpectedGenericBodyAndJavadocAndVerifyClassStructure() throws Exception {

        //  create test method
        PsiClass aClass = createSutClass2();
        TestClass testClass = BDDCore.createTestClass(aClass, new AbstractTestFrameworkStrategy(myProject) {
            @Override
            public TestFramework getTestFramework() {
                return null;
            }
        });

        //  get unitialized test method
        List<TestMethod> allTestMethods = testClass.getAllMethods();
        TestMethod testMethod = findTestMethodInCollection(allTestMethods, "do nothing", "zas");
        assertThat(testMethod.reallyExists(), Is.is(false)); // not created yet

        //  actually create
        testMethod.create();

        PsiMethod backingMethod = testMethod.getBackingElement();
        verifyStructureForGeneratedTestMethod(backingMethod);


        // test for correct javadoc when method uses generics


        TestMethod testMethodWithGenerics = findTestMethodInCollection(allTestMethods, "do nothing", "getHandlersForType");
        testMethodWithGenerics.create();
        PsiMethod methodWithGenericsBackingMethod = testMethodWithGenerics.getBackingElement();

        PsiDocTag seeWithGenerics = BaseTests.findDocTagByName(methodWithGenericsBackingMethod, "see");

        String desc = seeWithGenerics.getText().substring(seeWithGenerics.getText().indexOf("Foo"));
        assertThat(desc, Is.is("FooBar#getHandlersForType(Class, Class)"));



    }

    /**
     * Counts comments and statements in a pSi method body
     */
    private static class MethodBodyVisitor extends PsiElementVisitor {
        public List<PsiComment> getComments() {
            return comments;
        }

        public List<PsiStatement> getStatements() {
            return statements;


        }


        private MethodBodyVisitor() {
            comments = new ArrayList<PsiComment>();
            statements = new ArrayList<PsiStatement>();
        }

        private List<PsiComment> comments;
        private List<PsiStatement> statements;


        @Override
        public void visitComment(PsiComment comment) {

            this.comments.add(comment);
        }

        @Override
        public void visitElement(PsiElement element) {
            if (element instanceof PsiStatement) {
                PsiStatement psiStatement = (PsiStatement) element;
                this.statements.add(psiStatement);
            }
        }
    }
}
