package intellij.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static junit.framework.Assert.*;

/**
 * **********************
 * <p/>
 * MOVE THIS TEMPLATE SOMEWHERE ELSE AS UTILITY
 */

public class ExpectExceptionsExecutor {

    private ExpectExceptionsExecutor() {
    }

    public static  void execute(ExpectExceptionsTemplate e) {
        Class<? extends Throwable> aClass = e.getExpectedException();

        try {
            Method method = ExpectExceptionsTemplate.class.getMethod("doInttemplate");
            method.invoke(e);
        } catch (NoSuchMethodException e1) {


            throw new RuntimeException();
        } catch (InvocationTargetException e1) {
            //  determinar el tipo de excepcion y comparar con el esperado
            //  asegurarse de que la llamada al metodo justo arriba haya hgenerado la exccepcion esperada

            Throwable throwable = e1.getTargetException();
            if (!aClass.isAssignableFrom(throwable.getClass())) {
                //  assert false
                fail("Exception isn't the one expected");
            } else {
                assertTrue("Exception captured ", true);
                return;
            }
            ;


        } catch (IllegalAccessException e1) {
            throw new RuntimeException();
        }

        fail("No exception has been thrown");
    }




}
