package com.intellij.generatetestcases.test;

/**
* User: JHABLUTZEL
* Date: 21/10/2010
* Time: 12:08:05 PM
*/
public interface ExpectExceptionsTemplate<T extends Throwable> {


    /**
     * Specify the type of exception that doInttemplate is expected to throw
     * @return
     */
    Class<T> getExpectedException();


    /**
     * Execute risky code inside this method
     * TODO specify expected exception using an annotation
     */
    public void doInttemplate();

}
