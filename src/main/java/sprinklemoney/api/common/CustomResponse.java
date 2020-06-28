package sprinklemoney.api.common;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomResponse {

    private Header header;
    private Object result;

    public static Builder builder() {
        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder {
        private static final int SUCCESS_CODE = 0;
        private static final String SUCCESS_MSG = "Success";

        public CustomResponse success() {
            return new CustomResponse(new Header(SUCCESS_CODE, SUCCESS_MSG), null);
        }

        public CustomResponse success(Object result) {
            return new CustomResponse(new Header(SUCCESS_CODE, SUCCESS_MSG), result);
        }

        public CustomResponse fail(int errorCode, String message) {
            return new CustomResponse(new Header(errorCode, message), null);
        }

    }


    @Getter
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Header {
        private final int resultCode;
        private final String message;
    }
}
