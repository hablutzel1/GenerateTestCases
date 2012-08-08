package com.intellij.generatetestcases.actions;

import com.intellij.generatetestcases.model.BDDCore;
import com.intellij.generatetestcases.model.TestClass;
import com.intellij.generatetestcases.test.BaseTests;
import com.intellij.generatetestcases.testframework.JUnit3Strategy;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: JHABLUTZEL
 * Date: Dec 21, 2010
 * Time: 9:46:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateTestMethodsTest extends BaseTests {

    /**
     * @verifies process inmediately upper class if caret is at anonymous class
     * @see GenerateTestMethods#actionPerformed(com.intellij.openapi.actionSystem.AnActionEvent)
     */
    public void testActionPerformed_shouldProcessInmediatelyUpperClassIfCaretIsAtAnonymousClass() throws Exception {


        //  create class with anonymous declaration of class in a method
        PsiClass classWithAnonymousDeclaration = createClassFromTextInPackage(myProject, "public class A {\n" +
                "\n" +
                "    /**\n" +
                "     * @should test something\n" +
                "     */\n" +
                "    void b() {\n" +
                "        new Object() {\n" +
                "            @Override\n" +
                "            public String toString() {\n" +
                "                return super.toString();    //To change body of overridden methods use File | Settings | File Templates.\n" +
                "            }\n" +
                "        };\n" +
                "    }\n" +
                "\n" +
                "}", "A", comExamplePackage);


        //  call the generate test method action


        final PsiFile psiFile = classWithAnonymousDeclaration.getContainingFile();
        final VirtualFile file = psiFile.getVirtualFile();
        Document document = FileDocumentManager.getInstance().getDocument(file);

        EditorFactory editorFactory = EditorFactory.getInstance();
        final Editor editor = editorFactory.createEditor(document);
//        editor.

        DataContext dc = new DataContext() {
            @Nullable
            public Object getData(String id) {
                if (PlatformDataKeys.VIRTUAL_FILE_ARRAY.is(id)) return new VirtualFile[]{file};
                ;
                if (PlatformDataKeys.EDITOR.is(id)) return editor;
                if (PlatformDataKeys.PROJECT.is(id)) return myProject;
                if (LangDataKeys.PSI_FILE.is(id)) return psiFile;
                return null;
            }
        };


        //    place context caret inside anonymous declaration
        editor.getCaretModel().moveToOffset(classWithAnonymousDeclaration.getText().indexOf("toString"));

        AnAction action = ActionManager.getInstance().getAction("com.intellij.generatetestcases.actions.GenerateTestMethods");
        action.actionPerformed(new AnActionEvent(null, dc, "", action.getTemplatePresentation(), null, -1));


        //  assert something has been created for the inmediately upside class
        TestClass testClass = BDDCore.createTestClass(classWithAnonymousDeclaration, new JUnit3Strategy(myProject));
        assertThat(testClass.reallyExists(), is(true));

        //  release editor
        editorFactory.releaseEditor(editor);
    }


}
