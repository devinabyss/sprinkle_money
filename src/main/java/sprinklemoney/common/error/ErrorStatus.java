package sprinklemoney.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorStatus {

    INVALID_SPRINKLE_TOKEN_VALUE(HttpStatus.BAD_REQUEST, 2000, "잘못된 뿌리기 식별 값 입니다."),
    NOT_EXIST_SPRINKLE(HttpStatus.NOT_FOUND, 3001, "유효한 뿌리기가 없습니다."),
    INVALID_SPRINKLE_STATUS(HttpStatus.BAD_REQUEST, 3002, "지급이 불가능한 뿌리기 입니다."),

    ALREADY_RECEIVED_SPRINKLE(HttpStatus.BAD_REQUEST, 4001, "이미 지급 받은 뿌리기 입니다."),
    NOT_ELIGIBLE(HttpStatus.FORBIDDEN, 4002, "뿌리기를 받을 자격이 없습니다."),


    SPRINKLE_TOKEN_GENERATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 6001, "뿌리기 토큰 생성에 실패했습니다."),

    UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, 9999, "내부 ");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;

    ErrorStatus(HttpStatus httpStatus, int errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }
}
