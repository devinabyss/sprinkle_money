package sprinklemoney.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_SPRINKLE_TOKEN_VALUE(2000, "잘못된 뿌리기 식별 값 입니다."),
    NOT_EXIST_SPRINKLE(3001, "유효한 뿌리기가 없습니다."),
    INVALID_SPRINKLE_STATUS(3002, "지급이 불가능한 뿌리기 입니다."),

    ALREADY_RECEIVED_SPRINKLE(4001, "이미 지급 받은 뿌리기 입니다."),
    NOT_ELIGIBLE(4002, "뿌리기를 받을 자격이 없습니다.")
    ;

    private final int errorCode;
    private final String message;

    ErrorCode(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
