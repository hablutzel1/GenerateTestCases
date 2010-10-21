package intellij.adapter;

/**
 * User: JHABLUTZEL
 * Date: 21/10/2010
 * Time: 11:32:17 AM
 */
public interface JavaClass {

    String getFullyQualifiedName();

    
    JavaClass getInnerTypes();

    Object getBackingElement();
    
}
