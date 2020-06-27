package sprinklemoney.common.error;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final ErrorStatus errorStatus;

    public BaseException() {
        super(ErrorStatus.UNKNOWN.getMessage());
        this.errorStatus = ErrorStatus.UNKNOWN;
    }

    public BaseException(ErrorStatus error) {
        super(error.getMessage());
        this.errorStatus = error;
    }

    public static class TokenKeyGenerationFailedException extends BaseException {
        public TokenKeyGenerationFailedException() {
            super(ErrorStatus.SPRINKLE_TOKEN_GENERATION_FAIL);
        }
    }
}
