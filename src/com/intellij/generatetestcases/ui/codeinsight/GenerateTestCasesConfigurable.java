package com.intellij.generatetestcases.ui.codeinsight;

import com.intellij.generatetestcases.model.GenerateTestCasesSettings;
import com.intellij.generatetestcases.testframework.SupportedFrameworks;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Jaime Hablutzel
 */
public class GenerateTestCasesConfigurable extends BaseConfigurable implements SearchableConfigurable {


    private final Project myProject;


    private MyComponent myComponent;
    private static final String EMPTY_STRING = "";


    public GenerateTestCasesConfigurable(Project myProject) {
        this.myProject = myProject;
    }

    @Override
    public String getId() {
        return getDisplayName();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Runnable enableSearch(String option) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Generate Test Cases";
    }

//    @Override
//    public Icon getIcon() {
//        return IconLoader.getIcon("/images/junitopenmrs.gif");
//
//    }

    @Override
    public String getHelpTopic() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JComponent createComponent() {
        myComponent = new MyComponent();

        List<String> strings = new ArrayList<String>();
        strings.add("-");
        SupportedFrameworks[] frameworkses = SupportedFrameworks.values();
        for (SupportedFrameworks frameworkse : frameworkses) {
            strings.add(frameworkse.toString());
        }

        DefaultComboBoxModel aModel = new DefaultComboBoxModel(strings.toArray());

        GenerateTestCasesSettings casesSettings = GenerateTestCasesSettings.getInstance(myProject);


        String testFramework = casesSettings.getTestFramework();
        if (!testFramework.equals(EMPTY_STRING)) {
            aModel.setSelectedItem(testFramework);
        }

//         establecer el modelo del combo
        myComponent.myLafComboBox.setModel(aModel);

        //  devolver e panel contenido por el formulario
        return myComponent.myPanel;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void apply() throws ConfigurationException {

        //  get settings holder
        GenerateTestCasesSettings casesSettings = GenerateTestCasesSettings.getInstance(myProject);
        //  persist currently selected test framework
        String s = myComponent.myLafComboBox.getSelectedItem().toString(); 
        if (!s.equals("-")) {
            casesSettings.setTestFramework(s);
        } else {
             casesSettings.setTestFramework(EMPTY_STRING);
        }

    }

    @Override
    public void reset() {

//        //   disable any test framework, put it in nothing selected
//        myComponent.myLafComboBox.setSelectedItem("-");
//        GenerateTestCasesSettings casesSettings = GenerateTestCasesSettings.getInstance(myProject);
//        casesSettings.setTestFramework("");

    }

    @Override
    public boolean isModified() {
        GenerateTestCasesSettings casesSettings = GenerateTestCasesSettings.getInstance(myProject);
        String o = (String) myComponent.myLafComboBox.getSelectedItem();
        String s = casesSettings.getTestFramework();
        if (o.equals("-")) {
               return !s.equals(EMPTY_STRING);
        } else {
            return !o.equals(s);
        }
    }


    @Override
    public void disposeUIResources() {
    }

    private static class MyComponent {

        private JComboBox myLafComboBox;
        private JPanel myPanel;
    }

}
