package intellij.util;

/**
 * User: Jaime Hablutzel
 */
public class BddUtil {

    /**
     * @param originMethodName
     * @param shouldDescription
     * @return
     * @should create a appropiate name for the test method
     * @should fail if wrong args
     */
    public static String generateTestMethodName(String originMethodName, String shouldDescription) {
        StringBuilder builder = new StringBuilder(originMethodName
                + "_should");
        String[] tokens = shouldDescription.split("\\s+");
        for (String token : tokens) {
            char[] allChars = token.toCharArray();
            StringBuilder validChars = new StringBuilder();
            for (char validChar : allChars) {
                if (Character.isJavaIdentifierPart(validChar)) {
                    validChars.append(validChar);
                }
            }
            builder.append(toCamelCase(validChars.toString()));
        }
        return builder.toString();
    }

    private static String toCamelCase(String input) {
        assert input != null;
        if (input.length() == 0) {
            return ""; // is it ok?
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

}
