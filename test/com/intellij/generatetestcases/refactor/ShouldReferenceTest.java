package com.intellij.generatetestcases.refactor;

import com.intellij.generatetestcases.test.TestUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.ResolveTestCase;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by jhe
 * Time: 0:31
 */
public class ShouldReferenceTest extends ResolveTestCase {


    @Override
    protected String getTestDataPath() {
        return TestUtil.getPluginHomePath() + "/testData/resolve/";
    }


    /**
     * @verifies return only the range for the description without the javadoc tag
     * @see ShouldReference#getRangeInElement()
     */
    public void testGetRangeInElement_shouldReturnOnlyTheRangeForTheDescriptionWithoutTheJavadocTag() throws Exception {
//        create
        PsiReference psiReference = configureByFile("testmethod/X.java");
        addFileToClasspath("testmethod/XTest.java");
        assertThat(psiReference.getRangeInElement(), is(new TextRange(8, 11)));

    }

    private void addFileToClasspath(String anotherFile) throws Exception {
        final String fullPath = getTestDataPath() + anotherFile;
        final VirtualFile vFile = LocalFileSystem.getInstance().findFileByPath(fullPath.replace(File.separatorChar, '/'));
        assertNotNull("file not found", vFile);
        String fileText = StringUtil.convertLineSeparators(VfsUtil.loadText(vFile));
        final String fileName = vFile.getName();
        createFile(fileName, fileText);
    }
}
