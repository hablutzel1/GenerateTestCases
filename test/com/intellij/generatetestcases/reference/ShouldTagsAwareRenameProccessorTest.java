package com.intellij.generatetestcases.reference;

import com.intellij.generatetestcases.test.BaseTests;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by jhe
 * Time: 12:08
 */
public class ShouldTagsAwareRenameProccessorTest extends BaseTests {


    /**
     * @verifies create ShouldTagRenameDialog instead of RenameDialog when element is a TestMethod got from a (at)should tag reference
     * @see ShouldTagsAwareRenameProccessor#createRenameDialog(com.intellij.openapi.project.Project, com.intellij.psi.PsiElement, com.intellij.psi.PsiElement, com.intellij.openapi.editor.Editor)
     */
    public void testCreateRenameDialog_shouldCreateShouldTagRenameDialogInsteadOfRenameDialogWhenElementIsATestMethodGotFromAAtshouldTagReference() throws Exception {


        //TODO auto-generated
        Assert.fail("Not yet implemented");
    }
}
