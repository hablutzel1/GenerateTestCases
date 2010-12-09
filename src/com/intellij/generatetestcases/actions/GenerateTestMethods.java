package com.intellij.generatetestcases.actions;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.generatetestcases.BDDCore;
import com.intellij.generatetestcases.GenerateTestCasesBundle;
import com.intellij.generatetestcases.TestClass;
import com.intellij.generatetestcases.TestMethod;
import com.intellij.generatetestcases.impl.GenerateTestCasesSettings;
import com.intellij.generatetestcases.testframework.JUnit3Strategy;
import com.intellij.generatetestcases.testframework.JUnit4Strategy;
import com.intellij.generatetestcases.ui.codeinsight.GenerateTestCasesConfigurable;
import com.intellij.generatetestcases.ui.codeinsight.generation.PsiDocAnnotationMember;
import com.intellij.history.LocalHistory;
import com.intellij.history.LocalHistoryAction;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.DirectoryChooser;
import com.intellij.ide.util.MemberChooser;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableGroup;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.options.ex.ProjectConfigurablesGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: JHABLUTZEL
 * Date: 20/10/2010
 * Time: 12:27:20 PM
 */
public class GenerateTestMethods extends AnAction {


    public GenerateTestMethods() {
        super("Generate Test Methods", "Generate test methods for current file", IconLoader.getIcon("/images/junitopenmrs.gif"));
    }


    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();

        //  to get the current project
        final Project project = PlatformDataKeys.PROJECT.getData(dataContext);
        Editor editor = getEditor(dataContext);

        //  prompt to choose the strategy if it haven't been choosen before
        GenerateTestCasesSettings casesSettings = GenerateTestCasesSettings.getInstance(project);
        String s = casesSettings.getTestFramework();
        if (StringUtils.isEmpty(s)) { //  it haven't been defined yet

            ConfigurableGroup[] group = new ConfigurableGroup[]{
                    new ProjectConfigurablesGroup(project, false) {

                        @Override
                        public Configurable[] getConfigurables() {
                            final Configurable[] extensions = project.getExtensions(Configurable.PROJECT_CONFIGURABLES);
                            List<Configurable> list = new ArrayList<Configurable>();
                            for (Configurable component : extensions) {
                                if (component instanceof GenerateTestCasesConfigurable) {
                                    list.add(component);
                                }
                            }
                            return list.toArray(new Configurable[0]);    //To change body of overridden methods use File | Settings | File Templates.
                        }
                    },

            };

            //  allow to define it as default
            ShowSettingsUtil.getInstance().showSettingsDialog(project, group);

            //  verify if something has been selected, if not just skip
            //  overwrite s variable
            s = casesSettings.getTestFramework();
            if (StringUtils.isEmpty(s)) {

                // TODO show dialog displaying that there is no framework selection

                return;
            }
        }

        PsiClass psiClass = getSubjectClass(editor, dataContext);

