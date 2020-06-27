package sprinklemoney.domain.common.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class StringHandleUtilTest {

    @Test
    public void testGenerateRandomString() {
        for (int i = 1; i < 100; i++) {
            String generated = StringHandleUtil.generateUriSafeRandomString(i);
            log.info("## Size Input : {}, Generated : {}", i, generated);
            Assert.assertEquals("aa", generated.length(), i);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testMapRandomNumberToAsciiCodeInvalidParameter() throws Throwable {
        Method mapRandomNumberToAsciiCode = getPrivateMapRandomNumberToAsciiCodeMethod();
        try {
            int number = (int) mapRandomNumberToAsciiCode.invoke(mapRandomNumberToAsciiCode, 0);
            log.info("## Result : {}", number);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testMapRandomNumberToAsciiCode() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method mapRandomNumberToAsciiCode = getPrivateMapRandomNumberToAsciiCodeMethod();
        int number = (int) mapRandomNumberToAsciiCode.invoke(mapRandomNumberToAsciiCode, 56);
        log.info("## Ascii Code Decimal : {}", number);
        Assert.assertEquals(126, number);

    }

    private Method getPrivateMapRandomNumberToAsciiCodeMethod() throws NoSuchMethodException {
        Method method = StringHandleUtil.class.getDeclaredMethod("mapRandomNumberToAsciiCode", Integer.TYPE);
        method.setAccessible(true);
        return method;
    }

}
