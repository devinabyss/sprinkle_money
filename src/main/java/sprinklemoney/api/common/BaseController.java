package sprinklemoney.api.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseController {

    protected ResponseEntity<CustomResponse> success() {
        return ResponseEntity.ok(CustomResponse.builder().success());
    }

    protected ResponseEntity<CustomResponse> success(Object result) {
        return ResponseEntity.ok(CustomResponse.builder().success(result));
    }

    protected ResponseEntity<CustomResponse> fail(HttpStatus httpStatus, int errorCode, String msg) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CustomResponse.builder().fail(errorCode, msg));
    }
}
