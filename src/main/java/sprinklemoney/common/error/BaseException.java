package sprinklemoney.common.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import sprinklemoney.domain.money.entity.Sprinkle;
import sprinklemoney.domain.money.entity.SprinkleDistribution;

@Getter
public class BaseException extends NestedRuntimeException {
    private final ErrorStatus errorStatus;

    public BaseException(ErrorStatus error) {
        super(error.getMessage());
        this.errorStatus = error;
    }

    @Builder
    public BaseException(ErrorStatus error, @Nullable String message, @Nullable Throwable cause) {
        super(StringUtils.isEmpty(message) ? error.getMessage() : message, cause);
        this.errorStatus = error;
    }

    public static class TokenKeyGenerationFailedException extends BaseException {
        public TokenKeyGenerationFailedException() {
            super(ErrorStatus.SPRINKLE_TOKEN_GENERATION_FAIL);
        }
    }

    public static class DistributionReceiveHeavyLoadException extends BaseException {
        private final Sprinkle sprinkle;
        private final SprinkleDistribution distribution;

        public DistributionReceiveHeavyLoadException(Sprinkle sprinkle, SprinkleDistribution distribution, Throwable e) {
            super(ErrorStatus.HEAVY_LOAD_FAIL, "특정 뿌리기에 분배 요청이 집중되어 분배가 실패 했습니다.", e);
            this.sprinkle = sprinkle;
            this.distribution = distribution;
        }
    }
}
