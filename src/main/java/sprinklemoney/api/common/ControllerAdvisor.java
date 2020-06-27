package sprinklemoney.api.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sprinklemoney.common.error.BaseException;

@Slf4j
@RestControllerAdvice
public class ControllerAdvisor extends BaseController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse> exception(Exception e) {
        log.error("## Critical Exception : {}, {}", e.getClass().getCanonicalName(), e.getMessage(), e);
        return fail(HttpStatus.INTERNAL_SERVER_ERROR, 9999, "Internal Server Error");
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomResponse> runtimeException(RuntimeException e) {
        log.warn("## Not Detailed Exception : {}, {}", e.getClass().getCanonicalName(), e.getMessage(), e);
        return fail(HttpStatus.INTERNAL_SERVER_ERROR, 8888, e.getMessage());
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CustomResponse> baseException(BaseException e) {
        log.info("## Known Exception : {}, {}", e.getErrorStatus(), e.getMessage(), e);
        log.info("## {}", e.getErrorStatus().getHttpStatus());
        return fail(e.getErrorStatus().getHttpStatus(), e.getErrorStatus().getErrorCode(), e.getMessage());
    }

}
