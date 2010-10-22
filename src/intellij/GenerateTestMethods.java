package intellij;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.util.PsiTreeUtil;
import intellij.ui.codeinsight.generation.PsiDocAnnotationMember;
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

            //  crear listado de metodos para la clase actualmente abierta en el editor
            PsiMethod[] methods = psiClass.getMethods();

            //  choose fields to initialize by constructor (action corresponding to generate constructors action) it uses a nice treeview

            // TODO   crear un modelo de arbol con los metodos y anotaciones should debajo
            // TODO extender ClassMember para que soporte anotaciones should


            ArrayList<ClassMember> array = new ArrayList<ClassMember>();
            //  iterar sobre los metodos
            for (PsiMethod method : methods) {


                //  iterar sobre los comentarios del javadoc
                PsiDocComment comment = method.getDocComment();
                if (comment == null) { // if no doc comment
                    continue;
                }
                PsiDocTag[] tags = comment.getTags();
                for (PsiDocTag tag : tags) {

                    // TODO comprobar si este tag no existe en la clase de pruebas

                    //  conseguir text del tag y ponerlo en una sola linea

                    //  comprobar que el tag sea del tipo should
                    if (tag.getName().equals("should")) {
                        //  usar cada metodo como padre del doc annotation
                        final StringBuilder description = new StringBuilder();

                        PsiElement[] dataElements = tag.getDataElements();
                        boolean isFirst = true;
                        for (PsiElement dataElement : dataElements) {
                            description.append(dataElement.getText());
                            // TODO get the description taking into account the whitespaces
                            if (isFirst) {
                                description.append(" ");
                            }
                            isFirst = false;
                        }

                        PsiDocAnnotationMember member = new PsiDocAnnotationMember(tag, description.toString(), method);
                        array.add(member);
                    }
                }


            }

            ClassMember[] classMembers = array.toArray(new ClassMember[array.size()]);
            MemberChooser<ClassMember> chooser = new MemberChooser<ClassMember>(classMembers, false, true, project);
            chooser.setTitle("Choose should annotations");
            chooser.setCopyJavadocVisible(false);
            chooser.show();
            List<ClassMember> selectedElements = chooser.getSelectedElements();

            // TODO generar clase de pruebas con metodos correspondientes a estos tags

            //  obtener paquete de la clase actual
            PsiElement classScope = psiClass.getScope();
            if (classScope instanceof PsiJavaFile) {
                
                PsiJavaFile javaFileForClass = (PsiJavaFile) classScope;
                PsiDirectory psiDirectory = javaFileForClass.getParent();

                // obtener nombre de clase actual
                String nombreClaseDeOrigen = psiClass.getName();

                //  crear clase con sufijo Test
                String nombreClaseDePruebas = nombreClaseDeOrigen + "Test";

                // TODO dar al usuario la opcion de escoger la carpeta raiz del classpath

                // TODO verificar si la clase de prueba ya existe antes de crearla
                //  efectivamente crear la clase
                PsiClass claseDePruebas = JavaDirectoryService.getInstance().createClass(psiDirectory, nombreClaseDePruebas, "Class");


   
                //  generar metodo para cada uno de estos
                for (ClassMember classMember : selectedElements) {
                    //  ascender classMember a la implementacion
                    PsiDocAnnotationMember anotacionShould = (PsiDocAnnotationMember) classMember;

                    //  generar el nombre para el metodo
                    String shouldDescription = anotacionShould.getText();
                    PsiElement element = anotacionShould.getElement();
                    String nombreMetodoDeOrigen = ((PsiMethod) element.getParent().getParent()).getName();
                    String nombreMetodoDePrueba = generateTestMethodName(nombreMetodoDeOrigen, shouldDescription);
                    // TODO verificar si el metodo que se desea crear no existe

                    //  crear metodo con retorno void y este nombre generado en claseDePruebas
                    //claseDePruebas.add
                    PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);

                    // TODO revisar TestNG y permitir que el usuario escoja el framework de pruebas unitarias

                    // TODO revisar la existencia del framework, de no existir mostrar alerta

                    // TODO crear javadoc adecuado para cada uno de los metodos de prueba

                    // TODO importar clases de junit necesarias

                    // TODO agregar anotacion de junit
                    PsiMethod metodoDePrueba = elementFactory.createMethod(nombreMetodoDePrueba, PsiType.VOID);

                    // TODO correr esto dentro de un write-action   ( Write access is allowed inside write-action only )
                    claseDePruebas.add(metodoDePrueba);
                }


            }

        } else {
            //  si no hay ninguna clase en el editor se deberia desactivar la accion
        }


        // TODO crear primera interfaz del modelo (TestMethod) Utilizar patron adapter, considerar agregar metodo getBackingElement


    }


    	private String generateTestMethodName(String originMethodName, String shouldDescription) {

            // TODO mover a una clase estatica y validar argumentos

		StringBuilder builder = new StringBuilder(originMethodName
				+ "_should");
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
