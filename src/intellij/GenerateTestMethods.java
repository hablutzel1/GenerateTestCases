package intellij;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

/**
 * User: JHABLUTZEL
 * Date: 20/10/2010
 * Time: 12:27:20 PM
 */
public class GenerateTestMethods extends AnAction {


    public GenerateTestMethods() {
//        super(text, description, icon);
        super("Generate Test Methods", "Generate test methods for current file", IconLoader.getIcon("/images/junitopenmrs.gif"));
    }



    public void actionPerformed(AnActionEvent e) {


        //  obtener proyecto actual
        Project project = GenerateTestCasesProjectComponent.getProject();

        //  conseguir clase abierta en el editor

        DataContext dataContext = e.getDataContext();
        Editor editor = getEditor(dataContext);

        PsiClass psiClass = getSubjectClass(editor, dataContext);

        if (psiClass != null) {

            // TODO crear listado de metodos para la clase actualmente abierta en el editor
            PsiMethod[] methods = psiClass.getMethods();


            // TODO choose fields to initialize by constructor (action corresponding to generate constructors action) it uses a nice treeview
            // TODO crear structureViewModel e instanciar, bassarme en com.intellij.ide.actions.ViewStructureAction
            // return new FileStructureDialog(structureViewModel, editor, project, navigatable, alternativeDisposable, true);
//            new com.intellij.ide.util.MemberChooser();

            // TODO modificar el structureViewModel y crear un modelo de arbol con los metodos y anotaciones should debajo
            // TODo considerar el uso del MemberChooser



        } else {
                  //  si no hay ninguna clase en el editor se deberia desactivar la accion
        }


        // TODO crear primera interfaz del modelo (TestMethod) Utilizar patron adapter, considerar agregar metodo getBackingElement


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

        // must not be an interface
        return clazz.isInterface() ? null : clazz;
    }
}
