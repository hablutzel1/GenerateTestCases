package com.intellij.generatetestcases;


import com.intellij.generatetestcases.impl.TestClassImpl;
import com.intellij.generatetestcases.impl.TestMethodImpl;
import com.intellij.generatetestcases.test.BaseTests;
import com.intellij.generatetestcases.testframework.JUnit4Strategy;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocTag;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class TestMethodTest extends BaseTests {

    /**
     * @verifies return a description without leading or trailing spaces
     * @see TestMethod#getDescription()
     */
    @Test
    public void testGetDescription_shouldReturnADescriptionWithoutLeadingOrTrailingSpaces()
            throws Exception {

        Project project = getProject();
        //  create or get a Test Method
        PsiClass psiClass = createSutClass(project);
        PsiMethod[] methods = psiClass.findMethodsByName("getUserByUuid", false);

        //  create or get a tag
        PsiDocTag tag = methods[0].getDocComment().getTags()[3];

        TestMethod tm1 = new TestMethodImpl(tag, new TestClassImpl(psiClass, new JUnit4Strategy()), new JUnit4Strategy());
        TestMethod tm = tm1;
        String description = tm.getDescription();

        //  verificar que la descripcion de retorno del metodo
        assertThat(description, is("fetch user with given uuid"));

    }

    /**
     * @verifies return the sut class for this method
     * @see TestMethod#getSutMethod()
     */
    @Test
    public void getSutMethod_shouldReturnTheSutClassForThisMethod()
            throws Exception {

    }

    /**
     * @verifies return the sut method for this method
     * @see TestMethod#getSutMethod()
     */
    @Test
    public void testGetSutMethod_shouldReturnTheSutMethodForThisMethod()
            throws Exception {

        //  instantiate test method
        Project project = getProject();
        //  create or get a Test Method
        PsiClass psiClass = createSutClass(project);
        PsiMethod[] methods = psiClass.findMethodsByName("getUserByUuid", false);

        //  create or get a tag
        PsiDocTag tag = methods[0].getDocComment().getTags()[3];

        TestMethod tm1 = new TestMethodImpl(tag, new TestClassImpl(psiClass, new JUnit4Strategy()), new JUnit4Strategy());
        TestMethod tm = tm1;
        assertThat(tm.getSutMethod().getName(), is("getUserByUuid"));


    }

    /**
     * @verifies return a valid psiMethod if reallyExists returns true, false otherwise
     * @see TestMethod#getBackingMethod()
     */
    @Test
    public void testgetBackingMethod_shouldReturnAValidPsiMethodIfReallyExistsReturnsTrueFalseOtherwise()
            throws Exception {


        PsiClass psiClass = createSutClass(myProject);
        createTestClassForSut(myProject);

        TestClassImpl testClass = new TestClassImpl(psiClass, new JUnit4Strategy());
        List<TestMethod> methods = testClass.getAllMethods();

        //  create or get a test method that really exists
        //  assert that reallyExists for the method return true and that the call to getBackingMethod return the right method
        TestMethod tm = findTestMethodInCollection(methods, "fetch user with given uuid", "getUserByUuid");
        assertThat(tm.getBackingMethod().getName(), is("getUserByUuid_shouldFetchUserWithGivenUuid"));
        assertThat(tm.reallyExists(), is(true));

        //  create or get a test method that doesn't exists
        tm = findTestMethodInCollection(methods, "find object given valid uuid", "getUserByUuid");

        //  assert that reallyExists for the method return false and that the call to getBackingMethod return false
        assertThat(tm.getBackingMethod(), nullValue());
        assertThat(tm.reallyExists(), is(false));
    }

    private TestMethod findTestMethodInCollection(List<TestMethod> methods, String shouldDescription, String sutMethodName) {
        TestMethod tm = null;
        for (TestMethod method : methods) {
            if (method.getDescription().equals(shouldDescription) && method.getSutMethod().getName().equals(sutMethodName)) {
                tm = method;
            }

        }
        return tm;
    }

    /**
     * @verifies return true only if this test method has a backing psiMethod
     * @see TestMethod#reallyExists()
     */
    @Test
    public void testreallyExists_shouldReturnTrueOnlyIfThisTestMethodHasABackingPsiMethod()
            throws Exception {

        PsiClass psiClass = createSutClass(myProject);
        createTestClassForSut(myProject);

        TestClassImpl testClass = new TestClassImpl(psiClass, new JUnit4Strategy());
        List<TestMethod> methods = testClass.getAllMethods();

        //  create or get a test method that really exists
        //  assert that reallyExists for the method return true and that the call to getBackingMethod return the right method
        TestMethod tm = findTestMethodInCollection(methods, "fetch user with given uuid", "getUserByUuid");
        assertThat(tm.reallyExists(), is(true));

        tm = findTestMethodInCollection(methods, "find object given valid uuid", "getUserByUuid");
        assertThat(tm.reallyExists(), is(false));


    }


    /**
     * @verifies create a test method with the expected body and javadoc
     * @see TestMethod#create()
     */
    @Test
    public void testcreate_shouldCreateATestMethodWithTheExpectedBodyAndJavadoc()
            throws Exception {

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
        TestClass testClass = BDDCore.createTestClass(myProject, aClass);

        //  get unitialized test method
        List<TestMethod> allTestMethods = testClass.getAllMethods();
        TestMethod testMethod = findTestMethodInCollection(allTestMethods, "do nothing", "zas");
        assertThat(testMethod.reallyExists(), is(false));

        //  actually create
        testMethod.create();

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


        PsiMethod backingMethod = testMethod.getBackingMethod();

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
//        fail("org.junit.Assert.fail(\"Not yet implemented\"); no se debe ver el import completo");


        TestMethod testMethodWithGenerics = findTestMethodInCollection(allTestMethods, "do nothing", "getHandlersForType");
        testMethodWithGenerics.create();
        PsiMethod methodWithGenericsBackingMethod = testMethodWithGenerics.getBackingMethod();

        PsiDocTag seeWithGenerics = findDocTagByName(methodWithGenericsBackingMethod, "see");

        String desc = seeWithGenerics.getText().substring(seeWithGenerics.getText().indexOf("Foo"));
        assertThat(desc, is("FooBar#getHandlersForType(Class, Class)"));
//        @see FooBar#getHandlersForType(Class<H>, Class<T>)
//        fail("al crear el @link para  public static <H, T> List<H> getHandlersForType(Class<H> handlerType, Class<T> type) no generar texto para los datos genericos");
    }

    /**
     * Find the first doctag for the docTagName, return null if nothing found
     *
     * @param backingMethod
     * @param docTagName
     * @return
     */
    private PsiDocTag findDocTagByName(PsiMethod backingMethod, String docTagName) {
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
     * @verifies create the current test method
     * @see TestMethod#create()
     */
    @Test
    public void testcreate_shouldCreateTheCurrentTestMethod() throws Exception {

        //  create a psi sut
        PsiClass psiClass = createSutClass(myProject);
        //  create a psi test class
        createTestClassForSut(myProject);

        //  create TestClass
        TestClass testClass = BDDCore.createTestClass(myProject, psiClass);

        //  get test method
        List<TestMethod> methods = testClass.getAllMethods();

        //  verify it is unitialized

        TestMethod tm = findTestMethodInCollection(methods, "find object given valid uuid", "getUserByUuid");
        assertThat(tm.reallyExists(), is(false));

        //  create it
        tm.create();

        //  verify it got initialized and its backing method exists

        assertThat(tm.reallyExists(), is(true));
        assertThat(tm.getBackingMethod(), is(not(nullValue())));

        //  assert the backing test method is the right one

        assertThat(tm.getBackingMethod().getName(), is("getUserByUuid_shouldFindObjectGivenValidUuid"));
    }

    /**
     * @verifies create the parent in the same content source root that its backing method if it didn't exist already
     * @see TestMethod#create()
     */
    @Test
    public void testcreate_shouldCreateTheParentInTheSameContentSourceRootThatItsBackingMethodIfItDidntExistAlready()
            throws Exception {

        //  create a psi sut without psi test class
        PsiClass aClass = createFooBarSutClass();

        //  create TestClass
        TestClass testClass = BDDCore.createTestClass(myProject, aClass);

        //  get test method
        TestMethod testMethod = testClass.getAllMethods().get(0);

        //  verify test method  is unitialized

        assertThat(testMethod.reallyExists(), is(false));

        //  verify test class is unitialized and backingClass is null

        assertThat(testClass.getBackingClass(), is(nullValue()));
        assertThat(testClass.reallyExists(), is(false));

        //  create it
        testMethod.create();

        // get the parent test class backing psi class,
        PsiClass backingClass = testClass.getBackingClass();

        //  assert location is the same that sut class
        PsiDirectory sutContentSourceRoot = getContentSourceRoot((PsiJavaFile) backingClass.getContainingFile());
        PsiDirectory testContentSourceRoot = getContentSourceRoot((PsiJavaFile) aClass.getContainingFile());
        assertThat(testContentSourceRoot, is(sutContentSourceRoot));

        ///  assert test class really exists and the same for the method
        assertThat(testClass.reallyExists(), is(true));
        assertThat(testMethod.reallyExists(), is(true));


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