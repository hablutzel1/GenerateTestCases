package com.intellij.generatetestcases.actions;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.daemon.impl.quickfix.OrderEntryFix;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.testIntegration.TestFramework;
import com.intellij.ui.IdeBorderFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: JHABLUTZEL
 * Date: Dec 20, 2010
 * Time: 9:33:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class FixTestLibraryDialog extends DialogWrapper {

    Project project;
    Module module;
    JLabel myAddLibraryLabel;
//    JButton myAddLibraryButton;
    TestFramework myTestFramework;

    protected FixTestLibraryDialog(Project project, Module targetModule, TestFramework TestFramework) {
        super(project, true);
        this.project = project;
        this.module = targetModule;
        this.myTestFramework = TestFramework;
        initControls();
        setTitle("Required test library not available in module...");
        init();

    }

    private void initControls() {
//        myAddLibraryButton = new JButton(CodeInsightBundle.message("intention.create.test.dialog.fix.library"));
//        myAddLibraryButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//
//            }
//        });

        //  initialize the label
        // TODO include test library name
        myAddLibraryLabel = new JLabel("Add test library to module?");
    }

    @Override
    protected void doOKAction() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                OrderEntryFix.addJarToRoots(myTestFramework.getLibraryPath(), module, null);
            }
        });
        super.doOKAction();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(IdeBorderFactory.createBorder());
        panel.add(myAddLibraryLabel);
//        panel.add(myAddLibraryButton);
        return panel;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