        if (psiClass != null) {
            //  create test class for this psiClass

            //  get the current test framework strategy from settings


            final TestClass testClass;

            if (s.equals("JUNIT3")) {
                testClass = BDDCore.createTestClass(project, psiClass, new JUnit3Strategy());
            } else {
                testClass = BDDCore.createTestClass(project, psiClass, new JUnit4Strategy());
            }

            ArrayList<ClassMember> array = new ArrayList<ClassMember>();

            List<TestMethod> methods = testClass.getAllMethods();

            // TODO if methods is empty show message dialog, or disable button to generate
            //  iterar sobre los metodos de prueba
            for (TestMethod method : methods) {

                if (!method.reallyExists()) {
                    //  crear a psiDocAnnotation para cada metodo no existente
                    PsiDocAnnotationMember member = new PsiDocAnnotationMember(method);
                    array.add(member);
                }
            }

            ClassMember[] classMembers = array.toArray(new ClassMember[array.size()]);
            MemberChooser<ClassMember> chooser = new MemberChooser<ClassMember>(classMembers, false, true, project);
            chooser.setTitle("Choose should annotations");
            chooser.setCopyJavadocVisible(false);
            chooser.show();
            final List<ClassMember> selectedElements = chooser.getSelectedElements();

            if (selectedElements == null || selectedElements.size() == 0) {
                //  canceled or nothing selected
                return;
            }

            //  ensure if parent exists
            PsiDirectory destinationRoot = null;
            boolean createParent = false;
            if (!testClass.reallyExists()) {

                //   otherwise allow to create in specified test sources root
                VirtualFile[] sourceRoots = ProjectRootManager.getInstance(project).getContentSourceRoots();

                //  get a list of all test roots
                final PsiManager manager = PsiManager.getInstance(project);
                List<PsiDirectory> allTestRoots = new ArrayList<PsiDirectory>(2);
                for (VirtualFile sourceRoot : sourceRoots) {
                    if (sourceRoot.isDirectory()) {
                        PsiDirectory directory = manager.findDirectory(sourceRoot);
                        allTestRoots.add(directory);
                    }
                }


                //  only display if more than one source root
                if (allTestRoots.size() > 1) {
                    DirectoryChooser fileChooser = new DirectoryChooser(project);
                    fileChooser.setTitle(IdeBundle.message("title.choose.destination.directory"));
                    fileChooser.fillList(allTestRoots.toArray(new PsiDirectory[allTestRoots.size()]), null, project, "");
                    fileChooser.show();
                    destinationRoot = fileChooser.isOK() ? fileChooser.getSelectedDirectory() : null;
                } else {
                    destinationRoot = allTestRoots.get(0);
                }


                if (destinationRoot != null) {
                    createParent = true;
                } else {
                    //  just cancel
                    return;
                }
            }


            //  if backing test class exists, just create the methods in the same
            //  para cada uno de los seleccionados llamar a create
            //  create an appropiate command name
            final String commandName = GenerateTestCasesBundle.message("plugin.GenerateTestCases.creatingtestcase", testClass.getClassUnderTest().getName());
            final boolean finalCreateParent = createParent;
            final PsiDirectory finalDestinationRoot = destinationRoot;
            new WriteCommandAction(project, commandName) {

                @Override
                protected void run(Result result) throws Throwable {
                    LocalHistoryAction action = LocalHistoryAction.NULL;
                    //  wrap this with error management
                    try {

                        action = LocalHistory.startAction(project, commandName);
                        if (finalCreateParent) {
                            testClass.create(finalDestinationRoot);
                        }
                        TestMethod lastTestMethod = null;
                        for (ClassMember selectedElement : selectedElements) {
                            if (selectedElement instanceof PsiDocAnnotationMember) {
                                PsiDocAnnotationMember member = (PsiDocAnnotationMember) selectedElement;
                                final TestMethod testMethod = member.getTestMethod();
                                lastTestMethod = testMethod;
                                if (!testMethod.reallyExists()) {
                                    testMethod.create();

                                }
                            }
                        }
                        //  if something has been created jump to the last created test method, this is 'lastTestMethod'
                        lastTestMethod.getBackingMethod().navigate(true);
                    } finally {
                        action.finish();
                    }

                }
            }.execute();


        }


    }


    private static String toCamelCase(String input) {
        assert input != null;
        if (input.length() == 0) {
            return ""; // is it ok?
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }


    public void update(Editor editor, Presentation presentation, DataContext dataContext) {
        //  si no hay ninguna clase en el editor se deberia desactivar la accion
        presentation.setEnabled(getSubjectClass(editor, dataContext) != null);
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        DataContext dataContext = e.getDataContext();
        Editor editor = getEditor(dataContext);
        if (editor == null) {
            presentation.setEnabled(false);
        } else {
            update(editor, presentation, dataContext);
        }
    }

    protected Editor getEditor(final DataContext dataContext) {
        return PlatformDataKeys.EDITOR.getData(dataContext);
    }

    @Nullable
    private static PsiClass getSubjectClass(Editor editor, DataContext dataContext) {
        PsiFile file = LangDataKeys.PSI_FILE.getData(dataContext);
        if (file == null) return null;

        int offset = editor.getCaretModel().getOffset();
        PsiElement context = file.findElementAt(offset);

        if (context == null) return null;

        PsiClass clazz = PsiTreeUtil.getParentOfType(context, PsiClass.class, false);
        if (clazz == null) {
            return null;
        }
        return clazz;
    }
}
