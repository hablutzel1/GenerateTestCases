package com.intellij.generatetestcases.model;


import com.intellij.generatetestcases.TestFrameworkNotConfigured;
import com.intellij.generatetestcases.model.BDDCore;
import com.intellij.generatetestcases.model.TestClass;
import com.intellij.generatetestcases.model.TestMethod;
import com.intellij.generatetestcases.test.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.source.jsp.jspJava.*;
import org.jmock.*;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.*;

public class BDDCoreTest extends BaseTests {


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
        TestClass result;
        try {
            result = BDDCore.createTestClass(psiClass);
        } catch (TestFrameworkNotConfigured testFrameworkNotConfigured) {
            throw new RuntimeException(testFrameworkNotConfigured);
        }
        TestClass testClass = result;
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
     * @see com.intellij.generatetestcases.model.BDDCore#createTestClass(com.intellij.openapi.project.Project, com.intellij.psi.PsiClass)
     */
    @Test
    public void testCreateTestClass_shouldReturnATestClassThatAlreadyExistsForASutClassWithSomeTestMethodsInitialized()
            throws Exception {

        //  get project
        Project project = getProject();
        PsiClass psiClass = createSutClass();
        //  create class there
        createTestClassForSut();

        TestClass result;
        try {
            result = BDDCore.createTestClass(psiClass);
        } catch (TestFrameworkNotConfigured testFrameworkNotConfigured) {
            throw new RuntimeException(testFrameworkNotConfigured);
        }
        TestClass testClass = result;

        //  esperar que la clase testClass really exists
        assertThat(testClass.reallyExists(), is(true));

        //  expect that two methods really exists
        List<TestMethod> allTestMethods = testClass.getAllMethods();


        //"\tpublic void getUser_shouldFetchUserWithGivenUserId()
        // "\tpublic void getUserByUuid_shouldFetchUserWithGivenUuid() throws Exception {\n" +

        boolean exists = existsReallyInitializedTestMethodInCollection(allTestMethods, "testGetUser_shouldFetchUserWithGivenUserId");
        assertThat(exists, is(true));
        exists = existsReallyInitializedTestMethodInCollection(allTestMethods, "testGetUserByUuid_shouldFetchUserWithGivenUuid");
        assertThat(exists, is(true));

        //  test some unitialized method
        exists = existsReallyInitializedTestMethodInCollection(allTestMethods, "testGetUserByUuid_shouldFindObjectGivenValidUuid");
        assertThat(exists, is(false));

    }


    /**
     * @verifies ignore should tags without a description when creating bdd test methods
     * @see com.intellij.generatetestcases.model.BDDCore#createTestClass(com.intellij.openapi.project.Project, com.intellij.psi.PsiClass)
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


        TestClass result;
        try {
            result = BDDCore.createTestClass(classFromTextInPackage);
        } catch (TestFrameworkNotConfigured testFrameworkNotConfigured) {
            throw new RuntimeException(testFrameworkNotConfigured);
        }
        TestClass testClass = result;

        assertThat(testClass.getAllMethods().size(), is(0));

    }

    /**
     * @verifies throw exception if there is a try to create a test class with an unsupported PsiClass
     * @see com.intellij.generatetestcases.model.BDDCore#createTestClass(com.intellij.openapi.project.Project, com.intellij.psi.PsiClass)
     */
    public void testCreateTestClass_shouldThrowExceptionIfThereIsATryToCreateATestClassWithAnUnsupportedPsiClass() throws Exception {

        //  try to create with a Jsp Psi Class
        Mockery context = new Mockery(); // should be instance variable
        final JspClass mock = context.mock(JspClass.class);
        //  train 'mock' to return something with getProject()
        context.checking(new Expectations() {
            {
                oneOf(mock).getProject();
                will(returnValue(getProject()));
            }
        });

        ExpectExceptionsExecutor.execute(new ExpectExceptionsTemplate<IllegalArgumentException>() {
            @Override
            public Class<IllegalArgumentException> getExpectedException() {
                return IllegalArgumentException.class;
            }

            @Override
            public void doInttemplate() {
                try {
                    BDDCore.createTestClass(mock);
                    return;
                } catch (TestFrameworkNotConfigured testFrameworkNotConfigured) {
                    throw new RuntimeException(testFrameworkNotConfigured);
                }
            }
        });


        context.assertIsSatisfied();

    }


}