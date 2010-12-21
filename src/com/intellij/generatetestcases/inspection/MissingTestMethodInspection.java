package com.intellij.generatetestcases.inspection;

import com.intellij.codeInspection.*;
import com.intellij.generatetestcases.BDDCore;
import com.intellij.generatetestcases.TestClass;
import com.intellij.generatetestcases.TestMethod;
import com.intellij.generatetestcases.impl.GenerateTestCasesSettings;
import com.intellij.generatetestcases.impl.TestMethodImpl;
import com.intellij.generatetestcases.util.BddUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This inspection will search for clases not tested yet, and it will inspect should annotations
 * without a test method created
 */
public class MissingTestMethodInspection extends BaseJavaLocalInspectionTool {
    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "BDD";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Unused Should Annotations";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "UnusedShould";
    }


    /**
     *
     * @param aClass
     * @param manager
     * @param isOnTheFly
     * @return
     * @should create problem for classes without backing class
     * @should create problem for should annotations without test methods
     */
    @Override
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {

        if (aClass instanceof PsiAnonymousClass) {
                   return null;
        }

        Project project = aClass.getProject();
        String testFramework = GenerateTestCasesSettings.getInstance(project).getTestFramework();
        if (StringUtils.isEmpty(testFramework)) {
            return null;
        }

        //  create TestClass for current class
        TestClass testClass = BDDCore.createTestClass(project, aClass, BddUtil.getStrategyForFramework(project, testFramework));


        //  highlight warning should cover test class name
        //  if test class doesn't exists place warning at class level
        if (!testClass.reallyExists()) {
            //  create warning
            return new ProblemDescriptor[]{
                    manager.createProblemDescriptor(testClass.getClassUnderTest().getNameIdentifier(), "Missing Test Class",
                            isOnTheFly, LocalQuickFix.EMPTY_ARRAY, ProblemHighlightType.GENERIC_ERROR_OR_WARNING)};
        }

        List<ProblemDescriptor> result = new ArrayList<ProblemDescriptor>();

        //  if test class exists place warning at javadoc tags level
        List<TestMethod> methods = testClass.getAllMethods();



        for (TestMethod method : methods) {
            if (!method.reallyExists()) {
                //  add warning
                // TODO highlight should cover all should annotation, but no whitespace
                result.add(manager.createProblemDescriptor(((TestMethodImpl) method).getBackingTag(),
                        "Missing test method for should annotation", isOnTheFly, LocalQuickFix.EMPTY_ARRAY, ProblemHighlightType.GENERIC_ERROR_OR_WARNING));
            }
        }
        
        // TODO create fix for this problem

        //        return
        return result.toArray(new ProblemDescriptor[result.size()]);
    }
}
