package com.intellij.generatetestcases.impl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@State(
        name = "GenerateTestCasesSettings",
        storages = {
                @Storage(id = "other", file = "$WORKSPACE_FILE$")}
)
public class GenerateTestCasesSettings implements PersistentStateComponent<GenerateTestCasesSettings> {


    public GenerateTestCasesSettings() {
        testFramework = "";
    }

    /**
     * Return an instance of GenerateTestCasesSettings which holds plugin preferences as testFramework
     *
     * @param project
     * @return
     */
    public static GenerateTestCasesSettings getInstance(Project project) {
        return project.getComponent(GenerateTestCasesSettings.class);
    }


    public String getTestFramework() {
        return testFramework;
    }

    public void setTestFramework(String testFramework) {
        this.testFramework = testFramework;
    }

    String testFramework;

    public GenerateTestCasesSettings getState() {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void loadState(GenerateTestCasesSettings state) {
        XmlSerializerUtil.copyBean(state, this);
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
