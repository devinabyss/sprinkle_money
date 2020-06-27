package sprinklemoney.api.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sprinklemoney.common.error.BaseException;
import sprinklemoney.common.error.ErrorStatus;

@Slf4j
@RestControllerAdvice
public class ControllerAdvisor extends BaseController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse> exception(Exception e) {
        ErrorStatus errorStatus = ErrorStatus.UNKNOWN;
        log.error("## Critical Exception : {}, {}, {}", errorStatus, e.getClass().getCanonicalName(), e.getMessage(), e);
        return fail(errorStatus.getHttpStatus(), errorStatus.getErrorCode(), errorStatus.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomResponse> runtimeException(RuntimeException e) {
        ErrorStatus errorStatus = ErrorStatus.UNKNOWN;
        log.error("## Not Detailed Exception : {}, {}, {}", errorStatus, e.getClass().getCanonicalName(), e.getMessage(), e);
        return fail(errorStatus.getHttpStatus(), 9998, errorStatus.getMessage());
    }

    @ExceptionHandler(CannotAcquireLockException.class)
    public ResponseEntity<CustomResponse> cannotAcquireLockException(CannotAcquireLockException e) {
        ErrorStatus errorStatus = ErrorStatus.HEAVY_LOAD_FAIL;
        log.error("## DB Transaction Heavy Load Tried : {}, {}, {}", errorStatus, e.getClass().getCanonicalName(), e.getMessage(), e);
        return fail(errorStatus.getHttpStatus(), errorStatus.getErrorCode(), errorStatus.getMessage());
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CustomResponse> baseException(BaseException e) {
        log.info("## Known Exception : {}, {}", e.getErrorStatus(), e.getMessage(), e);
        log.info("## {}", e.getErrorStatus().getHttpStatus());
        return fail(e.getErrorStatus().getHttpStatus(), e.getErrorStatus().getErrorCode(), e.getMessage());
    }

}
