package intellij.adapter;

import java.net.URL;

/**
 * User: JHABLUTZEL
 * Date: 21/10/2010
 * Time: 11:29:32 AM
 */
public interface JavaFile {

    String getSource();
    
    URL getFullPath();
}
