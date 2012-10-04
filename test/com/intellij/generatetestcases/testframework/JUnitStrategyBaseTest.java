package com.intellij.generatetestcases.testframework;

import com.intellij.generatetestcases.model.BDDCore;
import com.intellij.generatetestcases.model.TestClass;
import com.intellij.generatetestcases.model.TestMethod;
import com.intellij.generatetestcases.test.BaseTests;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiExpressionStatementImpl;
import com.intellij.psi.javadoc.PsiDocTag;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.generatetestcases.model.TestMethodTest.findTestMethodInCollection;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: Jaime Hablutzel
 */
public class JUnitStrategyBaseTest extends BaseTests {
//    /**
//     * @verifies create a junit test method with the expected body and javadoc and verify class structure
//     * @see JUnitStrategyBase#createBackingTestMethod(com.intellij.psi.PsiClass, com.intellij.psi.PsiMethod, String)
//     */
//    @Test
//    public void testCreateBackingTestMethod_shouldCreateAJunitTestMethodWithTheExpectedBodyAndJavadocAndVerifyClassStructure() throws Exception {
//        addJunit4LibraryToMockProject();
//        //  create sut class
//        PsiClass aClass = createSutClass2();
//
//        //  create test class for sut
//
//        String testClassText = "package com.example;\n" +
//                "\n" +
//                "\n" +
//                "import org.junit.Assert;\n" +
//                "import org.junit.Test;\n" +
//                "\n" +
//                "public class FooBarTest {\n" +
//                "}";
//        createClassFromTextInPackage(myProject, testClassText, "FooBarTest", comExamplePackage);
//
//        //  create test class
//        TestClass testClass = BDDCore.createTestClass(aClass, new JUnit4Strategy(myProject));
//
//        //  get unitialized test method
//        List<TestMethod> allTestMethods = testClass.getAllMethods();
//        TestMethod testMethod = findTestMethodInCollection(allTestMethods, "do nothing", "zas");
//        assertThat(testMethod.reallyExists(), is(false)); // not created yet
//
//        //  actually create
//        testMethod.create();
//
//        PsiMethod backingMethod = testMethod.getBackingElement();
//        verifyStructureForGeneratedTestMethod(backingMethod);
//
//
//        TestMethod testMethodWithGenerics = findTestMethodInCollection(allTestMethods, "do nothing", "getHandlersForType");
//        testMethodWithGenerics.create();
//        PsiMethod methodWithGenericsBackingMethod = testMethodWithGenerics.getBackingElement();
//
//        PsiDocTag seeWithGenerics = findDocTagByName(methodWithGenericsBackingMethod, "see");
//
//        String desc = seeWithGenerics.getText().substring(seeWithGenerics.getText().indexOf("Foo"));
//        assertThat(desc, is("FooBar#getHandlersForType(Class, Class)"));
//
//
//    }

    /**
     * It should manage these issues
     * <br />
     * <pre>
     * /  verify import for Assert before actually importing
     *
     * //  verify if junit.framework.Assert exists, if it does do not import org.junit.Assert
     *
     * //  if Assert exists and is different to both of previous, place fully qualified statement
     * </pre>
     *
     * @verifies manage appropiately existence of multiple junit Assert's imports across junit versions
     * @see JUnitStrategyBase#createBackingTestMethod(com.intellij.psi.PsiClass, com.intellij.psi.PsiMethod, String)
     */
    @Test
    public void testCreateBackingTestMethod_shouldManageAppropiatelyExistenceOfMultipleJunitAssertsImportsAcrossJunitVersions() throws Exception {

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

        //  create test class with this import: org.junit.Assert, just the junit 4 expected Assert
        createClassFromTextInPackage(myProject, "package com.example;\n" +
                "\n" +
                "\n" +
                "import org.junit.Assert;\n" +
                "\n" +
                "public class FooBarTest {\n" +
                "}", "FooBarTest", comExamplePackage);

        PsiClass testBackingClass = triggerCreateTestClassAndMethod(aClass);

        //  verify this import exists after the creation oof a test method
        assertThat(BddUtil.findImportsInClass(testBackingClass, "org.junit.Assert").size(), is(1));
        assertThat(BddUtil.findImportsInClass(testBackingClass, "junit.framework.Assert").size(), is(0));

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
        TestClass testClass1 = BDDCore.createTestClass(aClass, new JUnit4Strategy(myProject));

        //  get unitialized test method
        List<TestMethod> allTestMethods = testClass1.getAllMethods();
        TestMethod testMethod = findTestMethodInCollection(allTestMethods, "do nothing", "zas");
        assertThat(testMethod.reallyExists(), is(false));

        //  actually create
        testMethod.create();
        TestClass testClass = testClass1;

        PsiClass testBackingClass = testClass.getBackingElement();
        return testBackingClass;
    }


    @Override
    protected boolean isAddJunit4Library() {
        return false;
    }


    /**
     * TODO verify if this is actually testing something that could fail
     *
     * @verifies manage appropiately any condition of the backing test class (imports, existing methods, modifiers, etc)
     * @see JUnitStrategyBase#createBackingTestMethod(com.intellij.psi.PsiClass, com.intellij.psi.PsiMethod, String)
     */
    @Test
    public void testCreateBackingTestMethod_shouldManageAppropiatelyAnyConditionOfTheBackingTestClassImportsExistingMethodsModifiersEtc() throws Exception {
        addJunit4LibraryToMockProject();

        //  create backnig test class with static imports
        PsiClass aClass = createSutClass2();
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

}
