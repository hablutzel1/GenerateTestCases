package intellij.adapter;

/**
 * User: JHABLUTZEL
 * Date: 21/10/2010
 * Time: 11:35:10 AM
 */
public interface TestMethod extends  JavaMethod{

    TestClass getTestClass();
    
    JavaMethod getCorrespondingJavaMethod();

    String getDescription();

    int getDescriptionOffset();

    // TODO create some sort of region accesor
    int getShouldOffset();




}
