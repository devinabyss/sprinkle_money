package sprinklemoney.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private final int errorCode;
    private final String message;

    public BaseException(ErrorCode error){
        this.errorCode = error.getErrorCode();
        this.message = error.getMessage();
    }
}
