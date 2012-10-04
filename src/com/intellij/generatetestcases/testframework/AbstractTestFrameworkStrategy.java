package com.intellij.generatetestcases.testframework;

import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.generatetestcases.util.Constants;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import com.intellij.testIntegration.TestFramework;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Creado por: jaime
 * 10/3/12
 */
public abstract class AbstractTestFrameworkStrategy implements TestFrameworkStrategy {

    private Project project;

    public AbstractTestFrameworkStrategy(Project project) {
        this.project = project;
    }

    /**
     * Creates a test method name generic in the form {method_under_test}_should{should_description_camel_cased}
     *
     * @param originMethodName
     * @param shouldDescription
     * @return
     * @should create a appropiate name for the test method
     * @should fail if wrong args
     */
    protected static String generateGenericTestMethodName(@NotNull String originMethodName, @NotNull String shouldDescription) {

        if (StringUtils.isBlank(originMethodName) || StringUtils.isBlank(shouldDescription)) {
            throw new IllegalArgumentException();
        }

        StringBuilder builder = new StringBuilder(originMethodName
                + "_should");
        @NotNull
        String[] tokens = shouldDescription.split("\\s+");
        for (String token : tokens) {
            char[] allChars = token.toCharArray();
            StringBuilder validChars = new StringBuilder();
            for (char validChar : allChars) {
                if (Character.isJavaIdentifierPart(validChar)) {
                    validChars.append(validChar);
                }
            }
            builder.append(toCamelCase(validChars.toString()));
        }
        return builder.toString();
    }

