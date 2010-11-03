package com.intellij.generatetestcases;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * User: JHABLUTZEL
 * Date: 20/10/2010
 * Time: 12:29:27 PM
 */
public class GenerateTestCasesProjectComponent implements ProjectComponent {


    /**
     * Returns the singleton project
     * 
     * @return
     */
    public static Project getProject() {
        return project;
    }

    private static  Project project;




    public GenerateTestCasesProjectComponent(Project project) {

        GenerateTestCasesProjectComponent.project = project;
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "GenerateTestCasesProjectComponent";
    }

    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
