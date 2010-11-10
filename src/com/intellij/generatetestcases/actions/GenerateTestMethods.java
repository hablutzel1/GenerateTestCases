package com.intellij.generatetestcases.actions;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.generatetestcases.BDDCore;
import com.intellij.generatetestcases.TestClass;
import com.intellij.generatetestcases.TestMethod;
import com.intellij.generatetestcases.ui.codeinsight.generation.PsiDocAnnotationMember;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.DirectoryChooser;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

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

        PsiClass psiClass = getSubjectClass(editor, dataContext);

        if (psiClass != null) {

            //  create test class for this psiClass
            final TestClass testClass = BDDCore.createTestClass(project, psiClass);

            ArrayList<ClassMember> array = new ArrayList<ClassMember>();

            List<TestMethod> methods = testClass.getAllMethods();

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
            List<ClassMember> selectedElements = chooser.getSelectedElements();

            if (selectedElements == null || selectedElements.size() == 0) {
                //  canceled or nothing selected
                return;
            }

            //  ensure if parent exists

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

                DirectoryChooser fileChooser = new DirectoryChooser(project);
                fileChooser.setTitle(IdeBundle.message("title.choose.destination.directory"));
                fileChooser.fillList(allTestRoots.toArray(new PsiDirectory[allTestRoots.size()]), null, project, "");
                // TODO only display if more than one source root
                fileChooser.show();

               final  PsiDirectory psiDirectory = fileChooser.isOK() ? fileChooser.getSelectedDirectory() : null;


                if (psiDirectory != null) {
                    //  create in specified test root
                    // TODO run in write action together with method generation

                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        public void run() {
                            testClass.create(psiDirectory);
                        }

                    });


                } else {
                    //  just cancel
                    return;

                }


            }

            //  if backing test class exists, just create the methods in the same

            for (ClassMember selectedElement : selectedElements) {
                if (selectedElement instanceof PsiDocAnnotationMember) {
                    PsiDocAnnotationMember member = (PsiDocAnnotationMember) selectedElement;
                    final TestMethod testMethod = member.getTestMethod();
                    if (!testMethod.reallyExists()) {
                        //  para cada uno de los seleccionados llamar a create

                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
                            public void run() {
                                testMethod.create();
                            }
                        });

                    }
                }
            }


            // TODO if something has been created jump to the last created test method

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
