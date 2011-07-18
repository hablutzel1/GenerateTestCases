package com.intellij.generatetestcases;


import com.intellij.generatetestcases.test.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.source.jsp.jspJava.*;
import junit.framework.Assert;
import org.jmock.*;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.*;

//@RunWith(JUnit4.class)

public class BDDCoreTest extends BaseTests {


    public <T> T zas(Class<T> foo) {

        return null;
    }


    /**
     * @verifies create a new test class with test methods unitialized
     * @see BDDCore#createTestClass(com.intellij.openapi.project.Project, com.intellij.psi.PsiClass)
     */
//	@Test
    public void testCreateTestClass_shouldCreateANewTestClassWithTestMethodsUnitialized()
            throws Exception {
        //  get project
        Project project = getProject();
        PsiClass psiClass = createSutClass();

        //  create class there
        TestClass testClass = BDDCore.createTestClass(project, psiClass);
        //  verificar que el retorno sea valido
        assertThat(" test class returned ", testClass, notNullValue());

        assertThat(testClass.reallyExists(), is(false));
        //  verificar la cantidad y el estado de los TestMethod's esperados
        assertThat(testClass.getAllMethods().size(), is(4));


        List<TestMethod> methods = testClass.getAllMethods();

        boolean exists = existsDescriptionInTestMethodsList("fetch user with given userId", methods);
        assertThat(exists, is(true));
        exists = existsDescriptionInTestMethodsList("fetch user with given uuid", methods);
        assertThat(exists, is(true));
        exists = existsDescriptionInTestMethodsList("find object given valid uuid", methods);
        assertThat(exists, is(true));
        exists = existsDescriptionInTestMethodsList("return null if no object found with given uuid", methods);
        assertThat(exists, is(true));

        for (TestMethod method : methods) {
            assertThat(method.reallyExists(), is(false));
        }

    }


    /**
     * @verifies return a test class that already exists for a sut class with some test methods initialized
     * @see com.intellij.generatetestcases.BDDCore#createTestClass(com.intellij.openapi.project.Project, com.intellij.psi.PsiClass)
     */
    @Test
    public void testCreateTestClass_shouldReturnATestClassThatAlreadyExistsForASutClassWithSomeTestMethodsInitialized()
            throws Exception {

        //  get project
        Project project = getProject();
        PsiClass psiClass = createSutClass();
        //  create class there
        createTestClassForSut();

        TestClass testClass = BDDCore.createTestClass(project, psiClass);

        //  esperar que la clase testClass really exists
        assertThat(testClass.reallyExists(), is(true));

        //  expect that two methods really exists
        List<TestMethod> allTestMethods = testClass.getAllMethods();


        //"\tpublic void getUser_shouldFetchUserWithGivenUserId()
        // "\tpublic void getUserByUuid_shouldFetchUserWithGivenUuid() throws Exception {\n" +

        boolean exists = existsReallyInitializedTestMethodInCollection(allTestMethods, "getUser_shouldFetchUserWithGivenUserId");
        assertThat(exists, is(true));
        exists = existsReallyInitializedTestMethodInCollection(allTestMethods, "getUserByUuid_shouldFetchUserWithGivenUuid");
        assertThat(exists, is(true));

        //  test some unitialized method
        exists = existsReallyInitializedTestMethodInCollection(allTestMethods, "getUserByUuid_shouldFindObjectGivenValidUuid");
        assertThat(exists, is(false));

    }


    /**
     * @verifies ignore should tags without a description when creating bdd test methods
     * @see BDDCore#createTestClass(com.intellij.openapi.project.Project, com.intellij.psi.PsiClass)
     */
    public void testCreateTestClass_shouldIgnoreShouldTagsWithoutADescriptionWhenCreatingBddTestMethods() throws Exception {


        String text = "package com.example;  public interface Foo  {\n" +
                "\n" +
                "\t/**\n" +
                "\t * Get user by the given uuid.\n" +
                "\t * \n" +
                "\t * @param uuid\n" +
                "\t * @return\n" +
                "\t * @throws APIException\n" +
                "\t * @should \n" +
                "\t * @should\n" +
                "\t */\n" +
                "\tpublic String getUserByUuid(String uuid);\n" +
                "\n" +
                "}";

        final String className = "Foo";

        PsiClass classFromTextInPackage = createClassFromTextInPackage(myProject, text, className, comExamplePackage);

        Project project = getProject();


        TestClass testClass = BDDCore.createTestClass(project, classFromTextInPackage);

        assertThat(testClass.getAllMethods().size(), is(0));

    }

    /**
     * @verifies throw exception if there is a try to create a test class with an unsupported PsiClass
     * @see BDDCore#createTestClass(com.intellij.openapi.project.Project, com.intellij.psi.PsiClass)
     */
    public void testCreateTestClass_shouldThrowExceptionIfThereIsATryToCreateATestClassWithAnUnsupportedPsiClass() throws Exception {

        // TODO try to create with a Jsp Psi Class
        Mockery context = new Mockery(); // should be instance variable
        final JspClass mock = context.mock(JspClass.class);
        context.checking(new Expectations() {
            {
                // jmock without expectations
            }
        });
        ExpectExceptionsExecutor.execute(new ExpectExceptionsTemplate<IllegalArgumentException>() {
            @Override
            public Class<IllegalArgumentException> getExpectedException() {
                return IllegalArgumentException.class;
            }

            @Override
            public void doInttemplate() {
        BDDCore.createTestClass(myProject, mock);
            }
        });


        context.assertIsSatisfied();

    }


}