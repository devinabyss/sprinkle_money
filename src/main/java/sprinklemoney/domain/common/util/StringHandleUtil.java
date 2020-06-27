package sprinklemoney.domain.common.util;

import java.security.SecureRandom;

public class StringHandleUtil {

    private static final int CHARACTER_BOUNDARY_FROM = 33;
    private static final int CHARACTER_BOUNDARY_TO = 126;

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateUriSafeRandomString(int size) {
        return secureRandom
                .ints(1, 56)
                .map(StringHandleUtil::mapRandomNumberToAsciiCode)
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private static int mapRandomNumberToAsciiCode(int number) {

        if (number < 1 || number > 56)
            throw new RuntimeException("Invalid Number Boundary To Mapping Mapping AsciiCode. Number : " + number);

        if (number < 27) return number + 64;

        if (number < 53) return number + 70; // 70 = 96 - 26

        return switch (number) {
            case 53 -> 45; // "-"
            case 54 -> 95; // "_"
            case 55 -> 46; // "."
            case 56 -> 126; // "~"
            default -> 65;
        };
    }


}
