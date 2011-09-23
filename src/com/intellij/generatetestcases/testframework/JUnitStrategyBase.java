package com.intellij.generatetestcases.testframework;

import com.intellij.generatetestcases.util.*;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import com.intellij.testIntegration.TestFramework;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * User: Jaime Hablutzel
 */
public abstract class JUnitStrategyBase implements TestFrameworkStrategy {

    private PsiElementFactory elementFactory;

    protected JUnitStrategyBase(Project project) {
        this.project = project;
    }

    private Project project;


    @Override
    public PsiMethod findBackingTestMethod(PsiClass testClass, PsiMethod sutMethod, String testDescription) {
        //  resolve (find) backing test method in test class
        String nombreMetodoDePrueba = getExpectedNameForThisTestMethod(sutMethod.getName(), testDescription);
//        PsiMethod backingMethod = null;
//        PsiClass parent = testClass.getContainingClass();

        // TODO get the test class
        PsiMethod[] byNameMethods = testClass.findMethodsByName(nombreMetodoDePrueba, false);
        if (byNameMethods.length > 0) {
            return byNameMethods[0];
        }

        return null;
    }

    @NotNull
    public abstract String getExpectedNameForThisTestMethod(@NotNull String sutMethodName, @NotNull String description);


    /**
     * @param testClass
     * @param sutMethod
     * @param testDescription @return
     * @return
     * @should create a junit test method with the expected body and javadoc and verify class structure
     * @should manage appropiately existence of multiple Assert's imports
     * @should manage appropiately any condition of the backing test class (imports, existing methods, modifiers, etc)
     * @should create test method even with broken references if test libraries aren't available
     */
    @Override
    public PsiMethod createBackingTestMethod(PsiClass testClass, PsiMethod sutMethod, String testDescription) {

        Project project = sutMethod.getProject();
        elementFactory = JavaPsiFacade.getElementFactory(project);
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
        //  add junit 4 Test annotation


        afterCreatingMethod(project, realTestMethod);

        PsiClassType fqExceptionName = JavaPsiFacade.getInstance(project)
                .getElementFactory().createTypeByFQClassName(
                        CommonClassNames.JAVA_LANG_EXCEPTION, GlobalSearchScope.allScope(project));

        PsiClass exceptionClass = fqExceptionName.resolve();
        if (exceptionClass != null) {
            PsiUtil.addException(realTestMethod, exceptionClass);
        }

        //  add //TODO auto-generated comment in the body
        PsiComment fromText = elementFactory.createCommentFromText("//TODO auto-generated", null);
        PsiElement todoComment = realTestMethod.getBody().addBefore(fromText, null);

        //  add org.junit.Assert.fail("Not yet implemented");,

        PsiJavaFile javaFile = (PsiJavaFile) testClass.getContainingFile();

        boolean assertImportExists = javaFile.getImportList().findSingleImportStatement("Assert") == null ? false : true;
        boolean makeFullQualified = false;

        // TODO if Assert exists and is different to both of previous, place fully qualified statement
        if (assertImportExists) {

            //  verify if junit.framework.Assert exists, if it does do not import org.junit.Assert
            //  verify import for Assert before actually importing


            //  replace it by ((PsiJavaFile) testClass.getContainingFile()).getImportList()
            PsiImportStatement bei = javaFile.getImportList().findSingleClassImportStatement("org.junit.Assert");
//            List<PsiImportStatementBase> basicExpectedImport = BddUtil.findImportsInClass(testClass, );

            PsiImportStatement oei = javaFile.getImportList().findSingleClassImportStatement("junit.framework.Assert");
//            List<PsiImportStatementBase> otherExpectedImport = BddUtil.findImportsInClass(testClass, "");

            if (bei == null && oei == null) {
                // then it is a weird class
                makeFullQualified = true;
            }


        } else {
            //  create basic org.junit.Assert
            addBasicImport(testClass, project);
        }


        // org.junit.Assert
        PsiStatement statement;

        if (makeFullQualified) {
            statement = elementFactory.createStatementFromText(getFrameworkBasePackage() + ".Assert.fail(\"Not yet implemented\");", null);
        } else {
            statement = elementFactory.createStatementFromText("Assert.fail(\"Not yet implemented\");", null);
        }

        realTestMethod.getBody().addAfter(statement, todoComment);

        return realTestMethod;
    }

    protected void afterCreatingMethod(Project project, PsiMethod realTestMethod) {

    }

    private void addBasicImport(PsiClass testClass, Project project) {

        String s = getFrameworkBasePackage();
        String text = "import " + s + ".Assert;";
        String ext = StdFileTypes.JAVA.getDefaultExtension();
        @NonNls String fileName = "_Dummy_." + ext;
        FileType type = StdFileTypes.JAVA;
        PsiJavaFile javaFile = (PsiJavaFile) PsiFileFactory.getInstance(project).createFileFromText(type, fileName, text, 0, text.length());
        PsiImportStatement statement = javaFile.getImportList().getImportStatements()[0];
        PsiImportList list = ((PsiJavaFile) testClass.getContainingFile()).getImportList();
        list.add(statement);
    }

    protected abstract String getFrameworkBasePackage();

    private static final String TEST_CLASS_SUFFIX = "Test";

    /**
     * It will return the test framework descriptor for the specified test framework, this descriptor will give us information
     * like this:
     *
     * @return
     */
    public abstract TestFramework getTestFramework();


    @Override
    public PsiClass findBackingPsiClass(PsiClass sutClass) {

        if (sutClass instanceof PsiAnonymousClass) {
            return null;
        }
        String packageName = BddUtil.getPackageName(sutClass);
        String testClassName = getCandidateClassName(sutClass);


        String fullyQualifiedTestClass = packageName == null ? testClassName : packageName + "." + testClassName;
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
    public String getCandidateClassName(PsiClass sutClass) {
        //  build the test class name
        //  get the sut class name
        String s = sutClass.getName();
        String testClassName = s + TEST_CLASS_SUFFIX;
        return testClassName;
    }


    @Override
    public PsiClass createBackingTestClass(PsiClass sutClass, PsiDirectory sourceRoot) {

        PsiClass ret;
        if (sourceRoot == null || sourceRoot.equals(sutClass.getContainingFile().getParent())) {
            //  create the test class in the same source root

            //  get psiDirectory for sut class
            PsiElement parentPackage = sutClass.getScope().getParent();
            // get test class name
            String testClassName = getCandidateClassName(sutClass);
            //  check
            JavaDirectoryService.getInstance().checkCreateClass((PsiDirectory) parentPackage, testClassName);
            //  create
            ret = JavaDirectoryService.getInstance().createClass((PsiDirectory) parentPackage, testClassName, "Class");

        } else {

            //  create the test class in the specified source root
            // get test class name
            String testClassName = getCandidateClassName(sutClass);


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

    protected void afterCreatingClass(Project project, PsiClass backingTestClass) {

    }

}
