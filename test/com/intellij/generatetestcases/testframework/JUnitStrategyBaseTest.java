package com.intellij.generatetestcases.testframework;

import com.intellij.generatetestcases.BDDCore;
import com.intellij.generatetestcases.TestClass;
import com.intellij.generatetestcases.TestMethod;
import com.intellij.generatetestcases.test.BaseTests;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiExpressionStatementImpl;
import com.intellij.psi.javadoc.PsiDocTag;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.generatetestcases.TestMethodTest.findTestMethodInCollection;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: Jaime Hablutzel
 */
public class JUnitStrategyBaseTest extends BaseTests {
    /**
     * @verifies create a junit test method with the expected body and javadoc and verify class structure
     * @see JUnitStrategyBase#createBackingTestMethod(com.intellij.psi.PsiClass, com.intellij.psi.PsiMethod, String)
     */
    @Test
    public void testCreateBackingTestMethod_shouldCreateAJunitTestMethodWithTheExpectedBodyAndJavadocAndVerifyClassStructure() throws Exception {
        addJunit4LibraryToMockProject();
        //  create sut class
        PsiClass aClass = createFooBarSutClass();

        //  create test class for sut

        String testClassText = "package com.example;\n" +
                "\n" +
                "\n" +
                "import org.junit.Assert;\n" +
                "import org.junit.Test;\n" +
                "\n" +
                "public class FooBarTest {\n" +
                "}";
        createClassFromTextInPackage(myProject, testClassText, "FooBarTest", comExamplePackage);

        //  create test class
        TestClass testClass = BDDCore.createTestClass(myProject, aClass, new JUnit4Strategy(myProject));

        //  get unitialized test method
        List<TestMethod> allTestMethods = testClass.getAllMethods();
        TestMethod testMethod = findTestMethodInCollection(allTestMethods, "do nothing", "zas");
        assertThat(testMethod.reallyExists(), is(false));

        //  actually create
        testMethod.create();

        PsiMethod backingMethod = testMethod.getBackingMethod();
        verifyStructureForShouldAnnotation(backingMethod);


        TestMethod testMethodWithGenerics = findTestMethodInCollection(allTestMethods, "do nothing", "getHandlersForType");
        testMethodWithGenerics.create();
        PsiMethod methodWithGenericsBackingMethod = testMethodWithGenerics.getBackingMethod();

        PsiDocTag seeWithGenerics = findDocTagByName(methodWithGenericsBackingMethod, "see");

        String desc = seeWithGenerics.getText().substring(seeWithGenerics.getText().indexOf("Foo"));
        assertThat(desc, is("FooBar#getHandlersForType(Class, Class)"));


    }

    private void verifyStructureForShouldAnnotation(PsiMethod backingMethod) {
        //  verify backing method structure like this one

        /////////////////////////////////////////////////
        //	/**
        //	 * @see FooBar#zas()
        //	 * @verifies do nothing
        //	 */
        //	@Test
        //	public void zas_shouldDoNothing() throws Exception {
        //		//TODO auto-generated
        //		Assert.fail("Not yet implemented");
        //	}
        //////////////////////////////////////////////////


        PsiDocTag[] docTags = backingMethod.getDocComment().getTags();
        assertThat(docTags.length, is(2));

        PsiDocTag tag = findDocTagByName(backingMethod, "see");

        assertThat(tag.getValueElement().getText(), is("FooBar#zas()"));

        PsiDocTag verifiesTag = findDocTagByName(backingMethod, "verifies");
        PsiElement[] elements = verifiesTag.getDataElements();
        String verifiesDescription = "";
        for (PsiElement element : elements) {
            verifiesDescription += element.getText() + " ";
        }
        verifiesDescription = verifiesDescription.trim();
        assertThat(verifiesDescription, is("do nothing"));

        //  assert for test annotation
        //  get qualified name, consider the package
//        PsiAnnotation annotation = (PsiAnnotation) backingMethod.getModifierList().getAnnotations()[0].getOriginalElement();
        assertThat(backingMethod.getModifierList().getAnnotations()[0].getQualifiedName(), is("org.junit.Test"));

        //  assert presence of throws clause
        assertThat(backingMethod.getThrowsList().getReferencedTypes()[0].getCanonicalText(), is("java.lang.Exception"));

        MethodBodyVisitor bodyVisitor = new MethodBodyVisitor();
        backingMethod.getBody().acceptChildren(bodyVisitor);
        //  assert comment in the body
        assertThat(bodyVisitor.getComments().get(0).getText(), is("//TODO auto-generated"));

        // assert Assert.fail... is present

        assertThat(bodyVisitor.getStatements().get(0).getText(), is("Assert.fail(\"Not yet implemented\");"));
    }

