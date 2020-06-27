package sprinklemoney.domain.common.util;

import sprinklemoney.common.error.BaseException;
import sprinklemoney.common.error.ErrorStatus;

import java.security.SecureRandom;

public class StringHandleUtil {

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
            throw new BaseException(ErrorStatus.INVALID_INTERNAL_LOGIC_PARAMETER, "토큰 생성 중 잘못된 숫자가 사용되어 적절한 AsciiCode 로 전환할 수 없음. 숫자 : " + number);

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
