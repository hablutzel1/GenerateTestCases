package com.intellij.generatetestcases.actions;

import com.intellij.generatetestcases.test.BaseTests;
import junit.framework.Assert;
import junit.framework.TestCase;

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

        // TODO create class with anonymous declaration of class in a method
        createClassFromTextInPackage(myProject, "", "Foo", comExamplePackage);
        
        // TODO place context caret inside anonymous declaration

        // TODO call the generate test method action

        // TODO assert something has been created for the inmediately upside class

        //TODO auto-generated
        Assert.fail("Not yet implemented");
    }
}