    private PsiClass createFooBarSutClass() {
        String text = "package com.example;\n" +
                "\n" +
                "public interface FooBar {\n" +
                "\t/**\n" +
                "\t * @should do nothing\n" +
                "\t */\n" +
                "\tvoid zas();\n" +
                "\t\n" +
                "\t\n" +
                "\t/**\n" +
                "\t * @should do nothing\n" +
                "\t */\n" +
                "\tpublic <H, T> List<H> getHandlersForType(Class<H> handlerType, Class<T> type);\n" +
                "}\n" +
                "";
        PsiClass aClass = createClassFromTextInPackage(myProject, text, "FooBar", comExamplePackage);
        return aClass;
    }

    /**
     * Find the first doctag for the docTagName, return null if nothing found
     *
     * @param backingMethod
     * @param docTagName
     * @return
     */
    private static PsiDocTag findDocTagByName(PsiMethod backingMethod, String docTagName) {
        PsiDocTag[] docTags2 = backingMethod.getDocComment().getTags();
        PsiDocTag tag = null;
        for (PsiDocTag docTag : docTags2) {
            if (docTag.getName().equals(docTagName)) {
                tag = docTag;
                break;
            }
        }
        return tag;
    }

    /**
     * It should manage these issues
     * <br />
     * <pre>
     * /  verify import for Assert before actually importing
     * <p/>
     * //  verify if junit.framework.Assert exists, if it does do not import org.junit.Assert
     * <p/>
     * //  if Assert exists and is different to both of previous, place fully qualified statement
     * </pre>
     *
     * @verifies manage appropiately existence of multiple Assert's imports
     * @see JUnitStrategyBase#createBackingTestMethod(com.intellij.psi.PsiClass, com.intellij.psi.PsiMethod, String)
     */
    @Test
    public void testCreateBackingTestMethod_shouldManageAppropiatelyExistenceOfMultipleAssertsImports() throws Exception {

        addJunit4LibraryToMockProject();
        //  invoke create foobarsut class
        PsiClass aClass = createClassFromTextInPackage(myProject, "package com.example;\n" +
                "\n" +
                "public interface FooBar {\n" +
                "\t/**\n" +
                "\t * @should do nothing\n" +
                "\t */\n" +
                "\tvoid zas();\n" +
                "\t\n" +
                "\t\n" +
                "\t/**\n" +
                "\t * @should do nothing\n" +
                "\t */\n" +
                "\tpublic <H, T> List<H> getHandlersForType(Class<H> handlerType, Class<T> type);\n" +
                "}\n" +
                "", "FooBar", comExamplePackage);

        //  create test class with this import: org.junit.Assert

        createClassFromTextInPackage(myProject, "package com.example;\n" +
                "\n" +
                "\n" +
                "import org.junit.Assert;\n" +
                "\n" +
                "public class FooBarTest {\n" +
                "}", "FooBarTest", comExamplePackage);

        PsiClass testBackingClass = triggerCreateTestClassAndMethod(aClass);


        List<PsiImportStatementBase> matchingImports = BddUtil.findImportsInClass(testBackingClass, "org.junit.Assert");

        //  verify this import exists after the creation oof a test method
        assertThat(matchingImports.size(), is(1));

        //  delete the test class
        testBackingClass.delete();

        //  create a test class with junit.framework.Assert
        createClassFromTextInPackage(myProject, "package com.example;\n" +
                "\n" +
                "\n" +
                "import junit.framework.Assert;\n" +
                "\n" +
                "public class FooBarTest {\n" +
                "}", "FooBarTest", comExamplePackage);

        testBackingClass = triggerCreateTestClassAndMethod(aClass);

        //  verify backing class structure with only that import
        assertThat(BddUtil.findImportsInClass(testBackingClass, "junit.framework.Assert").size(), is(1));
        assertThat(BddUtil.findImportsInClass(testBackingClass, "org.junit.Assert").size(), is(0));

        // ensure this will not qualify the expression statmente for Assert.fail("Not yet implemented");
        String expressionStatement = ((PsiExpressionStatementImpl) testBackingClass.getMethods()[0].getBody().getStatements()[0]).getExpression().getText();
        assertThat(expressionStatement.startsWith("org.junit"), is(false));

        testBackingClass.delete();

        // TODO create Assert class/type

        //  create a test class with unknown Assert import
        createClassFromTextInPackage(myProject, "package com.example;\n" +
                "\n" +
                "\n" +
                "import strange.Assert;\n" +
                "\n" +
                "public class FooBarTest {\n" +
                "}", "FooBarTest", comExamplePackage);


        testBackingClass = triggerCreateTestClassAndMethod(aClass);

        //  verify imports
        assertThat(BddUtil.findImportsInClass(testBackingClass, "junit.framework.Assert").size(), is(0));
        assertThat(BddUtil.findImportsInClass(testBackingClass, "org.junit.Assert").size(), is(0));
        assertThat(BddUtil.findImportsInClass(testBackingClass, "strange.Assert").size(), is(1));

        //  verify statement in method org.junit.Assert.fail("Not yet implemented"); fully qualified
        String expressionStatement2 = ((PsiExpressionStatementImpl) testBackingClass.getMethods()[0].getBody().getStatements()[0]).getExpression().getText();
        assertThat(expressionStatement2.startsWith("org.junit"), is(true));


    }

