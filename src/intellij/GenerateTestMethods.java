package intellij;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;

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

        // TODO conseguir clase abierta en el editor

        DataContext dataContext = e.getDataContext();
        Editor editor = getEditor(dataContext);

        Document document = editor.getDocument();
        // TODO crear listado de metodos para la clase actualmente abierta en el editor

        // TODO si no hay ninguna clase en el editor se deberia desactivar la accion

        // TODO crear primera interfaz del modelo (TestMethod) Utilizar patron adapter

    }

    protected Editor getEditor(final DataContext dataContext) {
        return PlatformDataKeys.EDITOR.getData(dataContext);
    }
}
