package sprinklemoney.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorStatus {

    ALREADY_HAD_RUNNING_SPRINKLE(HttpStatus.BAD_REQUEST, 2001, "이미 뿌리기를 진행하고 있습니다."),

    NOT_EXIST_SPRINKLE(HttpStatus.NOT_FOUND, 3001, "유효한 뿌리기가 없습니다."),
    INVALID_SPRINKLE_TOKEN_VALUE(HttpStatus.BAD_REQUEST, 3002, "잘못된 뿌리기 식별 값 입니다."),

    ALREADY_RECEIVED_SPRINKLE(HttpStatus.BAD_REQUEST, 4001, "이미 지급 받은 뿌리기 입니다."),
    INVALID_SPRINKLE_STATUS(HttpStatus.BAD_REQUEST, 4002, "지급이 불가능한 뿌리기 입니다."),
    NOT_ELIGIBLE(HttpStatus.FORBIDDEN, 4003, "뿌리기를 받을 자격이 없습니다."),


    SPRINKLE_TOKEN_GENERATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 6001, "뿌리기 토큰 생성에 실패했습니다."),

    INVALID_INTERNAL_LOGIC_PARAMETER(HttpStatus.INTERNAL_SERVER_ERROR, 6201, "내부 작업 중 잘못된 값이 생성되었습니다."),

    HEAVY_LOAD_FAIL(HttpStatus.SERVICE_UNAVAILABLE, 8001, "시스템 과부하 영향으로 처리되지 못했습니다. 다시 시도해 주세요."),
    HTTP_REQUEST_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, 8101, "잘못된 요청입니다. 인터페이스 스펙을 확인하세요."),

    UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, 9999, "예상치 못한 내부 오류가 발생했습니다. 관리자에게 문의해주세요.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String message;

    ErrorStatus(HttpStatus httpStatus, int errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }
}