    private PsiClass triggerCreateTestClassAndMethod(PsiClass aClass) {
        //  create the test class and invoke a method to create
        //  create test class
        TestClass testClass1 = BDDCore.createTestClass(myProject, aClass, new JUnit4Strategy(myProject));

        //  get unitialized test method
        List<TestMethod> allTestMethods = testClass1.getAllMethods();
        TestMethod testMethod = findTestMethodInCollection(allTestMethods, "do nothing", "zas");
        assertThat(testMethod.reallyExists(), is(false));

        //  actually create
        testMethod.create();
        TestClass testClass = testClass1;

        PsiClass testBackingClass = testClass.getBackingClass();
        return testBackingClass;
    }


    @Override
    protected boolean isAddJunit4Library() {
        return false;
    }

    /**
     * @verifies create test method even with broken references if test libraries aren't available
     * @see JUnitStrategyBase#createBackingTestMethod(com.intellij.psi.PsiClass, com.intellij.psi.PsiMethod, String)
     */
    @Test
    public void testCreateBackingTestMethod_shouldCreateTestMethodEvenWithBrokenReferencesIfTestLibrariesArentAvailable() throws Exception {

        //  set up mock project without junit library

        //  create test for should annotation
          PsiClass aClass = createFooBarSutClass();



              //  create test class
        TestClass testClass = BDDCore.createTestClass(myProject, aClass, new JUnit4Strategy(myProject));

        //  get unitialized test method
        List<TestMethod> allTestMethods = testClass.getAllMethods();
        TestMethod testMethod = findTestMethodInCollection(allTestMethods, "do nothing", "zas");
        assertThat(testMethod.reallyExists(), is(false));

        //  actually create
        testMethod.create();

        PsiMethod backingMethod = testMethod.getBackingMethod();
           //  verify operation completed succesfully
        verifyStructureForShouldAnnotation(backingMethod);

    }

    /**
     * @verifies manage appropiately any condition of the backing test class (imports, existing methods, modifiers, etc)
     * @see JUnitStrategyBase#createBackingTestMethod(com.intellij.psi.PsiClass, com.intellij.psi.PsiMethod, String)
     */
    @Test
    public void testCreateBackingTestMethod_shouldManageAppropiatelyAnyConditionOfTheBackingTestClassImportsExistingMethodsModifiersEtc() throws Exception {
        addJunit4LibraryToMockProject();

        //  create backnig test class with static imports
        PsiClass aClass = createFooBarSutClass();
        //"consider situation where target class has static imports"
        createClassFromTextInPackage(myProject, "package com.example;\n" +
                "\n" +
                "\n" +
                "import org.junit.Assert;\n" +
                "\n" +
                "import static org.junit.Assert.fail;\n" +
                "public class FooBarTest {\n" +
                "}", "FooBarTest", comExamplePackage);

        PsiClass psiClass = triggerCreateTestClassAndMethod(aClass);

        assertThat(psiClass.getMethods().length, is(1));
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
