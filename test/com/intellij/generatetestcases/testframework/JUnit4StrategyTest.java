package com.intellij.generatetestcases.testframework;

import com.intellij.generatetestcases.test.BaseTests;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: JHABLUTZEL
 * Date: Dec 20, 2010
 * Time: 11:41:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class JUnit4StrategyTest extends BaseTests {
    /**
     * @verifies create the right test class
     * @see TestFrameworkStrategy#createBackingTestClass(com.intellij.psi.PsiClass, com.intellij.psi.PsiDirectory)
     */
    @Test
    public void testCreateBackingTestClass_shouldCreateTheRightTestClass() throws Exception {
        // create sut class
        PsiClass psiClass = createSutClass();
        // create test class
        PsiClass backingTestClass = new JUnit4Strategy(myProject).createBackingTestClass(psiClass, null);
        //  assert the name
        assertThat(backingTestClass.getName(), is("FooTest"));
        //  assert no extends exists
        PsiClassType[] referencedTypes = backingTestClass.getExtendsList().getReferencedTypes();
        assertThat("number of extends for test class", referencedTypes.length, is(0));
    }
}
