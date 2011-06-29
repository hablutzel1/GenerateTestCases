package com.intellij.generatetestcases;


import com.intellij.generatetestcases.impl.TestClassImpl;
import com.intellij.generatetestcases.impl.TestMethodImpl;
import com.intellij.generatetestcases.test.BaseTests;
import com.intellij.generatetestcases.testframework.JUnit4Strategy;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocTag;
import org.junit.Test;

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

//        Project project = getProject();
        //  create or get a Test Method
        PsiClass psiClass = createSutClass();
        PsiMethod[] methods = psiClass.findMethodsByName("getUserByUuid", false);

        //  create or get a tag
        PsiDocTag tag = methods[0].getDocComment().getTags()[3];

        TestMethod tm1 = new TestMethodImpl(tag, new TestClassImpl(psiClass, new JUnit4Strategy(myProject)), new JUnit4Strategy(myProject));
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
//        Project project = getProject();
        //  create or get a Test Method
        PsiClass psiClass = createSutClass();
        PsiMethod[] methods = psiClass.findMethodsByName("getUserByUuid", false);

        //  create or get a tag
        PsiDocTag tag = methods[0].getDocComment().getTags()[3];

        TestMethod tm1 = new TestMethodImpl(tag, new TestClassImpl(psiClass, new JUnit4Strategy(myProject)), new JUnit4Strategy(myProject));
        TestMethod tm = tm1;
        assertThat(tm.getSutMethod().getName(), is("getUserByUuid"));


    }



    public static TestMethod findTestMethodInCollection(List<TestMethod> methods, String shouldDescription, String sutMethodName) {
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
    public void testReallyExists_shouldReturnTrueOnlyIfThisTestMethodHasABackingPsiMethod()
            throws Exception {

        PsiClass psiClass = createSutClass();
        createTestClassForSut();

        TestClassImpl testClass = new TestClassImpl(psiClass, new JUnit4Strategy(myProject));
        List<TestMethod> methods = testClass.getAllMethods();

        //  create or get a test method that really exists
        //  assert that reallyExists for the method return true and that the call to getBackingElement return the right method
        TestMethod tm = findTestMethodInCollection(methods, "fetch user with given uuid", "getUserByUuid");
        assertThat(tm.reallyExists(), is(true));

        tm = findTestMethodInCollection(methods, "find object given valid uuid", "getUserByUuid");
        assertThat(tm.reallyExists(), is(false));


    }


    /**
     * @verifies create the current test method
     * @see TestMethod#create()
     */
    @Test
    public void testCreate_shouldCreateTheCurrentTestMethod() throws Exception {

        //  create a psi sut
        PsiClass psiClass = createSutClass();
        //  create a psi test class
        createTestClassForSut();

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
        assertThat(tm.getBackingElement(), is(not(nullValue())));

        //  assert the backing test method is the right one

        assertThat(tm.getBackingElement().getName(), is("getUserByUuid_shouldFindObjectGivenValidUuid"));
    }

    /**
     * @verifies create the parent in the same content source root that its backing method if it didn't exist already
     * @see TestMethod#create()
     */
    @Test
    public void testCreate_shouldCreateTheParentInTheSameContentSourceRootThatItsBackingMethodIfItDidntExistAlready()
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

        assertThat(testClass.getBackingElement(), is(nullValue()));
        assertThat(testClass.reallyExists(), is(false));

        //  create it
        testMethod.create();

        // get the parent test class backing psi class,
        PsiClass backingClass = testClass.getBackingElement();

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
     * @verifies return a valid psiMethod if reallyExists returns true, false otherwise
     * @see TestMethod#getBackingElement()
     */
    public void testGetBackingElement_shouldReturnAValidPsiMethodIfReallyExistsReturnsTrueFalseOtherwise() throws Exception {
        PsiClass psiClass = createSutClass();
        createTestClassForSut();

        TestClassImpl testClass = new TestClassImpl(psiClass, new JUnit4Strategy(myProject));
        List<TestMethod> methods = testClass.getAllMethods();

        //  create or get a test method that really exists
        //  assert that reallyExists for the method return true and that the call to getBackingElement return the right method
        TestMethod tm = findTestMethodInCollection(methods, "fetch user with given uuid", "getUserByUuid");
        assertThat(tm.getBackingElement().getName(), is("getUserByUuid_shouldFetchUserWithGivenUuid"));
        assertThat(tm.reallyExists(), is(true));

        //  create or get a test method that doesn't exists
        tm = findTestMethodInCollection(methods, "find object given valid uuid", "getUserByUuid");

        //  assert that reallyExists for the method return false and that the call to getBackingElement return false
        assertThat(tm.getBackingElement(), nullValue());
        assertThat(tm.reallyExists(), is(false));

    }
}