    private static String toCamelCase(String input) {
        assert input != null;
        if (input.length() == 0) {
            return ""; // is it ok?
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * It will return the test framework descriptor for the specified test framework, this descriptor will give us information
     * like this: TODO document
     *
     * @return
     */
    public abstract TestFramework getTestFramework();

    @Override
    public final boolean isTestFrameworkLibraryAvailable(Module module) {
        return getTestFramework().isLibraryAttached(module);
    }

    @Override
    public PsiClass findBackingPsiClass(PsiClass sutClass) {

        if (sutClass instanceof PsiAnonymousClass) {
            return null;
        }
        String packageName = BddUtil.getPackageName(sutClass);
        String testClassName = getCandidateTestClassName(sutClass);


        String fullyQualifiedTestClass = packageName == null ? testClassName : (packageName + "." + testClassName);
        //  verify if the test class really exists in classpath for the current module/project
        return JavaPsiFacade.getInstance(project).findClass(fullyQualifiedTestClass, GlobalSearchScope.projectScope(project));
    }

    /**
     * Meant to be overrided for test classses don't doesn't follow the 'Test' suffix
     * convention in its name
     *
     * @param sutClass
     * @return
     */
    @Override
    public String getCandidateTestClassName(PsiClass sutClass) {
        //  build the test class name
        //  get the sut class name
        String s = sutClass.getName();
        return s + TEST_CLASS_SUFFIX;
    }


    /**
     *
     * @param sutClass
     * @param sourceRoot
     * @return
     * @should create a test class with the suffix 'Test'
     */
    @Override
    public PsiClass createBackingTestClass(PsiClass sutClass, PsiDirectory sourceRoot) {

        PsiClass ret;
        if (sourceRoot == null || sourceRoot.equals(sutClass.getContainingFile().getParent())) {
            //  create the test class in the same source root

            //  get psiDirectory for sut class
            PsiElement parentPackage = sutClass.getScope().getParent();
            // get test class name
            String testClassName = getCandidateTestClassName(sutClass);
            //  check
            JavaDirectoryService.getInstance().checkCreateClass((PsiDirectory) parentPackage, testClassName);
            //  create
            ret = JavaDirectoryService.getInstance().createClass((PsiDirectory) parentPackage, testClassName, "Class");

        } else {

            //  create the test class in the specified source root
            // get test class name
            String testClassName = getCandidateTestClassName(sutClass);


            String packageName = BddUtil.getPackageName(sutClass);
            if (packageName == null) {
                packageName = "";
            }
            VirtualFile path = sourceRoot.getVirtualFile().findFileByRelativePath(packageName.replace(".", "/"));
            PsiDirectory psiDirectory;
            if (path == null) {
                //  check or create entire path to package
                psiDirectory = DirectoryUtil.createSubdirectories(packageName, sourceRoot, ".");

            } else {
                //  just create a psi directory for VirtualFile
                psiDirectory = PsiManager.getInstance(project).findDirectory(path);
            }
            //  check
            JavaDirectoryService.getInstance().checkCreateClass(psiDirectory, testClassName);
            //  create
            ret = JavaDirectoryService.getInstance().createClass(psiDirectory, testClassName, "Class");

        }
        afterCreatingClass(project, ret);
        return ret;

    }

    /**
     * This method will be called after the class is created, allowing the extender to do some work (e.g. add some imports) on the created test class
     *
     * @param project
     * @param backingTestClass
     */
    protected void afterCreatingClass(Project project, PsiClass backingTestClass) {

    }

    @Override
    public PsiMethod findBackingTestMethod(PsiClass testClass, PsiMethod sutMethod, String testDescription) {
        //  resolve (find) backing test method in test class
        String nombreMetodoDePrueba = getExpectedNameForThisTestMethod(sutMethod.getName(), testDescription);

        PsiMethod[] byNameMethods = testClass.findMethodsByName(nombreMetodoDePrueba, false);
        if (byNameMethods.length > 0) {
            return byNameMethods[0];
        }

        return null;
    }

    // TODO migrate tests that belongs to this level from com.intellij.generatetestcases.testframework.JUnitStrategyBase.createBackingTestMethod()

    /**
     *
     * @param testClass
     * @param sutMethod
     * @param testDescription @return
     * @return
     * @should create a test method with the expected generic body and javadoc and verify class structure
     */
    public PsiMethod createBackingTestMethod(PsiClass testClass, PsiMethod sutMethod, String testDescription) {
        PsiElementFactory elementFactory;

        elementFactory = JavaPsiFacade.getElementFactory(sutMethod.getProject());
        //  get test method name
        PsiMethod factoriedTestMethod = elementFactory.createMethod(getExpectedNameForThisTestMethod(sutMethod.getName(), testDescription), PsiType.VOID);

        //  correr esto dentro de un write-action   ( Write access is allowed inside write-action only )
        testClass.add(factoriedTestMethod);
        PsiMethod realTestMethod = testClass.findMethodBySignature(factoriedTestMethod, false);


        //  get sut method name and signature
        // use fqn#methodName(ParamType)
        String methodQualifiedName;

        PsiClass aClass = sutMethod.getContainingClass();
        String className = aClass == null ? "" : aClass.getQualifiedName();
        methodQualifiedName = className == null ? "" : className;
        if (methodQualifiedName.length() != 0) methodQualifiedName += "#";
        methodQualifiedName += sutMethod.getName() + "(";
        PsiParameter[] parameters = sutMethod.getParameterList().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            PsiParameter parameter = parameters[i];
            if (i != 0) methodQualifiedName += ", ";
            methodQualifiedName += parameter.getType().getCanonicalText();
        }
        methodQualifiedName += ")";

        //  replace <.*> by blanks, there is a better way :S
        methodQualifiedName = methodQualifiedName.replaceAll("<.*?>", "");


        //  get test method description

        String commentText = "/**\n" +

                "* @" + Constants.VERIFIES_DOC_TAG + " " + testDescription + "\n" +
                "*/";

        PsiDocTag docTag = elementFactory.createDocTagFromText("@see  " + methodQualifiedName);

        PsiComment psiComment = elementFactory.createCommentFromText(commentText, null);
        psiComment.add(docTag);

//        final JavaCodeStyleManager codeStyleManagerEx = JavaCodeStyleManager.getInstance(project);


        realTestMethod.addBefore(psiComment, realTestMethod.getFirstChild());

        PsiClassType fqExceptionName = JavaPsiFacade.getInstance(sutMethod.getProject())
                .getElementFactory().createTypeByFQClassName(
                        CommonClassNames.JAVA_LANG_EXCEPTION, GlobalSearchScope.allScope(sutMethod.getProject()));

        PsiClass exceptionClass = fqExceptionName.resolve();
        if (exceptionClass != null) {
            PsiUtil.addException(realTestMethod, exceptionClass);
        }

        //  add //TODO auto-generated comment in the body
        PsiComment fromText = elementFactory.createCommentFromText("//TODO auto-generated", null);
        realTestMethod.getBody().addBefore(fromText, null);
        return realTestMethod;
    }

    /**
     * It returns the expected name for this method, it could make use
     * of an strategy for naming, investigate it further
     *
     * @return
     */
    @Override
    @NotNull
    public String getExpectedNameForThisTestMethod(String sutMethodName, String description) {
        return generateGenericTestMethodName(sutMethodName, description);
    }
}
